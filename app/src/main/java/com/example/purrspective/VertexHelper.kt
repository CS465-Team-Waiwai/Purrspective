package com.example.purrspective

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object VertexHelper {

    fun askAsync(
        context: Context,
        prompt: String,
        callback: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val model = Firebase.ai(
                backend = GenerativeBackend.googleAI()
            ).generativeModel("gemini-2.5-flash")

            val response = try {
                model.generateContent(prompt)
            } catch (e: Exception) {
                Log.e("AI", "Generation failed", e)
                null
            }

            val text = response?.text ?: "Oops, I couldn't generate a response."

            // Switch back to main thread for UI update
            withContext(Dispatchers.Main) {
                callback(text)
            }
        }
    }
}
