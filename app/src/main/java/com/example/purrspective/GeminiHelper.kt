package com.example.purrspective

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object GeminiHelper {

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        // the key isn't supposed to be visible but I'll put it here so it's easier for everyone to use
        apiKey = "AIzaSyB_ZCvys4SQV9lu5pOZr0lrVCgkzv9N7wQ"
    )

    fun sendMessageAsync(prompt: String, callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = try {
                model.generateContent(prompt)
            } catch (e: Exception) {
                null
            }

            val text = response?.text ?: "Oops, I couldn't generate a response."

            // Switch back to Main thread before calling back into Java/UI
            withContext(Dispatchers.Main) {
                callback(text)
            }
        }
    }
}
