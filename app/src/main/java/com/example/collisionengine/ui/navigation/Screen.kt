package com.example.collisionengine.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object Research : Screen("research?query={query}") {
        fun createRoute(query: String? = null): String {
            return if (query != null) "research?query=${android.net.Uri.encode(query)}" else "research"
        }
    }
    object Placement : Screen("placement")
    
    // Pass query encoded to avoid breaking the navigation string
    object Results : Screen("results/{type}/{query}") {
        fun createRoute(type: String, query: String) = "results/$type/${android.net.Uri.encode(query)}"
    }
    
    object Explanation : Screen("explanation/{name}/{role}/{reason}/{score}") {
        fun createRoute(name: String, role: String, reason: String, score: Int): String {
            return "explanation/${android.net.Uri.encode(name)}/${android.net.Uri.encode(role)}/${android.net.Uri.encode(reason)}/$score"
        }
    }
    
    object Conversation : Screen("conversation/{name}/{reason}") {
        fun createRoute(name: String, reason: String): String {
            return "conversation/${android.net.Uri.encode(name)}/${android.net.Uri.encode(reason)}"
        }
    }

    object Chat : Screen("chat/{name}") {
        fun createRoute(name: String) = "chat/${android.net.Uri.encode(name)}"
    }
    
    // New requested screens
    object Messages : Screen("messages")
    object Profile : Screen("profile")
    object Notifications : Screen("notifications")
    object AddPaper : Screen("add_paper")
    object Connections : Screen("connections")
    object PdfViewer : Screen("pdf_viewer")
    object Insights : Screen("insights")
}
