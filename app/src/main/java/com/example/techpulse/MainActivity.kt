package com.example.techpulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.techpulse.ui.components.NameInputDialog
import com.example.techpulse.ui.main.MainViewModel
import com.example.techpulse.ui.navigation.BottomNavItem
import com.example.techpulse.ui.navigation.Screen
import com.example.techpulse.ui.navigation.TechPulseNav
import com.example.techpulse.ui.theme.TechPulseTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinContext

/**
 * Haupt-Activity der Anwendung TechPulse, die als zentraler Einstiegspunkt fungiert.
 *
 * Verwaltet das globale Basis-Layout inklusive TopAppBar, BottomNavigationBar, Navigations-Controller
 * sowie globale Dialoge (z. B. Namenseingabe-Dialog) und Ladeindikatoren über den [MainViewModel].
 */
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TechPulseTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val isDetailScreen = currentDestination?.hasRoute<Screen.PostDetailRoute>() == true
                val isSettingsScreen = currentDestination?.hasRoute<Screen.Settings>() == true
                val isRepoDetail = currentDestination?.hasRoute<Screen.RepoDetailRoute>() == true

                KoinContext {
                    val mainViewModel: MainViewModel = koinViewModel()
                    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            if (!isDetailScreen && !isSettingsScreen && !isRepoDetail) {
                                CenterAlignedTopAppBar(
                                    title = {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.techpulse_appicon),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = stringResource(id = R.string.app_name),
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    },
                                    actions = {
                                        IconButton(onClick = { navController.navigate(Screen.Settings) }) {
                                            Icon(
                                                imageVector = Icons.Outlined.Settings,
                                                contentDescription = "Settings"
                                            )
                                        }
                                    },
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    )
                                )
                            }
                        },
                        bottomBar = {
                            if (!isDetailScreen && !isSettingsScreen) {
                                NavigationBar {
                                    BottomNavItem.bottomNavItems.forEach { item ->
                                        val isSelected = currentDestination?.hierarchy?.any {
                                            it.route == item.screen::class.qualifiedName
                                        } == true

                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = {
                                                navController.navigate(item.screen) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            },
                                            icon = { Icon(item.icon, contentDescription = item.title) },
                                            label = { Text(item.title) }
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.fillMaxSize()) {
                            TechPulseNav(
                                navController = navController
                            )

                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            if (uiState.showNameDialog) {
                                NameInputDialog(
                                    errorMessage = uiState.errorMessage,
                                    onDismiss = { mainViewModel.onDismissDialog() },
                                    onConfirm = { inputName ->
                                        mainViewModel.onConfirmUsername(inputName)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}