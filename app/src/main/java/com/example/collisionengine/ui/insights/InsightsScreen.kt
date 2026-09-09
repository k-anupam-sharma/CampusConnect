package com.example.collisionengine.ui.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.collisionengine.ui.theme.BackgroundLight
import com.example.collisionengine.ui.theme.PrimaryBlue
import com.example.collisionengine.ui.theme.TextPrimaryLight
import com.example.collisionengine.ui.theme.TextSecondaryLight

data class BranchInsight(
    val name: String,
    val paperCount: Int,
    val topAuthor: String
)

val mockInsights = listOf(
    BranchInsight("Computer Science", 145, "Dr. Emily Chen"),
    BranchInsight("Electrical Engineering", 112, "Dr. Alan Turing"),
    BranchInsight("Mechanical Engineering", 85, "Dr. Sarah Connor"),
    BranchInsight("Bio-Technology", 74, "Dr. Robert Neville"),
    BranchInsight("Civil Engineering", 50, "Dr. Emmett Brown"),
    BranchInsight("Data Science", 120, "Rahul Bose")
).sortedByDescending { it.paperCount }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("College Insights", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight
                )
            )
        },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Research Output by Branch",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimaryLight,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(mockInsights.withIndex().toList()) { (index, insight) ->
                    InsightCard(insight = insight, rank = index + 1)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun InsightCard(insight: BranchInsight, rank: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = PrimaryBlue.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (rank == 1) Color(0xFFFFD700).copy(alpha = 0.2f)
                        else PrimaryBlue.copy(alpha = 0.1f), 
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (rank == 1) {
                    Icon(Icons.Filled.EmojiEvents, contentDescription = "Top", tint = Color(0xFFB8860B), modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "#$rank",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = insight.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Top Author: ${insight.topAuthor}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryLight
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${insight.paperCount}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                Text(
                    text = "Papers",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryLight
                )
            }
        }
    }
}
