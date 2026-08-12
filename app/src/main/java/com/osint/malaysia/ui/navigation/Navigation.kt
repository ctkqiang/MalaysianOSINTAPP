package com.osint.malaysia.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.osint.malaysia.ui.screens.*
import com.osint.malaysia.ui.theme.Colors
import com.osint.malaysia.util.LocalStrings
import com.osint.malaysia.viewmodel.MainViewModel
import com.osint.malaysia.viewmodel.SettingsViewModel

object Routes {
    const val HOME = "home"; const val ID = "id"; const val COMPANY = "company"
    const val SOCIAL = "social"; const val COURT = "court"; const val SETTINGS = "settings"
}

private data class Nav(val route: String, val icon: ImageVector) {
    companion object {
        val home = Nav(Routes.HOME, Icons.Default.Home)
        val id = Nav(Routes.ID, Icons.Default.CreditCard)
        val company = Nav(Routes.COMPANY, Icons.Default.Business)
        val social = Nav(Routes.SOCIAL, Icons.Default.Language)
        val court = Nav(Routes.COURT, Icons.Default.Balance)
        val settings = Nav(Routes.SETTINGS, Icons.Default.Settings)
        val all = listOf(home, id, company, social, court, settings)
    }
}

private fun label(n: Nav, s: com.osint.malaysia.util.UiStrings) = when (n.route) {
    Routes.HOME -> s.navHome; Routes.ID -> s.navId; Routes.COMPANY -> s.navCompany
    Routes.SOCIAL -> s.navSocial; Routes.COURT -> s.navCourt; Routes.SETTINGS -> s.navSettings
    else -> ""
}

private val enter = fadeIn(tween(300)) + slideInHorizontally(tween(350)) { it / 10 }
private val exit = fadeOut(tween(200)) + slideOutHorizontally(tween(250)) { -it / 10 }
private val popEnter = fadeIn(tween(300)) + slideInHorizontally(tween(350)) { -it / 10 }
private val popExit = fadeOut(tween(200)) + slideOutHorizontally(tween(250)) { it / 10 }

@Composable
fun MainNavigation(vm: MainViewModel, svm: SettingsViewModel) {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route
    val s = LocalStrings.current
    val haptic = LocalHapticFeedback.current

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                Surface(color = MaterialTheme.colorScheme.background, shadowElevation = 0.dp) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp).statusBarsPadding().height(56.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(s.appName, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                    }
                }
            },
            bottomBar = {
                Surface(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f), shadowElevation = 8.dp, tonalElevation = 4.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) {
                    Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Nav.all.forEach { item ->
                            val sel = route == item.route
                            NavigationBarItem(
                                icon = { Icon(item.icon, label(item, s), tint = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                                label = { Text(label(item, s), style = MaterialTheme.typography.labelSmall, color = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                                selected = sel,
                                onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); if (!sel) nav.navigate(item.route) { popUpTo(Routes.HOME) { saveState = true }; launchSingleTop = true; restoreState = true } },
                                colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                            )
                        }
                    }
                }
            }
        ) { ip ->
            NavHost(nav, Routes.HOME, Modifier.padding(ip), enterTransition = { enter }, exitTransition = { exit }, popEnterTransition = { popEnter }, popExitTransition = { popExit }) {
                composable(Routes.HOME) { HomeScreen(vm) }
                composable(Routes.ID) { IDCheckScreen(vm) }
                composable(Routes.COMPANY) { CompanyScreen(vm) }
                composable(Routes.SOCIAL) { SocialScreen(vm) }
                composable(Routes.COURT) { CourtScreen(vm, nav) }
                composable(Routes.SETTINGS) { SettingsScreen(svm) }
            }
        }
    }
}
