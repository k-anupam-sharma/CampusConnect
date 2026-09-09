package com.example.collisionengine.ui.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.collisionengine.data.models.SupabaseChatMessage
import com.example.collisionengine.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    name: String,
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val myUserId = com.example.collisionengine.data.state.GlobalProfileState.name.collectAsState().value

    LaunchedEffect(name) {
        viewModel.init(name)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name.split(" ")
                                        .filter { it.isNotBlank() && !it.equals("Dr.", ignoreCase = true) && !it.equals("Prof.", ignoreCase = true) }
                                        .take(2)
                                        .mapNotNull { it.firstOrNull()?.toString() }
                                        .joinToString("")
                                        .ifEmpty { name.firstOrNull()?.toString() ?: "U" },
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
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
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...", color = TextSecondaryLight, fontSize = 14.sp) },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF1F5F9),
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = PrimaryBlue
                        )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = PrimaryBlue,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                    }
                }
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            reverseLayout = true,
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages.reversed()) { message ->
                ChatBubble(
                    message = message,
                    isMine = message.senderId == myUserId
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun ChatBubble(message: SupabaseChatMessage, isMine: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isMine) 18.dp else 4.dp,
                bottomEnd = if (isMine) 4.dp else 18.dp
            ),
            color = if (isMine) PrimaryBlue else Color.White,
            border = if (isMine) null else BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
            shadowElevation = if (isMine) 2.dp else 1.dp,
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Text(
                text = message.content,
                color = if (isMine) Color.White else TextPrimaryLight,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                lineHeight = 20.sp
            )
        }
    }
}
