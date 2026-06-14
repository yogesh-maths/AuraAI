package com.yogesh.auraai.presentation.chat

import com.yogesh.auraai.core.network.NetworkMonitor
import com.yogesh.auraai.data.remote.GeminiService
import com.yogesh.auraai.domain.model.Message
import com.yogesh.auraai.domain.model.MessageRole
import com.yogesh.auraai.domain.model.SyncStatus
import com.yogesh.auraai.domain.repository.ConversationRepository
import com.yogesh.auraai.domain.repository.MessageRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private lateinit var geminiService: GeminiService
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var conversationRepository: ConversationRepository
    private lateinit var messageRepository: MessageRepository
    private lateinit var networkMonitor: NetworkMonitor

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        conversationRepository = mockk(relaxed = true)
        messageRepository = mockk(relaxed = true)
        networkMonitor = mockk(relaxed = true)
        geminiService = mockk()
        every { networkMonitor.isOnline } returns MutableStateFlow(true)
        every { messageRepository.observeMessages("conv-1") } returns flowOf(emptyList())
        coEvery {
            geminiService.ask(any())
        } returns "Test response"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun sendMessage_clearsInputAndCallsRepository() = runTest {
        coEvery { messageRepository.sendUserMessage("conv-1", "Hello") } returns "msg-1"

        val viewModel = ChatViewModel(
            conversationId = "conv-1",
            conversationRepository = conversationRepository,
            messageRepository = messageRepository,
            geminiService = geminiService,
            networkMonitor = networkMonitor,
        )

        viewModel.onInputChanged("Hello")
        viewModel.sendMessage()
        advanceUntilIdle()

        coVerify { messageRepository.sendUserMessage("conv-1", "Hello") }
        assertEquals("", viewModel.uiState.value.inputText)
    }

    @Test
    fun retryMessage_callsRepository() = runTest {
        coEvery { messageRepository.retryFailedMessage("msg-1") } returns Unit

        val viewModel = ChatViewModel(
            conversationId = "conv-1",
            conversationRepository = conversationRepository,
            messageRepository = messageRepository,
            geminiService = geminiService,
            networkMonitor = networkMonitor,
        )

        viewModel.retryMessage("msg-1")
        advanceUntilIdle()

        coVerify { messageRepository.retryFailedMessage("msg-1") }
    }
}
