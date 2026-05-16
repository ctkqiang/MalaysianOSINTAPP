package xin.ctkqiang.malaysianosint.ui.navigation

import androidx.compose.foundation.clickable
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

enum class BottomTab(val label: String, val icon: ImageVector) {
    HOME("首页", Icons.Filled.Search),
    SETTINGS("设置", Icons.Filled.Settings),
    ABOUT("关于", Icons.Filled.Info),
}

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(BottomTab.HOME) }
    var showSettings by remember { mutableStateOf(false) }

    if (showSettings) {
        SettingsScreen(viewModel = viewModel, onBack = { showSettings = false })
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("马来西亚 OSINT", color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Filled.Settings, "设置", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    BottomTab.entries.forEach { tab ->
                        val sel = selectedTab == tab
                        Column(
                            modifier = Modifier.clickable { selectedTab = tab }
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                tab.icon, contentDescription = tab.label,
                                tint = if (sel) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                tab.label,
                                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                                color = if (sel) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
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
