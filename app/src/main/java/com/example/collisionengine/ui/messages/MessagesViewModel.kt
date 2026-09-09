package com.example.collisionengine.ui.messages

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collisionengine.data.models.SupabaseChatMessage
import com.example.collisionengine.data.network.SupabaseClient
import com.example.collisionengine.data.state.GlobalProfileState
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.PostgresAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MessagesViewModel : ViewModel() {

    private val _realConversations = MutableStateFlow<List<ChatConversation>>(emptyList())
    val realConversations: StateFlow<List<ChatConversation>> = _realConversations.asStateFlow()

    private val myUserId = GlobalProfileState.name.value
    private val allMessages = mutableListOf<SupabaseChatMessage>()

    init {
        if (myUserId.isNotBlank()) {
            fetchMessages()
            subscribeToRealtime()
        }
    }

    private fun fetchMessages() {
        viewModelScope.launch {
            try {
                // Ideally, you'd filter via Postgrest `or=(sender_id.eq.myUserId,receiver_id.eq.myUserId)`
                // But for simplicity/hackathon, fetch all and filter locally, or use a broad filter
                val fetched = SupabaseClient.client.from("messages")
                    .select {
                        // filter {}
                    }
                    .decodeList<SupabaseChatMessage>()

                val myMessages = fetched.filter { it.senderId == myUserId || it.receiverId == myUserId }
                allMessages.clear()
                allMessages.addAll(myMessages)
                updateConversations()
            } catch (e: Exception) {
                Log.e("MessagesViewModel", "Error fetching messages", e)
            }
        }
    }

    private fun subscribeToRealtime() {
        viewModelScope.launch {
            try {
                val channel = SupabaseClient.client.channel("messages_inbox")
                val messageFlow = channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                    table = "messages"
                }
                channel.subscribe()

                messageFlow.collect { action ->
                    val newMessage = action.decodeRecord<SupabaseChatMessage>()
                    if (newMessage.senderId == myUserId || newMessage.receiverId == myUserId) {
                        if (allMessages.none { it.id == newMessage.id }) {
                            allMessages.add(newMessage)
                            updateConversations()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("MessagesViewModel", "Error subscribing to realtime", e)
            }
        }
    }

    private fun updateConversations() {
        // Group by the "other" person
        val grouped = allMessages.groupBy { if (it.senderId == myUserId) it.receiverId else it.senderId }
        
        val conversations = grouped.map { (peerName, messages) ->
            val latestMessage = messages.maxByOrNull { it.createdAt ?: "" }!!
            ChatConversation(
                id = peerName,
                name = peerName,
                role = "Campus Network", // We could look up their role from profiles table
                lastMessage = latestMessage.content,
                timeAgo = "Just now",
                unreadCount = 0,
                isOnline = true,
                avatarColor = androidx.compose.ui.graphics.Color(0xFF4285F4)
            )
        }.sortedByDescending { it.timeAgo }

        _realConversations.value = conversations
    }
}
