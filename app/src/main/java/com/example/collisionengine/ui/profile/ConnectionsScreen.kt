package com.example.collisionengine.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.collisionengine.ui.theme.BackgroundLight
import com.example.collisionengine.ui.theme.PrimaryBlue
import com.example.collisionengine.ui.theme.TextPrimaryLight
import com.example.collisionengine.ui.theme.TextSecondaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    val connections = listOf(
        Pair("Aditya Kulkarni", "Student"),
        Pair("Sarah J.", "Student"),
        Pair("Emily Chen", "Faculty"),
        Pair("Rajesh Kumar", "Faculty"),
        Pair("Michael Smith", "Student"),
        Pair("Priya Patel", "Student"),
        Pair("David Wong", "Faculty"),
        Pair("Elena Rodriguez", "Student")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Peers", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextPrimaryLight
                )
            )
        },
        containerColor = BackgroundLight
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(connections) { (name, role) ->
                ConnectionItem(
                    name = name,
                    role = role,
                    onMessageClick = { onNavigateToChat(name) }
                )
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun ConnectionItem(
    name: String,
    role: String,
    onMessageClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMessageClick() }
            .padding(vertical = 16.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Person, contentDescription = null, tint = PrimaryBlue)
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Name
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimaryLight
            )
            Text(
                text = role,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryLight
            )
        }
        
        // Message Icon
        IconButton(
            onClick = onMessageClick,
            modifier = Modifier
                .size(40.dp)
                .background(PrimaryBlue.copy(alpha = 0.1f), CircleShape)
        ) {
            Icon(Icons.Filled.MailOutline, contentDescription = "Message", tint = PrimaryBlue)
        }
    }
}
