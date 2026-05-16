/* 马来西亚OSINT — 底部导航 + 路由图 */

package com.osint.malaysia.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.osint.malaysia.ui.screens.*
import com.osint.malaysia.ui.theme.NavyBlue
import com.osint.malaysia.viewmodel.MainViewModel
import com.osint.malaysia.viewmodel.SettingsViewModel

/* 导航路由 */
object Routes {
    const val HOME = "home"
    const val ID_CHECK = "id_check"
    const val COMPANY = "company"
    const val SOCIAL = "social"
    const val COURT = "court"
    const val SETTINGS = "settings"
}

/* 底部导航项 */
sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(Routes.HOME, "首页", Icons.Default.Security)
    data object IDCheck : BottomNavItem(Routes.ID_CHECK, "身份证", Icons.Default.Badge)
    data object Company : BottomNavItem(Routes.COMPANY, "企业", Icons.Default.Business)
    data object Social : BottomNavItem(Routes.SOCIAL, "社交", Icons.Default.Public)
    data object Court : BottomNavItem(Routes.COURT, "法庭", Icons.Default.Gavel)
    data object Settings : BottomNavItem(Routes.SETTINGS, "设置", Icons.Default.Settings)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.IDCheck,
    BottomNavItem.Company,
    BottomNavItem.Social,
    BottomNavItem.Court,
    BottomNavItem.Settings
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(
    viewModel: MainViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = NavyBlue.N950,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "马来西亚OSINT",
                        color = NavyBlue.N50,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyBlue.N900,
                    titleContentColor = NavyBlue.N50
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = NavyBlue.N900,
                contentColor = NavyBlue.N200
            ) {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        },
                        label = {
                            Text(
                                item.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(Routes.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavyBlue.N200,
                            selectedTextColor = NavyBlue.N200,
                            unselectedIconColor = NavyBlue.N500,
                            unselectedTextColor = NavyBlue.N500,
                            indicatorColor = NavyBlue.N700
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) { HomeScreen(viewModel) }
            composable(Routes.ID_CHECK) { IDCheckScreen(viewModel) }
            composable(Routes.COMPANY) { CompanyScreen(viewModel) }
            composable(Routes.SOCIAL) { SocialScreen(viewModel) }
            composable(Routes.COURT) { CourtScreen(viewModel) }
            composable(Routes.SETTINGS) { SettingsScreen(settingsViewModel) }
        }
    }
}
