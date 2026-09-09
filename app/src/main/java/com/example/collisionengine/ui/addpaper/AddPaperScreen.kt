package com.example.collisionengine.ui.addpaper

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.collisionengine.ui.theme.BackgroundLight
import com.example.collisionengine.ui.theme.PrimaryBlue
import com.example.collisionengine.ui.theme.TextPrimaryLight
import com.example.collisionengine.ui.theme.TextSecondaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaperScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var authors by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedFileUri = uri
        uri?.let {
            val cursor = context.contentResolver.query(it, null, null, null, null)
            cursor?.use { c ->
                val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (c.moveToFirst() && nameIndex != -1) {
                    selectedFileName = c.getString(nameIndex)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Research Paper", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Research paper title") },
                leadingIcon = { Icon(Icons.Default.Article, contentDescription = null, tint = PrimaryBlue) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimaryLight),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedLabelColor = PrimaryBlue,
                    unfocusedLabelColor = TextSecondaryLight
                ),
                shape = RoundedCornerShape(16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = authors,
                onValueChange = { authors = it },
                label = { Text("Authors (comma separated)") },
                leadingIcon = { Icon(Icons.Default.People, contentDescription = null, tint = PrimaryBlue) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimaryLight),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedLabelColor = PrimaryBlue,
                    unfocusedLabelColor = TextSecondaryLight
                ),
                shape = RoundedCornerShape(16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("Tags/Keywords (comma separated)") },
                leadingIcon = { Icon(Icons.Default.Label, contentDescription = null, tint = PrimaryBlue) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimaryLight),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedLabelColor = PrimaryBlue,
                    unfocusedLabelColor = TextSecondaryLight
                ),
                shape = RoundedCornerShape(16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description of the research paper") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = PrimaryBlue) },
                modifier = Modifier.fillMaxWidth().height(140.dp),
                maxLines = 5,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimaryLight),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedLabelColor = PrimaryBlue,
                    unfocusedLabelColor = TextSecondaryLight
                ),
                shape = RoundedCornerShape(16.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (selectedFileName != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("File Selected", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue)
                            Text(selectedFileName!!, style = MaterialTheme.typography.bodyMedium, color = TextPrimaryLight, maxLines = 1)
                        }
                        IconButton(onClick = { 
                            selectedFileUri = null
                            selectedFileName = null
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = TextSecondaryLight)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Button(
                onClick = { 
                    if (selectedFileName == null) {
                        filePickerLauncher.launch("application/pdf")
                    } else {
                        // Handle the upload / submission
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (selectedFileName == null) {
                    Icon(Icons.Default.UploadFile, contentDescription = "Upload", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select PDF from Files", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.CloudUpload, contentDescription = "Submit", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload and Submit", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
