package com.example.collisionengine.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.collisionengine.ui.components.StandardCard
import com.example.collisionengine.ui.components.bouncyClickable
import com.example.collisionengine.ui.theme.*
import com.example.collisionengine.data.state.GlobalProfileState
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToConnections: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var isLiked1 by remember { mutableStateOf(true) }
    var isLiked2 by remember { mutableStateOf(false) }

    val userName by GlobalProfileState.name.collectAsState()
    val userRole by GlobalProfileState.role.collectAsState()
    val userBio by GlobalProfileState.bio.collectAsState()
    val researchGateLink by GlobalProfileState.researchGateLink.collectAsState()
    val googleScholarLink by GlobalProfileState.googleScholarLink.collectAsState()

    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    var showEditDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section with Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                // Banner Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(PrimaryBlue, SecondaryBlue)
                            )
                        )
                ) {
                    // Top App Bar Icons overlaid on banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        IconButton(
                            onClick = { /* Settings */ },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = Color.White)
                        }
                    }
                }

                // Profile Avatar overlapping banner
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = (-20).dp)
                        .size(120.dp)
                        .shadow(16.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(4.dp, BackgroundLight, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(64.dp),
                            tint = PrimaryBlue
                        )
                    }
                }
            }

            // User Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userRole,
                    style = MaterialTheme.typography.bodyLarge,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = userBio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryLight,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (researchGateLink.isNotBlank()) {
                        Text(
                            text = "ResearchGate",
                            color = PrimaryBlue,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryBlue.copy(alpha = 0.1f))
                                .clickable {
                                    try {
                                        var url = researchGateLink.trim()
                                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                            url = "https://$url"
                                        }
                                        uriHandler.openUri(url)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    if (googleScholarLink.isNotBlank()) {
                        Text(
                            text = "Google Scholar",
                            color = PrimaryBlue,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryBlue.copy(alpha = 0.1f))
                                .clickable {
                                    try {
                                        var url = googleScholarLink.trim()
                                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                            url = "https://$url"
                                        }
                                        uriHandler.openUri(url)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = PrimaryBlue.copy(alpha = 0.1f))
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(modifier = Modifier.clickable { onNavigateToConnections() }) {
                        ProfileStat(count = "24", label = "Connections")
                    }
                    Divider(
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp),
                        color = BackgroundLight
                    )
                    Box {
                        ProfileStat(count = "5", label = "Papers")
                    }
                    Divider(
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp),
                        color = BackgroundLight
                    )
                    ProfileStat(count = "12k", label = "Views")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Edit Profile Button
                Button(
                    onClick = { showEditDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .bouncyClickable { },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Profile", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Published Papers Section
            Text(
                text = "Published Papers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val papers = listOf(
                "Edge AI and Computer Vision",
                "Distributed Systems in Healthcare",
                "IoT for Smart Parking",
                "LLMs for Code Generation",
                "Graph Neural Networks"
            )

            papers.forEachIndexed { index, paper ->
                ActivityTimelineItem(
                    title = paper,
                    subtitle = "Published Paper",
                    icon = Icons.Filled.Edit,
                    tags = listOf("Research", "Publication"),
                    isLast = index == papers.size - 1
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Button
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp)
                    .bouncyClickable { },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("Logout", fontWeight = FontWeight.Bold, color = Color.White, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(120.dp)) // Padding for bottom nav
        }
    }

    if (showEditDialog) {
        var editName by remember { mutableStateOf(userName) }
        var editRole by remember { mutableStateOf(userRole) }
        var editBio by remember { mutableStateOf(userBio) }
        var editResearchGate by remember { mutableStateOf(researchGateLink) }
        var editScholar by remember { mutableStateOf(googleScholarLink) }
        
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = { 
                Text(
                    "Edit Profile", 
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                ) 
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = editRole,
                        onValueChange = { editRole = it },
                        label = { Text("Role") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = editResearchGate,
                        onValueChange = { editResearchGate = it },
                        label = { Text("ResearchGate Link") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = editScholar,
                        onValueChange = { editScholar = it },
                        label = { Text("Google Scholar Link") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        GlobalProfileState.name.value = editName
                        GlobalProfileState.role.value = editRole
                        GlobalProfileState.bio.value = editBio
                        GlobalProfileState.researchGateLink.value = editResearchGate
                        GlobalProfileState.googleScholarLink.value = editScholar
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = TextSecondaryLight)
                }
            }
        )
    }

}

@Composable
fun ActivityTimelineItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tags: List<String>,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // Content Card
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 16.dp)
                .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = PrimaryBlue.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, style = MaterialTheme.typography.labelMedium, color = TextSecondaryLight)
                Spacer(modifier = Modifier.height(12.dp))
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEach { tag ->
                        Surface(
                            color = PrimaryBlue.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStat(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryLight
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondaryLight
        )
    }
}
