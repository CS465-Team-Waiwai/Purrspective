package com.example.purrspective

import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object GeminiHelper {

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        // this key is not supposed to be visible but I did this so we all can use it easier
        apiKey = "AIzaSyB_ZCvys4SQV9lu5pOZr0lrVCgkzv9N7wQ"
    )

    suspend fun sendMessage(prompt: String): String {
        return withContext(Dispatchers.IO) {
            val response = model.generateContent(prompt)
            response.text ?: "Error: no response"
        }
    }
}
