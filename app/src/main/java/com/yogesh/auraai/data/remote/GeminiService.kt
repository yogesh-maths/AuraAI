package com.yogesh.auraai.data.remote




import com.google.ai.client.generativeai.GenerativeModel
import com.yogesh.auraai.BuildConfig
class GeminiService {

    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun ask(prompt: String): String {

        val response = model.generateContent(prompt)

        return response.text ?: "No response"
    }
}