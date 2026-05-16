package xin.ctkqiang.malaysianosint.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import xin.ctkqiang.malaysianosint.ui.screens.AboutScreen
import xin.ctkqiang.malaysianosint.ui.screens.HomeScreen
import xin.ctkqiang.malaysianosint.ui.screens.SettingsScreen
import xin.ctkqiang.malaysianosint.ui.MainViewModel

/** 底部导航标签定义 */
enum class BottomTab(val label: String, val icon: ImageVector) {
    HOME("首页", Icons.Filled.Search),
    SETTINGS("设置", Icons.Filled.Settings),
    ABOUT("关于", Icons.Filled.Info),
}

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                BottomTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                BottomTab.HOME -> HomeScreen(viewModel)
                BottomTab.SETTINGS -> SettingsScreen(viewModel)
                BottomTab.ABOUT -> AboutScreen()
            }
        }
    }
}
