package com.example.collisionengine.ui.login

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.example.collisionengine.ui.theme.BackgroundLight
import com.example.collisionengine.ui.theme.GradientTop
import com.example.collisionengine.ui.theme.PrimaryBlue
import com.example.collisionengine.ui.theme.SurfaceLight
import com.example.collisionengine.ui.theme.TextPrimaryLight
import com.example.collisionengine.ui.theme.TextSecondaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel(),
    initialIsSplash: Boolean = false // Set this to true from navigation if needed, or default true if we replace Splash
) {
    val uiState by viewModel.uiState.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isSignUpMode by remember { mutableStateOf(false) }
    var role by remember { mutableStateOf("") }

    var isSplashMode by remember { mutableStateOf(initialIsSplash) }
    var splashSubtitleVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = initialIsSplash) {
        if (initialIsSplash) {
            delay(500L)
            splashSubtitleVisible = true
            delay(2000L)
            splashSubtitleVisible = false
            
            if (com.example.collisionengine.data.state.GlobalProfileState.isLoggedIn.value) {
                onLoginSuccess()
            } else {
                isSplashMode = false
            }
        }
    }

    // Animations
    val titleOffsetY by animateDpAsState(
        targetValue = if (isSplashMode) 350.dp else 100.dp,
        animationSpec = tween(2500, easing = FastOutSlowInEasing), label = "titleOffset"
    )
    val titleColor by animateColorAsState(
        targetValue = if (isSplashMode) Color.White else PrimaryBlue,
        animationSpec = tween(2500), label = "titleColor"
    )
    val loginAlpha by animateFloatAsState(
        targetValue = if (isSplashMode) 0f else 1f,
        animationSpec = tween(2000, delayMillis = 400, easing = LinearEasing), label = "loginAlpha"
    )
    val splashBackgroundAlpha by animateFloatAsState(
        targetValue = if (isSplashMode) 1f else 0f,
        animationSpec = tween(1800, easing = LinearEasing), label = "splashBgAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Login Top Gradient Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(GradientTop, BackgroundLight)
                    )
                )
        )

        // Splash Background overlay that fades out
        if (splashBackgroundAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(splashBackgroundAlpha)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(PrimaryBlue, Color.White)
                        )
                    )
            )
        }
        
        // Splash Subtitle
        AnimatedVisibility(
            visible = splashSubtitleVisible,
            enter = fadeIn(animationSpec = tween(1000)),
            exit = fadeOut(animationSpec = tween(500)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {
            Text(
                text = "Find the people who've been\nwhere you're going.",
                style = MaterialTheme.typography.bodyLarge,
                color = PrimaryBlue,
                textAlign = TextAlign.Center
            )
        }

        // Animated Title
        Text(
            text = "Campus Connect",
            style = MaterialTheme.typography.displayMedium.copy(
                fontFamily = FontFamily.Monospace
            ),
            color = titleColor,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = titleOffsetY)
        )

        // Login Card
        if (loginAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp)
                    .alpha(loginAlpha),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .shadow(elevation = 16.dp, shape = RoundedCornerShape(32.dp), spotColor = Color.Black.copy(alpha = 0.1f))
                        .background(SurfaceLight, shape = RoundedCornerShape(32.dp))
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isSignUpMode) "Sign Up" else "Login",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                    
                    Text(
                        text = if (isSignUpMode) "Create an account to get started" else "Login to your account to continue",
                        color = TextSecondaryLight,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                    )

                    // Username Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Username / Name", color = TextPrimaryLight, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            placeholder = { Text("e.g. Tanya Kulkarni", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = PrimaryBlue) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedContainerColor = BackgroundLight,
                                focusedContainerColor = BackgroundLight,
                                focusedTextColor = TextPrimaryLight,
                                unfocusedTextColor = TextPrimaryLight
                            ),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Password", color = TextPrimaryLight, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("••••••••", color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = PrimaryBlue) },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = Color.Gray
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = PrimaryBlue,
                                unfocusedContainerColor = BackgroundLight,
                                focusedContainerColor = BackgroundLight,
                                focusedTextColor = TextPrimaryLight,
                                unfocusedTextColor = TextPrimaryLight
                            ),
                            singleLine = true
                        )
                    }

                    if (isSignUpMode) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Role Field
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Role", color = TextPrimaryLight, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = role,
                                onValueChange = { role = it },
                                placeholder = { Text("e.g. Student, Researcher", color = Color.Gray) },
                                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = PrimaryBlue) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedContainerColor = BackgroundLight,
                                    focusedContainerColor = BackgroundLight,
                                    focusedTextColor = TextPrimaryLight,
                                    unfocusedTextColor = TextPrimaryLight
                                ),
                                singleLine = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isSignUpMode) "Already have an account? Login" else "Don't have an account? Sign Up",
                        color = PrimaryBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(vertical = 8.dp)
                            .clickable { isSignUpMode = !isSignUpMode }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Login Button
                    Button(
                        onClick = {
                            if (isSignUpMode) {
                                viewModel.signUp(username, role, onLoginSuccess)
                            } else {
                                viewModel.login(username, onLoginSuccess)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            disabledContainerColor = Color.Gray
                        ),
                        enabled = uiState !is LoginUiState.Loading
                    ) {
                        if (uiState is LoginUiState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(if (isSignUpMode) "Sign Up" else "Login", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (uiState is LoginUiState.Error) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = (uiState as LoginUiState.Error).message,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Bottom security text
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .alpha(loginAlpha),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = TextSecondaryLight, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Your data is secure with us", color = TextSecondaryLight, fontSize = 12.sp)
            }
        }
    }
}
