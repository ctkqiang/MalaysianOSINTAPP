package xin.ctkqiang.malaysianosint.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xin.ctkqiang.malaysianosint.ui.MainViewModel
import xin.ctkqiang.malaysianosint.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val themeMode by viewModel.themeMode.collectAsState()
    val autoTranslate by viewModel.autoTranslate.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = { Text("设置", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
        )

        Column(modifier = Modifier.padding(16.dp)) {
            /** 主题模式 */
            Text("主题模式", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))

            ThemeMode.entries.forEach { mode ->
                val label = when (mode) {
                    ThemeMode.AUTO -> "跟随系统"
                    ThemeMode.LIGHT -> "浅色模式"
                    ThemeMode.DARK -> "深色模式"
                }
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.setThemeMode(mode) }.padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = themeMode == mode,
                        onClick = { viewModel.setThemeMode(mode) },
                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outline)

            /** 自动翻译 */
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text("自动翻译", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)
                    Text("API 返回内容自动翻译为中文", fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = autoTranslate,
                    onCheckedChange = { viewModel.setAutoTranslate(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary),
                )
            }
        }
    }
}
