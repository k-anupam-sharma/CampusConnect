package com.example.collisionengine.ui.explanation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collisionengine.data.network.LocalDatasetClient
import com.example.collisionengine.ui.theme.PrimaryBlue
import com.example.collisionengine.ui.theme.SecondaryBlue
import com.example.collisionengine.ui.theme.TextPrimaryLight
import com.example.collisionengine.ui.theme.TextSecondaryLight
import com.example.collisionengine.ui.theme.VerifiedGreen
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExplanationScreen(
    name: String,
    role: String,
    reason: String,
    score: Int,
    onNavigateBack: () -> Unit,
    onStartConversation: () -> Unit
) {
    val context = LocalContext.current

    // Decode and sanitize any residual URL encoding or plus signs
    val decodedReason = remember(reason) {
        try {
            URLDecoder.decode(reason, "UTF-8").replace("+", " ")
        } catch (e: Exception) {
            reason.replace("+", " ")
        }
    }

    // Lookup full database entity
    val studentData = remember(name) { LocalDatasetClient.getStudentByName(name) }
    val facultyData = remember(name) { LocalDatasetClient.getFacultyByName(name) }
    val isFaculty = facultyData != null || role.contains("Faculty", ignoreCase = true) || name.startsWith("Dr.", ignoreCase = true) || name.startsWith("Prof.", ignoreCase = true)

    // Extract tags/items from reason, skills, expertise, and research
    val allTags = remember(studentData, facultyData, decodedReason) {
        val list = mutableListOf<String>()
        if (studentData != null) {
            studentData.skills?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.let { list.addAll(it) }
            studentData.researchInterests?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.let { list.addAll(it) }
        } else if (facultyData != null) {
            facultyData.expertise?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.let { list.addAll(it) }
            facultyData.researchInterests?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() }?.let { list.addAll(it) }
        } else {
            decodedReason.split(Regex("[,;]+")).map { it.trim() }.filter { it.isNotBlank() && it.length > 2 }.let { list.addAll(it) }
        }
        list.distinct()
    }

    val projectsList = remember(studentData) {
        if (studentData != null && !studentData.projects.isNullOrBlank()) {
            studentData.projects.split(";").map { it.trim() }.filter { it.isNotBlank() }
        } else {
            emptyList()
        }
    }

    val publicationsList = remember(facultyData) {
        if (facultyData != null && !facultyData.publications.isNullOrBlank()) {
            facultyData.publications.split(";").map { it.trim() }.filter { it.isNotBlank() }
        } else {
            emptyList()
        }
    }

    var isBookmarked by remember { mutableStateOf(false) }
    var feedbackState by remember { mutableStateOf(0) } // 0=None, 1=Up, -1=Down

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold, color = TextPrimaryLight) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimaryLight)
                    }
                },
                actions = {
                    IconButton(onClick = { isBookmarked = !isBookmarked }) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) PrimaryBlue else TextSecondaryLight
                        )
                    }
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Campus Connect Profile: $name")
                            putExtra(Intent.EXTRA_TEXT, "Check out $name ($role) on Campus Connect! Specializes in: ${allTags.take(3).joinToString(", ")}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Profile"))
                    }) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share", tint = TextSecondaryLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Start Conversation CTA Button
                    Button(
                        onClick = onStartConversation,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Start Conversation", tint = Color.White)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Start Conversation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile Card Header
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar with ring
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(PrimaryBlue.copy(alpha = 0.15f), SecondaryBlue.copy(alpha = 0.25f))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name.split(" ")
                                    .filter { it.isNotBlank() && !it.equals("Dr.", ignoreCase = true) && !it.equals("Prof.", ignoreCase = true) }
                                    .take(2)
                                    .mapNotNull { it.firstOrNull()?.toString() }
                                    .joinToString("")
                                    .ifEmpty { name.firstOrNull()?.toString() ?: "U" },
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                if (isFaculty) Icons.Default.School else Icons.Default.Work,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = role,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                color = PrimaryBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Action Buttons (Google Scholar & Email)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://scholar.google.com/scholar?q=${Uri.encode(name)}")
                                    )
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.4f)),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = PrimaryBlue.copy(alpha = 0.04f))
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Google Scholar", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Google Scholar", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:")
                                        putExtra(Intent.EXTRA_SUBJECT, "Connecting via Campus Connect - $name")
                                        putExtra(Intent.EXTRA_TEXT, "Hi $name,\n\nI discovered your profile on Campus Connect regarding your research and projects. I would love to connect!")
                                    }
                                    context.startActivity(emailIntent)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.4f)),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = PrimaryBlue.copy(alpha = 0.04f))
                            ) {
                                Icon(Icons.Default.Email, contentDescription = "Email", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Email", color = PrimaryBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }


            // Projects Section (if student or has projects)
            if (projectsList.isNotEmpty()) {
                item {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 10.dp)
                        ) {
                            Icon(Icons.Default.Code, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Featured Projects",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )
                        }

                        projectsList.forEach { projectTitle ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(PrimaryBlue.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Text(
                                        text = projectTitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimaryLight
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Publications Section (if faculty)
            if (publicationsList.isNotEmpty()) {
                item {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 10.dp)
                        ) {
                            Icon(Icons.Default.Science, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Selected Publications",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )
                        }

                        publicationsList.forEach { pubTitle ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFE8F5E9)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.School, contentDescription = null, tint = VerifiedGreen, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Text(
                                        text = pubTitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimaryLight
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Skills & Technical Expertise (Clean Chips)
            if (allTags.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = if (isFaculty) "Expertise & Research Focus" else "Skills & Technologies",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                allTags.forEach { tag ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0xFFF0F4F8),
                                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            text = tag,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimaryLight
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Collaboration Benefit Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFBFD)),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "💡 Collaboration Benefit",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Connecting with $name can help you resolve technical blockers, exchange research insights, and accelerate project delivery.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryLight
                        )
                    }
                }
            }

            // Match Feedback Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Was this match helpful?",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondaryLight
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { feedbackState = 1 },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (feedbackState == 1) PrimaryBlue else Color.LightGray),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (feedbackState == 1) PrimaryBlue.copy(alpha = 0.1f) else Color.White
                            )
                        ) {
                            Text("👍 Yes", color = if (feedbackState == 1) PrimaryBlue else TextPrimaryLight)
                        }

                        OutlinedButton(
                            onClick = { feedbackState = -1 },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, if (feedbackState == -1) Color.Red else Color.LightGray),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (feedbackState == -1) Color.Red.copy(alpha = 0.08f) else Color.White
                            )
                        ) {
                            Text("👎 No", color = if (feedbackState == -1) Color.Red else TextPrimaryLight)
                        }
                    }

                    if (feedbackState != 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Thank you! Your feedback improves future recommendations.",
                            style = MaterialTheme.typography.labelSmall,
                            color = PrimaryBlue
                        )
                    }
                }
            }
        }
    }
}

