package com.example.collisionengine.ui.research

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collisionengine.data.network.DatabricksClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.collisionengine.data.model.ChatMessage

class ResearchViewModel : ViewModel() {
    companion object {
        private val sessionMessages = mutableListOf<ChatMessage>()
        private var sessionQuery = ""

        fun clearSession() {
            sessionMessages.clear()
            sessionQuery = ""
        }
    }

    private val _queryText = MutableStateFlow(sessionQuery)
    val queryText: StateFlow<String> = _queryText.asStateFlow()
    
    private val _messages = MutableStateFlow<List<ChatMessage>>(sessionMessages.toList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun onQueryChanged(newText: String) {
        _queryText.value = newText
        sessionQuery = newText
    }

    fun clearChat() {
        _messages.value = emptyList()
        clearSession()
    }

    fun askDatabricks() {
        val query = _queryText.value
        if (query.isBlank()) return
        
        val userMsg = ChatMessage(text = query, isUser = true)
        val updatedList = _messages.value + userMsg
        _messages.value = updatedList
        sessionMessages.clear()
        sessionMessages.addAll(updatedList)
        
        _isLoading.value = true
        _queryText.value = "" // clear input
        sessionQuery = ""
        
        viewModelScope.launch {
            val result = DatabricksClient.askGenie(query)
            
            // Add AI response, making TopMatch dynamic based on extracted names and semantic dataset matching
            val extractedNames = emptyList<String>()
            val matchedProfiles = com.example.collisionengine.data.network.LocalDatasetClient.findMatches(query, result, extractedNames)
            
            val aiMsg = ChatMessage(
                text = result, 
                isUser = false, 
                isTopMatch = matchedProfiles.isNotEmpty(),
                topMatches = matchedProfiles
            )
            val finalMessages = _messages.value + aiMsg
            _messages.value = finalMessages
            sessionMessages.clear()
            sessionMessages.addAll(finalMessages)
            _isLoading.value = false
        }
    }
}
