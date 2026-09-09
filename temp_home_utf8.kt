package com.example.collisionengine.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collisionengine.ui.components.*
import com.example.collisionengine.ui.theme.*
import com.example.collisionengine.data.state.GlobalProfileState

@Composable
fun HomeScreen(
    onNavigateToResearch: () -> Unit,
    onNavigateToPlacement: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToConnections: () -> Unit,
    onNavigateToPapers: () -> Unit,
    onNavigateToInsights: () -> Unit,
    onMatchClick: (com.example.collisionengine.data.model.ProfileMatch) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<com.example.collisionengine.data.model.ProfileMatch>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("All Collisions") }
    var isPlacementLiked by remember { mutableStateOf(false) }
    
    var isVisible by remember { mutableStateOf(false) }
    val userName by GlobalProfileState.name.collectAsState()
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    val categories = listOf("All Collisions", "Research", "Placement", "Campus Insights")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Top Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(GradientTop, BackgroundLight)
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // 1. Header
            androidx.compose.animation.AnimatedVisibility(
                visible = isVisible,
                enter = androidx.compose.animation.slideInVertically(initialOffsetY = { 50 }, animationSpec = androidx.compose.animation.core.tween(300)) + androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(300))
            ) {
                TopHeader(userName = userName, onNotificationClick = onNavigateToNotifications)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            androidx.compose.animation.AnimatedVisibility(
                visible = isVisible,
                enter = androidx.compose.animation.slideInVertically(initialOffsetY = { 50 }, animationSpec = androidx.compose.animation.core.tween(300, delayMillis = 100)) + androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(300, delayMillis = 100))
            ) {
                Column {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { 
                            searchQuery = it 
                            if (it.length >= 2) {
                                searchResults = com.example.collisionengine.data.network.LocalDatasetClient.searchProfilesByNames(listOf(it))
                            } else {
                                searchResults = emptyList()
                            }
                        },
                        onSearch = {
                            if (searchResults.isNotEmpty()) {
                                onMatchClick(searchResults.first())
                            }
                        }
                    )
                    
                    if (searchResults.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                searchResults.take(5).forEach { match ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { 
                                                onMatchClick(match)
                                                searchQuery = ""
                                                searchResults = emptyList()
                                            }
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(PrimaryBlue.copy(alpha = 0.1f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Filled.Person, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text(match.name, fontWeight = FontWeight.SemiBold, color = TextPrimaryLight)
                                            Text(match.role, fontSize = 12.sp, color = TextSecondaryLight)
                                        }
                                    }
                                    if (match != searchResults.take(5).last()) {
                                        androidx.compose.material3.HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // 3. Quick Actions
            androidx.compose.animation.AnimatedVisibility(
                visible = isVisible,
                enter = androidx.compose.animation.slideInVertically(initialOffsetY = { 50 }, animationSpec = androidx.compose.animation.core.tween(300, delayMillis = 150)) + androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(300, delayMillis = 150))
            ) {
                Column {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimaryLight,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            QuickActionCard(icon = Icons.Filled.Group, label = "Peers", onClick = onNavigateToConnections)
                        }
                        item {
                            QuickActionCard(icon = Icons.Filled.MenuBook, label = "Guide", onClick = onNavigateToPapers)
                        }
                        item {
                            QuickActionCard(icon = Icons.Filled.HelpOutline, label = "What if ?", onClick = onNavigateToPlacement)
                        }
                        item {
                            QuickActionCard(icon = Icons.Filled.Event, label = "Insights", onClick = onNavigateToInsights)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Custom AI Search Card
            androidx.compose.animation.AnimatedVisibility(
                visible = isVisible,
                enter = androidx.compose.animation.slideInVertically(initialOffsetY = { 50 }, animationSpec = androidx.compose.animation.core.tween(300, delayMillis = 200)) + androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(300, delayMillis = 200))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clickable { onNavigateToResearch() },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
                ) {
                    Box(
                        modifier = Modifier.background(
                            Brush.verticalGradient(colors = listOf(SecondaryBlue, PrimaryBlue))
                        )
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                text = "What are you trying to solve?",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Outlined.Search, contentDescription = "Search", tint = Color.White)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Describe your problem, project, or goal...",
                                        color = Color.White.copy(alpha = 0.8f),
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.Mic, contentDescription = "Mic", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Research Papers Feed
            androidx.compose.animation.AnimatedVisibility(
                visible = isVisible,
                enter = androidx.compose.animation.slideInVertically(initialOffsetY = { 50 }, animationSpec = androidx.compose.animation.core.tween(300, delayMillis = 250)) + androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(300, delayMillis = 250))
            ) {
                Column {
                    Text(
                        text = "Recent Research & Ideas",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimaryLight,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ResearchPaperPost(
                        authorName = "Dr. Emily Chen",
                        timeAgo = "2 hours ago",
                        title = "Optimizing LLM Inference on Edge Devices",
                        description = "Explored techniques for quantization and distillation to run large language models on resource-constrained hardware with minimal accuracy loss.",
                        tags = listOf("AI", "Edge Computing", "LLM"),
                        reportUrl = "https://drive.google.com/file/d/1v9d7OcYYVYjsHLAYyFd8_LqvdkwEHOgy/view?usp=drive_link",
                        onProfileClick = {
                            onMatchClick(
                                com.example.collisionengine.data.model.ProfileMatch(
                                    name = "Dr. Emily Chen",
                                    role = "Faculty ΓÇó Computer Science",
                                    matchReasonTitle = "Research Author",
                                    matchReasonText = "Optimizing LLM Inference on Edge Devices; Deep Learning; Neuromorphic Computing; AI Accelerators",
                                    tags = listOf("AI", "Edge Computing", "LLM")
                                )
                            )
                        }
                    )
                    
                    ResearchPaperPost(
                        authorName = "Michael Ross",
                        timeAgo = "5 hours ago",
                        title = "Graph Neural Networks for Social Recommendation",
                        description = "A novel approach leveraging GNNs to improve friend recommendation algorithms by analyzing complex social network topologies.",
                        tags = listOf("GNN", "Social Networks", "ML"),
                        reportUrl = "https://drive.google.com/file/d/1vqsG5rlhJ4f2kqHTtv7lc3cH-J2zT0T6/view?usp=drive_link",
                        onProfileClick = {
                            onMatchClick(
                                com.example.collisionengine.data.model.ProfileMatch(
                                    name = "Michael Ross",
                                    role = "Computer Science ΓÇó Year 4",
                                    matchReasonTitle = "Research Author",
                                    matchReasonText = "Graph Neural Networks for Social Recommendation; Machine Learning; Network Theory; Distributed Systems",
                                    tags = listOf("GNN", "Social Networks", "ML")
                                )
                            )
                        }
                    )
                    
                    ResearchPaperPost(
                        authorName = "Sarah Jenkins",
                        timeAgo = "1 day ago",
                        title = "Sustainable Battery Technologies",
                        description = "Reviewing the latest advancements in solid-state batteries and their potential to replace lithium-ion in the next decade.",
                        tags = listOf("Green Tech", "Hardware", "Energy"),
                        reportUrl = "https://drive.google.com/file/d/1jjd1HKtg578rUSA1dZmH9hUJjncoOOv4/view?usp=drive_link",
                        onProfileClick = {
                            onMatchClick(
                                com.example.collisionengine.data.model.ProfileMatch(
                                    name = "Sarah Jenkins",
                                    role = "Electrical Engineering ΓÇó Year 3",
                                    matchReasonTitle = "Research Author",
                                    matchReasonText = "Sustainable Battery Technologies; Solid-State Batteries; Renewable Energy Systems; Power Electronics",
                                    tags = listOf("Green Tech", "Hardware", "Energy")
                                )
                            )
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp)) // Space for bottom nav
        }
    }
}
