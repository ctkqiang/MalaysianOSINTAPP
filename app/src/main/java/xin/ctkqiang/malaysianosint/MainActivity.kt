package xin.ctkqiang.malaysianosint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import xin.ctkqiang.malaysianosint.ui.MainViewModel
import xin.ctkqiang.malaysianosint.ui.navigation.AppNavigation
import xin.ctkqiang.malaysianosint.ui.theme.MalaysianOSINTTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }

        setContent {
            val viewModel: MainViewModel = viewModel()
            val themeMode by viewModel.themeMode.collectAsState()

            MalaysianOSINTTheme(themeMode = themeMode) {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}
