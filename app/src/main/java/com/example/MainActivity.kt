package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.ui.screens.AiChatScreen
import com.example.ui.screens.AiToolsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.ExamScreen
import com.example.ui.screens.FocusTimerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PlannerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SocialScreen
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.StudyFlowTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.StudyFlowViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    private val viewModel: StudyFlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StudyFlowTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

data class NavItem(
    val screen: AppScreen,
    val title: String,
    val icon: ImageVector,
    val tag: String
)

@Composable
fun MainApp(viewModel: StudyFlowViewModel) {
    var authUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var isGuestMode by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            authUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    val userProfile by viewModel.userProfile.collectAsState()
    var isOnboarded by remember { mutableStateOf(userProfile.isOnboarded) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when {
            authUser == null && !isGuestMode -> {
                AuthScreen(
                    onAuthSuccess = {
                        authUser = Firebase.auth.currentUser
                        isGuestMode = true
                    },
                    onContinueAsGuest = {
                        isGuestMode = true
                    }
                )
            }
            !isOnboarded -> {
                OnboardingScreen(
                    viewModel = viewModel,
                    onFinish = {
                        isOnboarded = true
                    }
                )
            }
            else -> {
                AuthenticatedScaffold(
                    viewModel = viewModel,
                    onSignOut = {
                        isGuestMode = false
                        authUser = null
                    }
                )
            }
        }
    }
}

@Composable
fun AuthenticatedScaffold(
    viewModel: StudyFlowViewModel,
    onSignOut: () -> Unit
) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        viewModel.handleBack()
    }

    val navItems = listOf(
        NavItem(AppScreen.HOME, "Today", Icons.Default.Home, "nav_home"),
        NavItem(AppScreen.PLANNER, "Planner", Icons.Default.CalendarToday, "nav_planner"),
        NavItem(AppScreen.FOCUS, "Focus", Icons.Default.Timer, "nav_focus"),
        NavItem(AppScreen.AI_CHAT, "AI Chat", Icons.Default.AutoAwesome, "nav_ai_chat"),
        NavItem(AppScreen.AI_TOOLS, "Tools", Icons.Default.Psychology, "nav_tools")
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                navItems.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateTo(item.screen) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandPrimary,
                            selectedTextColor = BrandPrimary,
                            indicatorColor = BrandPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag(item.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                AppScreen.PLANNER -> PlannerScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                AppScreen.FOCUS -> FocusTimerScreen(
                    viewModel = viewModel
                )
                AppScreen.EXAMS -> ExamScreen(
                    viewModel = viewModel
                )
                AppScreen.AI_CHAT -> AiChatScreen(
                    viewModel = viewModel
                )
                AppScreen.AI_TOOLS -> AiToolsScreen(
                    viewModel = viewModel
                )
                AppScreen.INSIGHTS -> InsightsScreen(
                    viewModel = viewModel
                )
                AppScreen.SOCIAL -> SocialScreen(
                    viewModel = viewModel
                )
                AppScreen.SETTINGS -> SettingsScreen(
                    viewModel = viewModel,
                    onSignOut = onSignOut
                )
            }
        }
    }
}
