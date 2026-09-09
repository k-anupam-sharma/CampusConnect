package com.example.collisionengine.ui.messages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collisionengine.ui.theme.*

data class ChatConversation(
    val id: String,
    val name: String,
    val role: String,
    val lastMessage: String,
    val timeAgo: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = true,
    val avatarColor: Color = PrimaryBlue
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChat: (String) -> Unit = {},
    viewModel: MessagesViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var showNewChatDialog by remember { mutableStateOf(false) }

    val realConversations by viewModel.realConversations.collectAsState()

    val activeConnections = remember {
        listOf(
            "Dr. Emily Chen" to true,
            "Aarav Bansal" to true,
            "Dr. Grace Lin" to false,
            "Michael Ross" to true,
            "Sarah Jenkins" to false,
            "Priya Agarwal" to true,
            "Dr. Yash Malhotra" to false
        )
    }

    val filteredConversations = remember(searchQuery, realConversations) {
        if (searchQuery.isBlank()) {
            realConversations
        } else {
            realConversations.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.lastMessage.contains(searchQuery, ignoreCase = true) ||
                it.role.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Messages", fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = PrimaryBlue
                        ) {
                            Text(
                                text = "2",
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimaryLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {


            // Active Connections Story Row
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(top = 10.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = "Active Connections",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(activeConnections) { (name, isOnline) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(68.dp)
                                    .clickable { onNavigateToChat(name) }
                            ) {
                                Box(contentAlignment = Alignment.BottomEnd) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(PrimaryBlue.copy(alpha = 0.2f), SecondaryBlue.copy(alpha = 0.35f))
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
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryBlue
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (name.startsWith("Dr.")) name.split(" ").take(2).joinToString(" ") else name.split(" ").firstOrNull() ?: name,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = TextPrimaryLight
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Chats",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                    Text(
                        text = "${filteredConversations.size} conversations",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryLight
                    )
                }
            }

            // Conversations List
            if (filteredConversations.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No conversations found", style = MaterialTheme.typography.titleMedium, color = TextSecondaryLight)
                    }
                }
            } else {
                items(filteredConversations) { conv ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { onNavigateToChat(conv.name) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar with online status
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(conv.avatarColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = conv.name.split(" ")
                                            .filter { it.isNotBlank() && !it.equals("Dr.", ignoreCase = true) && !it.equals("Prof.", ignoreCase = true) }
                                            .take(2)
                                            .mapNotNull { it.firstOrNull()?.toString() }
                                            .joinToString("")
                                            .ifEmpty { conv.name.firstOrNull()?.toString() ?: "U" },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = conv.avatarColor
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Message Info
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = conv.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryLight
                                    )
                                    Text(
                                        text = conv.timeAgo,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (conv.unreadCount > 0) PrimaryBlue else TextSecondaryLight,
                                        fontWeight = if (conv.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                                    )
                                }

                                Text(
                                    text = conv.role,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = conv.lastMessage,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (conv.unreadCount > 0) TextPrimaryLight else TextSecondaryLight,
                                        fontWeight = if (conv.unreadCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (conv.unreadCount > 0) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = CircleShape,
                                            color = PrimaryBlue
                                        ) {
                                            Text(
                                                text = conv.unreadCount.toString(),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Start New Chat Dialog
    if (showNewChatDialog) {
        AlertDialog(
            onDismissRequest = { showNewChatDialog = false },
            title = {
                Text("Start a New Chat", fontWeight = FontWeight.Bold, color = TextPrimaryLight)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select a campus peer or researcher to message:", style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
                    val quickPeers = listOf(
                        "Dr. Emily Chen" to "Faculty • Edge AI",
                        "Aarav Bansal" to "Student • Battery Systems",
                        "Dr. Grace Lin" to "Faculty • Distributed Systems",
                        "Michael Ross" to "Student • Graph Neural Networks",
                        "Sarah Jenkins" to "Researcher • Sustainable Tech",
                        "Priya Agarwal" to "Student • Cloud Systems"
                    )
                    quickPeers.forEach { (peerName, peerRole) ->
                        Surface(
                            onClick = {
                                showNewChatDialog = false
                                onNavigateToChat(peerName)
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryBlue.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = peerName.firstOrNull()?.toString() ?: "U",
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = peerName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextPrimaryLight)
                                    Text(text = peerRole, style = MaterialTheme.typography.labelSmall, color = TextSecondaryLight)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showNewChatDialog = false }) {
                    Text("Cancel", color = TextSecondaryLight)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

