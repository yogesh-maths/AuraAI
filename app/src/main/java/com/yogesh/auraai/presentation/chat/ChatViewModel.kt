package com.yogesh.auraai.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogesh.auraai.core.network.NetworkMonitor
import com.yogesh.auraai.domain.model.SyncStatus
import com.yogesh.auraai.domain.repository.ConversationRepository
import com.yogesh.auraai.domain.repository.MessageRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.yogesh.auraai.data.remote.GeminiService
import com.yogesh.auraai.domain.model.MessageRole

class ChatViewModel(
    private val conversationId: String,
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val geminiService: GeminiService,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ChatUiState(conversationId = conversationId),
    )
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ChatUiEvent>()
    val events = _events.asSharedFlow()

    val messages = messageRepository.observeMessages(conversationId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                _uiState.update { it.copy(isOffline = !isOnline) }
            }
        }
        viewModelScope.launch {
            val conversation = conversationRepository.getConversation(conversationId)
            if (conversation != null) {
                _uiState.update { it.copy(title = conversation.title) }
            }
        }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text, error = null) }
    }

    fun sendMessage() {
        val content = _uiState.value.inputText.trim()
        if (content.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true, inputText = "", error = null) }
            try {
                val messageId =
                    messageRepository.sendUserMessage(
                        conversationId,
                        content
                    )
                val historyMessages =
                    messages.value.takeLast(20)

                val history =
                    historyMessages.joinToString("\n") { message ->

                        val role =
                            if (message.role == MessageRole.USER)
                                "User"
                            else
                                "Assistant"

                        "$role: ${message.content}"
                    }

                val fullPrompt = """
You are AuraAI.

You were created by Yogesh Kumbhar.

Be helpful, friendly, and concise.

Conversation History:
$history

User: $content
""".trimIndent()
                val response =
                    geminiService.ask(fullPrompt)

                messageRepository.markAsSynced(messageId)

                messageRepository.insertAssistantMessage(
                    conversationId = conversationId,
                    content = response,
                    remoteId = null
                )
                val conversation = conversationRepository.getConversation(conversationId)
                if (conversation != null) {
                    _uiState.update { it.copy(title = conversation.title) }
                }
                _events.emit(ChatUiEvent.ScrollToBottom)
            } catch (e: Exception) {
                val errorMessage =
                    if (
                        e.message?.contains("503") == true ||
                        e.message?.contains("unavailable", true) == true ||
                        e.message?.contains("overloaded", true) == true
                    ) {
                        "⚠️ High traffic detected. Please wait a moment and try again."
                    } else {
                        e.message ?: "Failed to send message"
                    }

                _uiState.update {
                    it.copy(
                        inputText = content,
                        error = errorMessage
                    )
                }

                _events.emit(
                    ChatUiEvent.ShowSnackbar(errorMessage)
                )
            } finally {
                _uiState.update { it.copy(isSending = false) }
            }
        }
    }

    fun retryMessage(messageId: String) {
        viewModelScope.launch {
            try {
                messageRepository.retryFailedMessage(messageId)
                _events.emit(ChatUiEvent.ShowSnackbar("Retrying sync…"))
            } catch (e: Exception) {
                _events.emit(ChatUiEvent.ShowSnackbar(e.message ?: "Retry failed"))
            }
        }
    }

    fun updateTitle(title: String) {
        viewModelScope.launch {
            conversationRepository.updateTitle(conversationId, title.trim())
            _uiState.update { it.copy(title = title.trim()) }
        }
    }

    fun onBack() {
        viewModelScope.launch {
            _events.emit(ChatUiEvent.NavigateBack)
        }
    }

    fun hasFailedMessages(): Boolean = messages.value.any {
        it.syncStatus == SyncStatus.FAILED && it.role == com.yogesh.auraai.domain.model.MessageRole.USER
    }
}
