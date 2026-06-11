package com.yogesh.auraai.presentation.conversations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.yogesh.auraai.core.network.NetworkMonitor
import com.yogesh.auraai.domain.repository.ConversationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConversationListViewModel(
    private val conversationRepository: ConversationRepository,
    networkMonitor: NetworkMonitor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationListUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ConversationListUiEvent>()
    val events = _events.asSharedFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                _uiState.update { it.copy(isOffline = !isOnline) }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val conversations = searchQuery
        .flatMapLatest { query ->
            conversationRepository.getConversations(query)
        }
        .cachedIn(viewModelScope)

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onNewChat(onCreated: (String) -> Unit) {
        viewModelScope.launch {
            val id = conversationRepository.createConversation()
            onCreated(id)
        }
    }

    fun onConversationClicked(id: String) {
        viewModelScope.launch {
            _events.emit(ConversationListUiEvent.OpenConversation(id))
        }
    }

    fun onDeleteConversation(id: String) {
        viewModelScope.launch {
            conversationRepository.deleteConversation(id)
            _events.emit(ConversationListUiEvent.ShowSnackbar("Conversation deleted"))
        }
    }

    fun onArchiveConversation(id: String) {
        viewModelScope.launch {
            conversationRepository.archiveConversation(id)
            _events.emit(ConversationListUiEvent.ShowSnackbar("Conversation archived"))
        }
    }
}
