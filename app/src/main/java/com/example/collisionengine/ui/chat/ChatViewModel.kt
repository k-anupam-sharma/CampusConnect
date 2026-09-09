package com.example.collisionengine.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collisionengine.data.models.SupabaseChatMessage
import com.example.collisionengine.data.network.SupabaseClient
import com.example.collisionengine.data.state.GlobalProfileState
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.decodeRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<SupabaseChatMessage>>(emptyList())
    val messages: StateFlow<List<SupabaseChatMessage>> = _messages.asStateFlow()

    // The person you are chatting with
    private var receiverId = ""
    private var myUserId = ""

    fun init(receiverName: String) {
        if (this.receiverId == receiverName) return // Already initialized
        
        this.receiverId = receiverName
        this.myUserId = GlobalProfileState.name.value
        
        fetchMessages()
        subscribeToRealtime()
    }

    private fun fetchMessages() {
        viewModelScope.launch {
            try {
                val fetchedMessages = SupabaseClient.client.from("messages")
                    .select {
                        // Fetch messages where sender=me AND receiver=them OR sender=them AND receiver=me
                    }
                    .decodeList<SupabaseChatMessage>()
                    
                // Filter locally for now to avoid complex Postgrest queries
                val filteredMessages = fetchedMessages.filter { 
                    (it.senderId == myUserId && it.receiverId == receiverId) || 
                    (it.senderId == receiverId && it.receiverId == myUserId)
                }.sortedBy { it.createdAt }
                
                _messages.value = filteredMessages
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error fetching messages", e)
            }
        }
    }

    private fun subscribeToRealtime() {
        viewModelScope.launch {
            try {
                val channel = SupabaseClient.client.channel("public:messages")
                
                // Listen for any inserts to the messages table
                val messageFlow = channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                    table = "messages"
                }

                channel.subscribe()

                messageFlow.collect { action ->
                    val newMessage = action.decodeRecord<SupabaseChatMessage>()
                    
                    // Only add if it belongs to this conversation
                    if ((newMessage.senderId == myUserId && newMessage.receiverId == receiverId) || 
                        (newMessage.senderId == receiverId && newMessage.receiverId == myUserId)) {
                        
                        // Check if it already exists to avoid duplicates
                        if (_messages.value.none { it.id == newMessage.id }) {
                            _messages.value = _messages.value + newMessage
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error subscribing to realtime", e)
            }
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return
        
        viewModelScope.launch {
            try {
                val newMessage = com.example.collisionengine.data.models.SupabaseMessageInsert(
                    senderId = myUserId,
                    receiverId = receiverId,
                    content = content
                )
                SupabaseClient.client.from("messages").insert(newMessage)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending message", e)
            }
        }
    }
}
