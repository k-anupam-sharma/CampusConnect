package com.example.collisionengine.ui.conversation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.postgrest.from

class ConversationViewModel : ViewModel() {

    private val _suggestedMessage = MutableStateFlow("")
    val suggestedMessage: StateFlow<String> = _suggestedMessage.asStateFlow()

    private var currentReceiver = ""

    fun generateMessage(name: String, reason: String) {
        currentReceiver = name
        val decodedReason = try {
            java.net.URLDecoder.decode(reason, "UTF-8").replace("+", " ")
        } catch (e: Exception) {
            reason.replace("+", " ")
        }
        
        val greeting = if (name.startsWith("Dr.", ignoreCase = true) || name.startsWith("Prof.", ignoreCase = true)) {
            name
        } else {
            name.split(" ").firstOrNull() ?: name
        }

        val topTopics = decodedReason.split(Regex("[,;]+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString(" and ")

        var topicPhrase = if (topTopics.isNotBlank()) topTopics else decodedReason.take(50)
        if (topicPhrase.equals("None", ignoreCase = true) || topicPhrase.equals("Direct search match.", ignoreCase = true)) {
            topicPhrase = "your field"
        }

        val msg = "Hi $greeting,\n\nI came across your profile on Campus Connect regarding your work in $topicPhrase. I'm currently working on a related project and would love to connect, ask a quick question, and exchange insights!"
        _suggestedMessage.value = msg
    }

    fun updateMessage(newText: String) {
        _suggestedMessage.value = newText
    }

    fun sendMessageToSupabase() {
        val currentMessage = _suggestedMessage.value
        if (currentMessage.isBlank()) return
        
        viewModelScope.launch {
            try {
                val newMessage = com.example.collisionengine.data.models.SupabaseMessageInsert(
                    senderId = com.example.collisionengine.data.state.GlobalProfileState.name.value,
                    receiverId = currentReceiver,
                    content = currentMessage
                )
                com.example.collisionengine.data.network.SupabaseClient.client.from("messages").insert(newMessage)
            } catch (e: Exception) {
                android.util.Log.e("ConversationViewModel", "Error sending message", e)
            }
        }
    }
}
