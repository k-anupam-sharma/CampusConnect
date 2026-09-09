package com.example.collisionengine.data.state

import android.content.Context
import android.content.SharedPreferences
import com.example.collisionengine.data.models.SupabaseProfile
import kotlinx.coroutines.flow.MutableStateFlow

object GlobalProfileState {
    
    private const val PREF_NAME = "CampusConnectPrefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_NAME = "user_name"
    private const val KEY_ROLE = "user_role"

    private var prefs: SharedPreferences? = null

    val isLoggedIn = MutableStateFlow(false)
    val userId = MutableStateFlow("")
    val name = MutableStateFlow("")
    val role = MutableStateFlow("")
    val department = MutableStateFlow("")
    val skills = MutableStateFlow("")
    val bio = MutableStateFlow("")
    val githubLink = MutableStateFlow("https://github.com/")
    val researchGateLink = MutableStateFlow("https://www.researchgate.net/")
    val googleScholarLink = MutableStateFlow("https://scholar.google.com/")

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            
            // Restore session
            isLoggedIn.value = prefs?.getBoolean(KEY_IS_LOGGED_IN, false) ?: false
            if (isLoggedIn.value) {
                userId.value = prefs?.getString(KEY_USER_ID, "") ?: ""
                name.value = prefs?.getString(KEY_NAME, "") ?: ""
                role.value = prefs?.getString(KEY_ROLE, "") ?: ""
            }
        }
    }

    fun loginAs(profile: SupabaseProfile) {
        isLoggedIn.value = true
        userId.value = profile.id
        name.value = profile.name
        role.value = profile.role
        department.value = profile.department ?: ""
        skills.value = profile.skills ?: ""
        
        // Generate a bio based on role
        if (profile.role == "faculty") {
            bio.value = "Faculty in ${profile.department}. Research interests: ${profile.researchInterests ?: "N/A"}. Expertise: ${profile.expertise ?: "N/A"}."
        } else {
            bio.value = "Student in ${profile.department}. Skills: ${profile.skills ?: "N/A"}. Interests: ${profile.careerInterests ?: "N/A"}."
        }

        // Persist
        prefs?.edit()?.apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_ID, profile.id)
            putString(KEY_NAME, profile.name)
            putString(KEY_ROLE, profile.role)
            apply()
        }
    }

    fun logout() {
        isLoggedIn.value = false
        userId.value = ""
        name.value = ""
        role.value = ""
        bio.value = ""
        
        prefs?.edit()?.clear()?.apply()
    }
}
