package com.example.collisionengine.data.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.Realtime

object SupabaseClient {
    // Paste your Project URL here
    private const val SUPABASE_URL = "https://ztsgghwtdtxxopxaqnwx.supabase.co"
    
    // Paste your Anon Key here
    private const val SUPABASE_KEY = "sb_publishable_nmRPk6YXjBRhYuViLb1J7Q_OR5q20sO"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Postgrest)
        install(Realtime)
    }
    
    private fun normalizeName(name: String?): String {
        if (name == null) return ""
        return name
            .lowercase()
            .replace("dr.", "")
            .replace("dr ", "")
            .replace("prof.", "")
            .replace("prof ", "")
            .replace("professor", "")
            .replace("*", "")
            .replace("\"", "")
            .replace("'", "")
            .replace(Regex("""[<>]"""), "")
            .trim()
    }

    suspend fun searchProfilesByNames(names: List<String>): List<com.example.collisionengine.data.model.ProfileMatch> {
        if (names.isEmpty()) return emptyList()
        
        val normalizedNames = names.map { normalizeName(it) }
        val matches = mutableListOf<com.example.collisionengine.data.model.ProfileMatch>()
        
        // Fetch Students
        try {
            val allStudents = client.from("Student").select().decodeList<com.example.collisionengine.data.models.Student>()
            val students = allStudents.filter { normalizeName(it.name) in normalizedNames }
            matches.addAll(students.map { student ->
                com.example.collisionengine.data.model.ProfileMatch(
                    name = student.name ?: "Unknown",
                    role = "${student.department ?: ""} • ${student.year ?: ""}",
                    matchReasonTitle = "Related to your query",
                    matchReasonText = student.projects?.takeIf { it.isNotBlank() } ?: student.skills ?: "",
                    tags = student.skills?.split(",")?.map { it.trim() }?.take(4) ?: emptyList()
                )
            })
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val allStudentsFallback = client.from("students").select().decodeList<com.example.collisionengine.data.models.Student>()
                val students = allStudentsFallback.filter { normalizeName(it.name) in normalizedNames }
                matches.addAll(students.map { student ->
                    com.example.collisionengine.data.model.ProfileMatch(
                        name = student.name ?: "Unknown",
                        role = "${student.department ?: ""} • ${student.year ?: ""}",
                        matchReasonTitle = "Related to your query",
                        matchReasonText = student.projects?.takeIf { it.isNotBlank() } ?: student.skills ?: "",
                        tags = student.skills?.split(",")?.map { it.trim() }?.take(4) ?: emptyList()
                    )
                })
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
        
        // Fetch Faculty
        try {
            val allFaculty = client.from("Faculty").select().decodeList<com.example.collisionengine.data.models.Faculty>()
            val facultyList = allFaculty.filter { normalizeName(it.name) in normalizedNames }
            matches.addAll(facultyList.map { faculty ->
                com.example.collisionengine.data.model.ProfileMatch(
                    name = faculty.name ?: "Unknown",
                    role = "Faculty • ${faculty.department ?: ""}",
                    matchReasonTitle = "Related to your query",
                    matchReasonText = faculty.expertise ?: "",
                    tags = faculty.researchInterests?.split(",")?.map { it.trim() }?.take(4) ?: emptyList()
                )
            })
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val allFacultyFallback = client.from("faculty").select().decodeList<com.example.collisionengine.data.models.Faculty>()
                val facultyList = allFacultyFallback.filter { normalizeName(it.name) in normalizedNames }
                matches.addAll(facultyList.map { faculty ->
                    com.example.collisionengine.data.model.ProfileMatch(
                        name = faculty.name ?: "Unknown",
                        role = "Faculty • ${faculty.department ?: ""}",
                        matchReasonTitle = "Related to your query",
                        matchReasonText = faculty.expertise ?: "",
                        tags = faculty.researchInterests?.split(",")?.map { it.trim() }?.take(4) ?: emptyList()
                    )
                })
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }
        
        return matches
    }

    suspend fun searchProfilesByKeywords(query: String): List<com.example.collisionengine.data.model.ProfileMatch> {
        if (query.isBlank()) return emptyList()
        val q = query.lowercase().trim()
        val keywords = q.split(" ").filter { it.length > 3 }
        if (keywords.isEmpty()) return emptyList()

        val matches = mutableListOf<com.example.collisionengine.data.model.ProfileMatch>()

        try {
            val allStudents = client.from("Student").select().decodeList<com.example.collisionengine.data.models.Student>()
            val matchedStudents = allStudents.filter { student ->
                val textToSearch = "${student.department} ${student.skills} ${student.projects}".lowercase()
                keywords.any { keyword -> textToSearch.contains(keyword) }
            }
            matches.addAll(matchedStudents.map { student ->
                com.example.collisionengine.data.model.ProfileMatch(
                    name = student.name ?: "Unknown",
                    role = "${student.department ?: ""} • ${student.year ?: ""}",
                    matchReasonTitle = "Related to your query",
                    matchReasonText = student.projects?.takeIf { it.isNotBlank() } ?: student.skills ?: "",
                    tags = student.skills?.split(",")?.map { it.trim() }?.take(4) ?: emptyList()
                )
            })
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val allStudentsFallback = client.from("students").select().decodeList<com.example.collisionengine.data.models.Student>()
                val matchedStudents = allStudentsFallback.filter { student ->
                    val textToSearch = "${student.department} ${student.skills} ${student.projects}".lowercase()
                    keywords.any { keyword -> textToSearch.contains(keyword) }
                }
                matches.addAll(matchedStudents.map { student ->
                    com.example.collisionengine.data.model.ProfileMatch(
                        name = student.name ?: "Unknown",
                        role = "${student.department ?: ""} • ${student.year ?: ""}",
                        matchReasonTitle = "Related to your query",
                        matchReasonText = student.projects?.takeIf { it.isNotBlank() } ?: student.skills ?: "",
                        tags = student.skills?.split(",")?.map { it.trim() }?.take(4) ?: emptyList()
                    )
                })
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }

        try {
            val allFaculty = client.from("Faculty").select().decodeList<com.example.collisionengine.data.models.Faculty>()
            val matchedFaculty = allFaculty.filter { faculty ->
                val textToSearch = "${faculty.department} ${faculty.expertise} ${faculty.researchInterests}".lowercase()
                keywords.any { keyword -> textToSearch.contains(keyword) }
            }
            matches.addAll(matchedFaculty.map { faculty ->
                com.example.collisionengine.data.model.ProfileMatch(
                    name = faculty.name ?: "Unknown",
                    role = "Faculty • ${faculty.department ?: ""}",
                    matchReasonTitle = "Related to your query",
                    matchReasonText = faculty.expertise ?: "",
                    tags = faculty.researchInterests?.split(",")?.map { it.trim() }?.take(4) ?: emptyList()
                )
            })
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val allFacultyFallback = client.from("faculty").select().decodeList<com.example.collisionengine.data.models.Faculty>()
                val matchedFaculty = allFacultyFallback.filter { faculty ->
                    val textToSearch = "${faculty.department} ${faculty.expertise} ${faculty.researchInterests}".lowercase()
                    keywords.any { keyword -> textToSearch.contains(keyword) }
                }
                matches.addAll(matchedFaculty.map { faculty ->
                    com.example.collisionengine.data.model.ProfileMatch(
                        name = faculty.name ?: "Unknown",
                        role = "Faculty • ${faculty.department ?: ""}",
                        matchReasonTitle = "Related to your query",
                        matchReasonText = faculty.expertise ?: "",
                        tags = faculty.researchInterests?.split(",")?.map { it.trim() }?.take(4) ?: emptyList()
                    )
                })
            } catch (e2: Exception) {
                e2.printStackTrace()
            }
        }

        return matches.distinctBy { it.name }.take(5)
    }
}
