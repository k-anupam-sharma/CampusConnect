package com.example.collisionengine.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupabaseProfile(
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String,
    @SerialName("role") val role: String,
    @SerialName("department") val department: String? = null,
    @SerialName("year") val year: String? = null,
    @SerialName("skills") val skills: String? = null,
    @SerialName("projects") val projects: String? = null,
    @SerialName("research_interests") val researchInterests: String? = null,
    @SerialName("certifications") val certifications: String? = null,
    @SerialName("career_interests") val careerInterests: String? = null,
    @SerialName("expertise") val expertise: String? = null,
    @SerialName("publications") val publications: String? = null
)
