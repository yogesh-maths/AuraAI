package com.yogesh.auraai.data.remote

import com.yogesh.auraai.data.remote.dto.ChatCompletionRequest
import com.yogesh.auraai.data.remote.dto.ChatCompletionResponse
import com.yogesh.auraai.data.remote.dto.ModelsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AiApiService {

    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Body request: ChatCompletionRequest,
    ): ChatCompletionResponse

    @GET("models")
    suspend fun listModels(): ModelsResponse
}
