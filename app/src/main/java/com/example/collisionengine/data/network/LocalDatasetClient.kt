package com.example.collisionengine.data.network

import android.content.Context
import com.example.collisionengine.data.model.ProfileMatch
import com.example.collisionengine.data.models.*
import kotlinx.serialization.json.Json
import java.io.InputStreamReader

object LocalDatasetClient {
    private var allStudents: List<Student> = emptyList()
    private var allFaculty: List<Faculty> = emptyList()
    private var allProjects: List<Project> = emptyList()
    private var allResearch: List<Research> = emptyList()
    private var allPlacements: List<Placement> = emptyList()

    private val jsonFormat = Json { ignoreUnknownKeys = true }

    fun init(context: Context) {
        try {
            // Load Students
            context.assets.open("Students.json").use { inputStream ->
                val jsonString = InputStreamReader(inputStream).readText()
                allStudents = jsonFormat.decodeFromString(jsonString)
            }
            // Load Faculty
            context.assets.open("Faculty.json").use { inputStream ->
                val jsonString = InputStreamReader(inputStream).readText()
                allFaculty = jsonFormat.decodeFromString(jsonString)
            }
            // Load Projects
            context.assets.open("Projects.json").use { inputStream ->
                val jsonString = InputStreamReader(inputStream).readText()
                allProjects = jsonFormat.decodeFromString(jsonString)
            }
            // Load Research
            context.assets.open("Research.json").use { inputStream ->
                val jsonString = InputStreamReader(inputStream).readText()
                allResearch = jsonFormat.decodeFromString(jsonString)
            }
            // Load Placement
            context.assets.open("Placement.json").use { inputStream ->
                val jsonString = InputStreamReader(inputStream).readText()
                allPlacements = jsonFormat.decodeFromString(jsonString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    private fun containsName(text: String, name: String): Boolean {
        val cleanName = normalizeName(name)
        if (cleanName.length < 3) return false
        val cleanText = normalizeName(text)
        return cleanText.contains(cleanName)
    }

    fun getStudentByName(name: String): Student? {
        val norm = normalizeName(name)
        if (norm.isBlank()) return null
        return allStudents.firstOrNull { 
            val sNorm = normalizeName(it.name)
            sNorm == norm || (sNorm.isNotBlank() && (sNorm.contains(norm) || norm.contains(sNorm)))
        }
    }

    fun getFacultyByName(name: String): Faculty? {
        val norm = normalizeName(name)
        if (norm.isBlank()) return null
        return allFaculty.firstOrNull { 
            val fNorm = normalizeName(it.name)
            fNorm == norm || (fNorm.isNotBlank() && (fNorm.contains(norm) || norm.contains(fNorm)))
        }
    }

    fun getProjectsForStudent(studentId: String?): List<Project> {
        if (studentId.isNullOrBlank()) return emptyList()
        return allProjects.filter { it.studentId == studentId }
    }

    fun getResearchForFaculty(facultyId: String?): List<Research> {
        if (facultyId.isNullOrBlank()) return emptyList()
        return allResearch.filter { it.personId == facultyId }
    }

    fun findMatches(
        query: String,
        aiResponse: String,
        extractedNames: List<String> = emptyList()
    ): List<ProfileMatch> {
        val matches = mutableListOf<ProfileMatch>()
        val matchedNamesLower = mutableSetOf<String>()

        // 1. Gather all candidate names from:
        //    a) extractedNames parameter (from LLM)
        //    b) Regex extracted names from AI response
        //    c) Direct scanning of database names in AI response
        val candidateNames = mutableListOf<String>()
        candidateNames.addAll(extractedNames)

        allStudents.forEach { student ->
            val name = student.name ?: return@forEach
            if (name.isNotBlank() && containsName(aiResponse, name)) {
                candidateNames.add(name)
            }
        }

        allFaculty.forEach { faculty ->
            val name = faculty.name ?: return@forEach
            if (name.isNotBlank() && containsName(aiResponse, name)) {
                candidateNames.add(name)
            }
        }

        // 2. Resolve candidate names to Student / Faculty profiles
        for (candidate in candidateNames.distinct()) {
            val normCandidate = normalizeName(candidate)
            if (normCandidate.isBlank() || normCandidate in matchedNamesLower) continue

            // Check Students
            val student = allStudents.firstOrNull { 
                val sNorm = normalizeName(it.name)
                sNorm == normCandidate || (sNorm.isNotBlank() && (sNorm.contains(normCandidate) || normCandidate.contains(sNorm)))
            }
            if (student != null) {
                val sName = student.name ?: candidate
                if (sName.lowercase() !in matchedNamesLower) {
                    matchedNamesLower.add(sName.lowercase())
                    matches.add(createStudentMatch(student, query, aiResponse))
                }
                continue
            }

            // Check Faculty
            val faculty = allFaculty.firstOrNull { 
                val fNorm = normalizeName(it.name)
                fNorm == normCandidate || (fNorm.isNotBlank() && (fNorm.contains(normCandidate) || normCandidate.contains(fNorm)))
            }
            if (faculty != null) {
                val fName = faculty.name ?: candidate
                if (fName.lowercase() !in matchedNamesLower) {
                    matchedNamesLower.add(fName.lowercase())
                    matches.add(createFacultyMatch(faculty, query, aiResponse))
                }
            }
        }

        return matches.take(4)
    }

    fun searchProfilesByNames(names: List<String>): List<ProfileMatch> {
        return findMatches("", "", names)
    }

    fun searchProfilesByPartialName(query: String): List<ProfileMatch> {
        if (query.isBlank()) return emptyList()
        val normQuery = query.lowercase().trim()
        val results = mutableListOf<ProfileMatch>()
        
        allStudents.filter { it.name?.lowercase()?.contains(normQuery) == true }.forEach { student ->
            results.add(createStudentMatch(student, "", ""))
        }
        
        allFaculty.filter { it.name?.lowercase()?.contains(normQuery) == true }.forEach { faculty ->
            results.add(createFacultyMatch(faculty, "", ""))
        }
        
        return results.distinctBy { it.name }
    }

    private fun createStudentMatch(student: Student, query: String, aiResponse: String): ProfileMatch {
        val projectsText = student.projects ?: ""
        val skillsText = student.skills ?: ""
        val researchText = student.researchInterests ?: ""

        var reasonTitle = "Related to your query"
        var reasonText = ""

        if (projectsText.isNotBlank()) {
            val individualProjects = projectsText.split(";").map { it.trim() }
            val mentionedProject = individualProjects.firstOrNull { proj ->
                proj.isNotBlank() && (aiResponse.contains(proj, ignoreCase = true) || query.contains(proj, ignoreCase = true))
            }
            reasonText = mentionedProject ?: projectsText
            reasonTitle = "Project Contributor"
        } else if (skillsText.isNotBlank()) {
            reasonText = skillsText
            reasonTitle = "Related to your query"
        } else if (researchText.isNotBlank()) {
            reasonText = researchText
            reasonTitle = "Research Interest"
        }

        val tags = mutableListOf<String>()
        student.skills?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.let { tags.addAll(it) }
        student.researchInterests?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.let { tags.addAll(it) }

        return ProfileMatch(
            name = student.name ?: "Unknown Student",
            role = "${student.department ?: "Student"} • Year ${student.year ?: "4"}",
            matchReasonTitle = reasonTitle,
            matchReasonText = reasonText.ifBlank { "Active student researcher" },
            tags = tags.distinct().take(4)
        )
    }

    private fun createFacultyMatch(faculty: Faculty, query: String, aiResponse: String): ProfileMatch {
        val expertiseText = faculty.expertise ?: ""
        val researchText = faculty.researchInterests ?: ""

        val reasonText = if (expertiseText.isNotBlank()) expertiseText else researchText

        val tags = mutableListOf<String>()
        faculty.researchInterests?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.let { tags.addAll(it) }
        faculty.expertise?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.let { tags.addAll(it) }

        return ProfileMatch(
            name = faculty.name ?: "Unknown Faculty",
            role = "Faculty • ${faculty.department ?: "Department"}",
            matchReasonTitle = "Related to your query",
            matchReasonText = reasonText.ifBlank { "Faculty domain expert" },
            tags = tags.distinct().take(4)
        )
    }

    private fun searchByKeywords(
        query: String,
        aiResponse: String,
        excludeNames: Set<String>
    ): List<ProfileMatch> {
        val stopWords = setOf(
            "the", "and", "for", "with", "this", "that", "from", "working", "poor",
            "good", "who", "what", "where", "project", "titled", "student", "faculty",
            "related", "only", "data", "provided", "have", "been", "was", "were",
            "are", "is", "about", "tell", "show", "give", "list", "name", "any", "all"
        )
        val combinedText = "$query $aiResponse".lowercase()
        val tokens = combinedText.split(Regex("[^a-zA-Z0-9]+"))
            .filter { it.length >= 3 && it !in stopWords }
            .distinct()

        if (tokens.isEmpty()) return emptyList()

        val scoredMatches = mutableListOf<Pair<ProfileMatch, Int>>()

        // Score students
        for (student in allStudents) {
            val name = student.name ?: continue
            if (name.lowercase() in excludeNames) continue

            var score = 0
            val sProjects = student.projects?.lowercase() ?: ""
            val sSkills = student.skills?.lowercase() ?: ""
            val sResearch = student.researchInterests?.lowercase() ?: ""
            val sDept = student.department?.lowercase() ?: ""

            for (token in tokens) {
                if (sProjects.contains(token)) score += 40
                if (sSkills.contains(token)) score += 25
                if (sResearch.contains(token)) score += 20
                if (sDept.contains(token)) score += 10
            }

            // Linked projects
            val linkedProjects = allProjects.filter { it.studentId == student.studentId }
            for (proj in linkedProjects) {
                val pText = "${proj.title} ${proj.description} ${proj.technologies}".lowercase()
                for (token in tokens) {
                    if (pText.contains(token)) score += 30
                }
            }

            if (score > 0) {
                scoredMatches.add(Pair(createStudentMatch(student, query, aiResponse), score))
            }
        }

        // Score faculty
        for (faculty in allFaculty) {
            val name = faculty.name ?: continue
            if (name.lowercase() in excludeNames) continue

            var score = 0
            val fExpertise = faculty.expertise?.lowercase() ?: ""
            val fResearch = faculty.researchInterests?.lowercase() ?: ""
            val fDept = faculty.department?.lowercase() ?: ""

            for (token in tokens) {
                if (fExpertise.contains(token)) score += 35
                if (fResearch.contains(token)) score += 25
                if (fDept.contains(token)) score += 10
            }

            // Linked research
            val linkedResearch = allResearch.filter { it.personId == faculty.facultyId }
            for (res in linkedResearch) {
                val rText = "${res.title} ${res.abstract} ${res.researchArea} ${res.currentlyWorkingOn}".lowercase()
                for (token in tokens) {
                    if (rText.contains(token)) score += 30
                }
            }

            if (score > 0) {
                scoredMatches.add(Pair(createFacultyMatch(faculty, query, aiResponse), score))
            }
        }

        return scoredMatches.sortedByDescending { it.second }.map { it.first }
    }
}

