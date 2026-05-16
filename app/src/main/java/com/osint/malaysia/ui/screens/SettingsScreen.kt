/* 马来西亚OSINT — 设置页面 */

package com.osint.malaysia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.osint.malaysia.ui.theme.Accent
import com.osint.malaysia.ui.theme.AppTypography
import com.osint.malaysia.ui.theme.NavyBlue
import com.osint.malaysia.viewmodel.SettingsViewModel
import com.osint.malaysia.viewmodel.ThemeMode

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel) {
    val themeMode by settingsViewModel.themeMode.collectAsState()
    val translateChinese by settingsViewModel.translateChinese.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        /* 主题设置 */
        item {
            Text("外观设置", style = AppTypography.Title, color = NavyBlue.N50)
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavyBlue.N900),
                border = BorderStroke(1.dp, NavyBlue.N700)
            ) {
                Column {
                    ThemeOption("跟随系统", "自动匹配系统深色/浅色模式", themeMode == ThemeMode.AUTO) {
                        settingsViewModel.setThemeMode(ThemeMode.AUTO)
                    }
                    HorizontalDivider(color = NavyBlue.N800)
                    ThemeOption("浅色模式", "始终使用浅色主题", themeMode == ThemeMode.LIGHT) {
                        settingsViewModel.setThemeMode(ThemeMode.LIGHT)
                    }
                    HorizontalDivider(color = NavyBlue.N800)
                    ThemeOption("深色模式", "始终使用深色主题", themeMode == ThemeMode.DARK) {
                        settingsViewModel.setThemeMode(ThemeMode.DARK)
                    }
                }
            }
        }

        /* 语言设置 */
        item {
            Spacer(Modifier.height(8.dp))
            Text("语言设置", style = AppTypography.Title, color = NavyBlue.N50)
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavyBlue.N900),
                border = BorderStroke(1.dp, NavyBlue.N700)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Translate,
                        null,
                        tint = NavyBlue.N400,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("内容翻译为中文", style = AppTypography.Body, color = NavyBlue.N50)
                        Text(
                            "将API返回的马来文/英文内容翻译为中文显示",
                            style = AppTypography.Caption,
                            color = NavyBlue.N500
                        )
                    }
                    Switch(
                        checked = translateChinese,
                        onCheckedChange = { settingsViewModel.setTranslateChinese(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NavyBlue.N50,
                            checkedTrackColor = NavyBlue.N500,
                            uncheckedThumbColor = NavyBlue.N400,
                            uncheckedTrackColor = NavyBlue.N800
                        )
                    )
                }
            }
        }

        /* 应用信息 */
        item {
            Spacer(Modifier.height(8.dp))
            Text("关于", style = AppTypography.Title, color = NavyBlue.N50)
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavyBlue.N900),
                border = BorderStroke(1.dp, NavyBlue.N700)
            ) {
                Column(Modifier.padding(16.dp)) {
                    InfoRow("应用名称", "马来西亚OSINT")
                    InfoRow("版本", "1.0.0")
                    InfoRow("作者", "钟智强")
                    InfoRow("邮箱", "ctkqiang@dingtalk.com")
                    InfoRow("仓库", "gitcode.com/ctkqiang_sr/MalaysianOSINTAPP.git")
                }
            }
        }

        item {
            Text(
                "本应用仅供安全研究与合法授权测试使用。使用者应遵守马来西亚相关法律法规，对自身行为负责。",
                style = AppTypography.Caption,
                color = NavyBlue.N600
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text("$label: ", style = AppTypography.Caption, color = NavyBlue.N400)
        Text(value, style = AppTypography.Body, color = NavyBlue.N100)
    }
}

@Composable
private fun ThemeOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = NavyBlue.N400,
                unselectedColor = NavyBlue.N600
            )
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, style = AppTypography.Body, color = NavyBlue.N50)
            Text(description, style = AppTypography.Caption, color = NavyBlue.N500)
        }
    }
}
