package com.example.collisionengine.ui.pdf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collisionengine.ui.theme.BackgroundLight
import com.example.collisionengine.ui.theme.TextPrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Research Paper Guide") },
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = Color.LightGray),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Campus Connect\nHow to Write a Research Paper",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "A Step-by-Step Guide for Beginners",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimaryLight,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Divider(color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "This guide walks first-time researchers through every section a research paper needs, why each part matters, and practical tips for writing each one — from choosing a topic to submitting your final draft.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimaryLight,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    PdfSection(
                        title = "1. What Is a Research Paper?",
                        content = "A research paper is a structured written document that presents original findings, an analysis, or a review of existing work on a specific question or problem. Unlike a general essay, it follows a well-defined format, relies on evidence and citations, and is written for an audience of peers, reviewers, or instructors who expect rigor and clarity.\n\nCommon types of research papers include:\n• Empirical / experimental papers — report new data collected through experiments, surveys, or simulations.\n• Review papers — summarize and critically analyze existing research on a topic.\n• Case studies — examine a specific instance, system, or event in depth.\n• Theoretical / conceptual papers — propose new models, frameworks, or algorithms without new experimental data."
                    )

                    PdfSection(
                        title = "2. Before You Start Writing",
                        content = "A few things to sort out before you write a single word:\n• Choose a focused topic. Narrow enough to cover thoroughly, broad enough to find sufficient prior work.\n• Do a literature search. Read 15–30 related papers to understand what's already known and where the gap is.\n• Define your research question or hypothesis. This single sentence should guide every section you write.\n• Pick a target venue or format. A conference, journal, or course has its own formatting and length rules — check them early.\n• Choose your citation style. Common ones are IEEE, APA, MLA, and ACM — confirm which one your venue requires.\n\nTip: Keep a running spreadsheet of every paper you read: title, authors, year, key finding, and how it relates to your question. This becomes your literature review later."
                    )

                    PdfSection(
                        title = "3. The Standard Structure of a Research Paper",
                        content = "Most research papers — especially in science, engineering, and computer science — follow the IMRaD structure (Introduction, Methods, Results, and Discussion), wrapped with a title, abstract, and references. Below is what each required section should contain.\n\nTitle\n• Concise (usually under 15 words), specific, and descriptive of the actual contribution.\n• Avoid vague titles like \"A Study on Machine Learning\" — be specific about what, how, and on what data/system.\n• Avoid unnecessary jargon or abbreviations that a first-time reader wouldn't recognize.\n\nAbstract\n• A single paragraph (150–250 words) summarizing the problem, method, key results, and conclusion.\n• Should be readable on its own — many readers decide whether to continue based on the abstract alone.\n• Written last, even though it appears first.\n\n1. Introduction\n• States the problem and why it matters (motivation).\n• Briefly reviews relevant prior work and identifies the specific gap your paper addresses.\n• States your research question, objective, or hypothesis clearly.\n• Summarizes your contribution and, often, the structure of the rest of the paper.\n\n2. Literature Review / Related Work\n• Organizes prior research thematically or chronologically — not just a list of summaries.\n• Explains how existing approaches fall short and how your work differs or builds on them.\n• Every claim about prior work needs a citation.\n\n3. Methodology / Methods\n• Describes exactly what you did: data sources, tools, algorithms, experimental setup, or hardware.\n• Should be detailed enough that another researcher could reproduce your work.\n• Justify your choices (e.g., why this dataset, this architecture, this measurement technique).\n\n4. Results\n• Presents what you found — using tables, charts, and figures — without yet interpreting them.\n• Report both positive and negative or unexpected results honestly.\n• Use statistical measures or benchmarks appropriate to your field to support claims.\n\n5. Discussion\n• Interprets the results: what do they mean in relation to your research question?\n• Compares your findings with prior work discussed in the literature review.\n• Acknowledges limitations, sources of error, or threats to validity.\n\n6. Conclusion\n• Restates the key findings and their significance in a few sentences.\n• Suggests directions for future work.\n• Does not introduce new results or citations."
                    )

                    PdfSection(
                        title = "4. Practical Writing Tips for Beginners",
                        content = "• Write the abstract and introduction last. It's much easier to summarize a paper once it's written.\n• Use clear, simple sentences. Academic writing rewards clarity over complexity.\n• Be consistent with terminology. Don't switch between synonyms for the same concept (e.g., \"model\" vs. \"algorithm\") without reason.\n• Use figures and tables to reduce text. A well-labeled chart often communicates faster than a paragraph.\n• Cite as you write, not after. Retroactively finding sources for claims you've already written is time-consuming and risky.\n• Avoid plagiarism. Always paraphrase in your own words and cite the original source, even for ideas (not just direct quotes).\n• Get feedback early. Share a draft with your advisor or peers before your final revision pass.\n• Proofread for grammar and formatting — but do a full re-read for logical flow as well, not just typos."
                    )
                }
            }
        }
    }
}

@Composable
fun PdfSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = com.example.collisionengine.ui.theme.PrimaryBlue,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimaryLight,
            lineHeight = 24.sp
        )
    }
}
