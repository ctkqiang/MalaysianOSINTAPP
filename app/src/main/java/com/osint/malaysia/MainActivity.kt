/* 马来西亚OSINT — 主Activity入口 */

package com.osint.malaysia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.osint.malaysia.ui.navigation.MainNavigation
import com.osint.malaysia.ui.theme.MalaysianOSINTTheme
import com.osint.malaysia.util.LogUtil
import com.osint.malaysia.viewmodel.MainViewModel
import com.osint.malaysia.viewmodel.SettingsViewModel
import com.osint.malaysia.viewmodel.ThemeMode

class MainActivity : ComponentActivity() {

    private val tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.i(tag, "马来西亚OSINT应用启动")

        enableEdgeToEdge()

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.Factory(applicationContext)
            )
            val mainViewModel: MainViewModel = viewModel()
            val themeMode by settingsViewModel.themeMode.collectAsState()

            val useDarkTheme = when (themeMode) {
                ThemeMode.AUTO -> androidx.compose.foundation.isSystemInDarkTheme()
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
            }

            MalaysianOSINTTheme(darkTheme = useDarkTheme) {
                MainNavigation(
                    viewModel = mainViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtil.i(tag, "马来西亚OSINT应用关闭")
    }
}
