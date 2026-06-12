package com.yogesh.auraai.core.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.yogesh.auraai.core.common.DefaultDispatcherProvider
import com.yogesh.auraai.core.common.DispatcherProvider
import com.yogesh.auraai.core.network.NetworkMonitor
import com.yogesh.auraai.data.local.AuraDatabase
import com.yogesh.auraai.data.local.preferences.UserPreferencesDataStore
import com.yogesh.auraai.data.remote.AiApiService
import com.yogesh.auraai.data.remote.interceptor.AuthInterceptor
import com.yogesh.auraai.data.repository.ConversationRepositoryImpl
import com.yogesh.auraai.data.repository.MessageRepositoryImpl
import com.yogesh.auraai.data.repository.SettingsRepositoryImpl
import com.yogesh.auraai.data.repository.SyncRepositoryImpl
import com.yogesh.auraai.data.repository.WorkManagerSyncScheduler
import com.yogesh.auraai.domain.repository.ConversationRepository
import com.yogesh.auraai.domain.repository.MessageRepository
import com.yogesh.auraai.domain.repository.SettingsRepository
import com.yogesh.auraai.domain.repository.SyncRepository
import com.yogesh.auraai.presentation.chat.ChatViewModel
import com.yogesh.auraai.presentation.conversations.ConversationListViewModel
import com.yogesh.auraai.presentation.onboarding.OnboardingViewModel
import com.yogesh.auraai.presentation.settings.SettingsViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.yogesh.auraai.data.remote.GeminiService
class AppContainer(context: Context) {

    private val applicationContext = context.applicationContext

    val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()

    val networkMonitor: NetworkMonitor = NetworkMonitor(applicationContext)

    private val database: AuraDatabase = Room.databaseBuilder(
        applicationContext,
        AuraDatabase::class.java,
        "aura_database",
    ).build()

    private val dataStore = UserPreferencesDataStore(applicationContext)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            AuthInterceptor {
                runBlocking { dataStore.settings.first().apiKey }
            },
        )
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            },
        )
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/v1/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val aiApiService: AiApiService = retrofit.create(AiApiService::class.java)
    val geminiService = GeminiService()
    val conversationRepository: ConversationRepository = ConversationRepositoryImpl(
        conversationDao = database.conversationDao(),
    )

    private val syncScheduler = WorkManagerSyncScheduler(applicationContext)

    val messageRepository: MessageRepository = MessageRepositoryImpl(
        messageDao = database.messageDao(),
        conversationDao = database.conversationDao(),
        conversationRepository = conversationRepository,
        syncScheduler = syncScheduler,
    )

    val syncRepository: SyncRepository = SyncRepositoryImpl(
        messageDao = database.messageDao(),
        conversationDao = database.conversationDao(),
        messageRepository = messageRepository,
        dataStore = dataStore,
        aiApiService = aiApiService,
    )

    val settingsRepository: SettingsRepository = SettingsRepositoryImpl(
        dataStore = dataStore,
        database = database,
        aiApiService = aiApiService,
    )

    val viewModelFactory = AuraViewModelFactory(this)
}

class AuraViewModelFactory(
    private val container: AppContainer,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ConversationListViewModel::class.java) ->
                ConversationListViewModel(
                    conversationRepository = container.conversationRepository,
                    networkMonitor = container.networkMonitor,
                ) as T

            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(
                    settingsRepository = container.settingsRepository,
                ) as T

            modelClass.isAssignableFrom(OnboardingViewModel::class.java) ->
                OnboardingViewModel(
                    settingsRepository = container.settingsRepository,
                ) as T

            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}

class ChatViewModelFactory(
    private val container: AppContainer,
    private val conversationId: String,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(
                conversationId = conversationId,
                conversationRepository = container.conversationRepository,
                messageRepository = container.messageRepository,
                geminiService = container.geminiService,
                networkMonitor = container.networkMonitor,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
