package com.example.collisionengine.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.collisionengine.ui.components.CustomBottomNavBar
import com.example.collisionengine.ui.home.HomeScreen

import com.example.collisionengine.ui.messages.MessagesScreen
import com.example.collisionengine.ui.profile.ProfileScreen
import com.example.collisionengine.ui.login.LoginScreen
import com.example.collisionengine.data.state.GlobalProfileState
import androidx.compose.runtime.LaunchedEffect
import com.example.collisionengine.ui.notifications.NotificationsScreen
import com.example.collisionengine.ui.addpaper.AddPaperScreen

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomNav = currentRoute in listOf(
        Screen.Home.route,
        Screen.Research.route,
        Screen.Placement.route,
        Screen.Messages.route,
        Screen.Profile.route,
        Screen.AddPaper.route
    )
    
    val backgroundColor = com.example.collisionengine.ui.theme.BackgroundLight // Light

    val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)
    val isKeyboardOpen = imeBottom > 0
    Scaffold(
        containerColor = backgroundColor,
        bottomBar = {
            androidx.compose.animation.AnimatedVisibility(
                visible = showBottomNav && !isKeyboardOpen,
                enter = androidx.compose.animation.slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = androidx.compose.animation.core.tween(500, delayMillis = 500)
                ) + androidx.compose.animation.fadeIn(
                    animationSpec = androidx.compose.animation.core.tween(500, delayMillis = 500)
                ),
                exit = androidx.compose.animation.slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = androidx.compose.animation.core.tween(300)
                ) + androidx.compose.animation.fadeOut(
                    animationSpec = androidx.compose.animation.core.tween(300)
                )
            ) {
                val navBarRoute = when (currentRoute) {
                    Screen.Home.route -> "home"
                    Screen.Research.route -> "research"
                    Screen.Messages.route, Screen.Chat.route -> "messages"
                    Screen.Profile.route -> "profile"
                    Screen.AddPaper.route -> "add_paper"
                    else -> "home"
                }
                CustomBottomNavBar(
                    currentRoute = navBarRoute,
                    onNavigate = { route ->
                        when (route) {
                            "home" -> {
                                if (currentRoute != Screen.Home.route) {
                                    navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Home.route) { inclusive = true }
                                    }
                                }
                            }
                            "research" -> navController.navigate(Screen.Research.route)
                            "messages" -> navController.navigate(Screen.Messages.route)
                            "profile" -> navController.navigate(Screen.Profile.route)
                            "add_paper" -> navController.navigate(Screen.AddPaper.route)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Content extends behind the floating bottom bar
        ) {
            val bottomNavRoutes = listOf(Screen.Home.route, Screen.Research.route, Screen.Messages.route, Screen.Profile.route)
            
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route,
                modifier = Modifier.fillMaxSize(),
                enterTransition = { 
                    val initialIndex = bottomNavRoutes.indexOf(initialState.destination.route)
                    val targetIndex = bottomNavRoutes.indexOf(targetState.destination.route)
                    val isForward = if (initialIndex != -1 && targetIndex != -1) targetIndex > initialIndex else true
                    slideInHorizontally(initialOffsetX = { if (isForward) 1000 else -1000 }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)) 
                },
                exitTransition = { 
                    val initialIndex = bottomNavRoutes.indexOf(initialState.destination.route)
                    val targetIndex = bottomNavRoutes.indexOf(targetState.destination.route)
                    val isForward = if (initialIndex != -1 && targetIndex != -1) targetIndex > initialIndex else true
                    slideOutHorizontally(targetOffsetX = { if (isForward) -1000 else 1000 }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300)) 
                },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)) },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(300)) + fadeOut(animationSpec = tween(300)) }
            ) {
                composable(Screen.Splash.route) {
                    LoginScreen(
                        initialIsSplash = true,
                        onLoginSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    )
                }
                
                composable(Screen.Login.route) {
                    LoginScreen(
                        initialIsSplash = false,
                        onLoginSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    )
                }

                composable(route = Screen.Home.route) {
                    HomeScreen(
                        onNavigateToResearch = { query -> navController.navigate(Screen.Research.createRoute(query)) },
                        onNavigateToPlacement = { navController.navigate(Screen.Placement.route) },
                        onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                        onNavigateToConnections = { navController.navigate(Screen.Connections.route) },
                        onNavigateToPapers = { navController.navigate(Screen.PdfViewer.route) },
                        onNavigateToInsights = { navController.navigate(Screen.Insights.route) },
                        onMatchClick = { match ->
                            val reasonText = match.matchReasonText.takeIf { it.isNotBlank() } ?: "Direct search match."
                            val encodedReason = java.net.URLEncoder.encode(reasonText, "UTF-8")
                            navController.navigate(
                                Screen.Explanation.createRoute(
                                    name = match.name,
                                    role = match.role,
                                    reason = encodedReason,
                                    score = 100
                                )
                            )
                        }
                    )
                }
                composable(
                    route = Screen.Research.route,
                    arguments = listOf(androidx.navigation.navArgument("query") { 
                        type = androidx.navigation.NavType.StringType
                        nullable = true 
                    })
                ) { backStackEntry ->
                    val query = backStackEntry.arguments?.getString("query")
                    val viewModel: com.example.collisionengine.ui.research.ResearchViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                    com.example.collisionengine.ui.research.ResearchScreen(
                        initialQuery = query,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onFindCollisions = { query -> 
                            navController.navigate(Screen.Results.createRoute("Research", query))
                        },
                        onMatchClick = { match ->
                            navController.navigate(
                                Screen.Explanation.createRoute(
                                    name = match.name,
                                    role = match.role,
                                    reason = match.matchReasonText,
                                    score = 98
                                )
                            )
                        }
                    )
                }
                composable(route = Screen.Placement.route) {
                    val viewModel: com.example.collisionengine.ui.placement.PlacementViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                    com.example.collisionengine.ui.placement.PlacementScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onFindCollisions = { query -> 
                            navController.navigate(Screen.Results.createRoute("Placement", query))
                        },
                        onMatchClick = { match ->
                            navController.navigate(
                                Screen.Explanation.createRoute(
                                    name = match.name,
                                    role = match.role,
                                    reason = match.matchReasonText,
                                    score = 98
                                )
                            )
                        }
                    )
                }
                composable(route = Screen.Messages.route) {
                    MessagesScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToChat = { name ->
                            navController.navigate(Screen.Chat.createRoute(name))
                        }
                    )
                }
                composable(route = Screen.Profile.route) {
                    ProfileScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onLogout = {
                            GlobalProfileState.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
                composable(route = Screen.Connections.route) {
                    com.example.collisionengine.ui.profile.ConnectionsScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToChat = { name -> navController.navigate(Screen.Chat.createRoute(name)) }
                    )
                }
                composable(route = Screen.Notifications.route) {
                    NotificationsScreen(onNavigateBack = { navController.popBackStack() })
                }
                composable(route = Screen.AddPaper.route) {
                    AddPaperScreen(onNavigateBack = { navController.popBackStack() })
                }
                composable(route = Screen.PdfViewer.route) {
                    com.example.collisionengine.ui.pdf.PdfViewerScreen(onNavigateBack = { navController.popBackStack() })
                }
                composable(route = Screen.Insights.route) {
                    com.example.collisionengine.ui.insights.InsightsScreen(onNavigateBack = { navController.popBackStack() })
                }
                
                composable(
                    route = Screen.Results.route,
                    arguments = listOf(
                        androidx.navigation.navArgument("type") { type = androidx.navigation.NavType.StringType },
                        androidx.navigation.navArgument("query") { type = androidx.navigation.NavType.StringType }
                    )
                ) { backStackEntry ->
                    val type = backStackEntry.arguments?.getString("type") ?: ""
                    val query = backStackEntry.arguments?.getString("query") ?: ""
                    
                    val factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
                        androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application
                    )
                    val viewModel: com.example.collisionengine.ui.results.ResultsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = factory)
                    
                    com.example.collisionengine.ui.results.ResultsScreen(
                        query = query,
                        searchType = type,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onMatchSelected = { match ->
                            navController.navigate(
                                Screen.Explanation.createRoute(
                                    name = match.personName,
                                    role = match.roleTitle,
                                    reason = match.matchReason,
                                    score = match.score
                                )
                            )
                        }
                    )
                }
                
                composable(
                    route = Screen.Explanation.route,
                    arguments = listOf(
                        androidx.navigation.navArgument("name") { type = androidx.navigation.NavType.StringType },
                        androidx.navigation.navArgument("role") { type = androidx.navigation.NavType.StringType },
                        androidx.navigation.navArgument("reason") { type = androidx.navigation.NavType.StringType },
                        androidx.navigation.navArgument("score") { type = androidx.navigation.NavType.IntType }
                    )
                ) { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    val role = backStackEntry.arguments?.getString("role") ?: ""
                    val encodedReason = backStackEntry.arguments?.getString("reason") ?: ""
                    val reason = try { java.net.URLDecoder.decode(encodedReason, "UTF-8") } catch (e: Exception) { encodedReason }
                    val score = backStackEntry.arguments?.getInt("score") ?: 0
                    
                    com.example.collisionengine.ui.explanation.ExplanationScreen(
                        name = name,
                        role = role,
                        reason = reason,
                        score = score,
                        onNavigateBack = { navController.popBackStack() },
                        onStartConversation = {
                            navController.navigate(Screen.Conversation.createRoute(name, reason))
                        }
                    )
                }
                
                composable(
                    route = Screen.Conversation.route,
                    arguments = listOf(
                        androidx.navigation.navArgument("name") { type = androidx.navigation.NavType.StringType },
                        androidx.navigation.navArgument("reason") { type = androidx.navigation.NavType.StringType }
                    )
                ) { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("name") ?: "Unknown"
                    val reason = backStackEntry.arguments?.getString("reason") ?: "Unknown reason"
                    val viewModel: com.example.collisionengine.ui.conversation.ConversationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                    com.example.collisionengine.ui.conversation.ConversationScreen(
                        name = name,
                        reason = reason,
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToChat = { navController.navigate(Screen.Chat.createRoute(name)) }
                    )
                }
                composable(
                    route = Screen.Chat.route,
                    arguments = listOf(androidx.navigation.navArgument("name") { type = androidx.navigation.NavType.StringType })
                ) { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("name") ?: "Unknown"
                    com.example.collisionengine.ui.chat.ChatScreen(
                        name = name,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
