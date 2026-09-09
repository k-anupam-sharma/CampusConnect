package com.example.collisionengine.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseChatMessage(
    @SerialName("id")
    val id: String? = null,
    @SerialName("connection_id")
    val connectionId: String? = null, // Currently a UUID in DB, but can map to String
    @SerialName("sender_id")
    val senderId: String,
    @SerialName("receiver_id")
    val receiverId: String,
    @SerialName("content")
    val content: String,
    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class SupabaseMessageInsert(
    @SerialName("sender_id")
    val senderId: String,
    @SerialName("receiver_id")
    val receiverId: String,
    @SerialName("content")
    val content: String
)
