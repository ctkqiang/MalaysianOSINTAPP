package xin.ctkqiang.malaysianosint.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen() {
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = { Text("关于", fontWeight = FontWeight.Bold) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Text("马来西亚 OSINT", fontSize = 26.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                Text("开源情报收集工具", fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                Spacer(Modifier.height(24.dp))
            }

            item { InfoSection("开发者信息") }
            item { InfoRow("作者", "钟智强") }
            item { InfoRow("代号", "哪吒网络安全 / ctkqiang") }
            item { InfoRow("邮箱", "ctkqiang@dingtalk.com") }
            item { Spacer(Modifier.height(12.dp)) }

            item { InfoSection("项目信息") }
            item { InfoRow("仓库", "gitcode.com/ctkqiang_sr/MalaysianOSINTAPP") }
            item { InfoRow("原始项目", "github.com/ctkqiang/MalaysianOSINT (C 语言)") }
            item { InfoRow("平台", "Android 6.0+ (API 23+)") }
            item { Spacer(Modifier.height(12.dp)) }

            item { InfoSection("技术栈") }
            item { InfoRow("语言", "Kotlin 2.0") }
            item { InfoRow("UI", "Jetpack Compose + Material 3") }
            item { InfoRow("架构", "MVVM + StateFlow + Coroutines") }
            item { InfoRow("网络", "OkHttp 4 + Jsoup") }
            item { InfoRow("存储", "DataStore Preferences") }
            item { Spacer(Modifier.height(24.dp)) }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                ) {
                    Text(
                        "本工具仅供开源情报研究与教育用途。\n查询均为马来西亚政府公开数据。\n开发者不对任何滥用行为承担责任。",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text("PNM · APMM · 马来情报", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun InfoSection(title: String) {
    Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text("$label:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(8.dp))
        Text(value, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}
