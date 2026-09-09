package com.example.collisionengine.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collisionengine.data.models.SupabaseProfile
import com.example.collisionengine.data.network.SupabaseClient
import com.example.collisionengine.data.state.GlobalProfileState
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(name: String, onLoginSuccess: () -> Unit) {
        if (name.isBlank()) {
            _uiState.value = LoginUiState.Error("Please enter your name")
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            try {
                // Look up the profile in Supabase by name
                val profiles = SupabaseClient.client.from("profiles")
                    .select {
                        filter {
                            eq("name", name)
                        }
                    }.decodeList<SupabaseProfile>()

                if (profiles.isNotEmpty()) {
                    val profile = profiles.first()
                    // Update global state and persist session
                    GlobalProfileState.loginAs(profile)
                    
                    _uiState.value = LoginUiState.Success
                    onLoginSuccess()
                } else {
                    _uiState.value = LoginUiState.Error("Profile not found. Please check your name.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = LoginUiState.Error("Failed to connect: ${e.message}")
            }
        }
    }

    fun signUp(name: String, role: String, onSignUpSuccess: () -> Unit) {
        if (name.isBlank() || role.isBlank()) {
            _uiState.value = LoginUiState.Error("Please enter your name and role")
            return
        }

        _uiState.value = LoginUiState.Loading

        viewModelScope.launch {
            try {
                // Check if user already exists
                val existing = SupabaseClient.client.from("profiles")
                    .select { filter { eq("name", name) } }.decodeList<SupabaseProfile>()
                
                if (existing.isNotEmpty()) {
                    _uiState.value = LoginUiState.Error("User already exists. Please login.")
                    return@launch
                }

                val newProfile = SupabaseProfile(
                    id = java.util.UUID.randomUUID().toString(),
                    name = name,
                    role = role
                )

                SupabaseClient.client.from("profiles").insert(newProfile)

                GlobalProfileState.loginAs(newProfile)
                _uiState.value = LoginUiState.Success
                onSignUpSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = LoginUiState.Error("Failed to sign up: ${e.message}")
            }
        }
    }
}
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
