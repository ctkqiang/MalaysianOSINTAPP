/* 马来西亚OSINT — 社交媒体搜索页面 */

package com.osint.malaysia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.osint.malaysia.ui.components.*
import com.osint.malaysia.ui.theme.Accent
import com.osint.malaysia.ui.theme.AppTypography
import com.osint.malaysia.viewmodel.MainViewModel

@Composable
fun SocialScreen(viewModel: MainViewModel) {
    var username by remember { mutableStateOf("") }
    val isSearching by viewModel.isSocialSearching.collectAsState()
    val socialResults by viewModel.socialResults.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("社交媒体足迹搜索", style = AppTypography.Title, color = MaterialTheme.colorScheme.onBackground)
            Text(
                "跨平台用户名枚举 — 遍历60+主流社交平台进行匹配",
                style = AppTypography.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            SearchBar(
                query = username,
                onQueryChange = { username = it.trim().take(50) },
                onSearch = { if (username.isNotBlank()) viewModel.searchSocial(username) },
                placeholder = "输入要搜索的用户名",
                enabled = !isSearching
            )
        }

        item {
            ActionButton(
                "开始搜索",
                onClick = { if (username.isNotBlank()) viewModel.searchSocial(username) },
                isLoading = isSearching
            )
        }

        /* 搜索进度 */
        if (isSearching) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "正在搜索… 已找到 ${socialResults.size} 个平台",
                        style = AppTypography.Caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        /* 搜索结果 */
        if (!isSearching && username.isNotBlank()) {
            item {
                SectionHeader(
                    if (socialResults.isEmpty()) "未发现匹配平台"
                    else "发现匹配 — ${socialResults.size} 个平台"
                )
            }
        }

        if (socialResults.isNotEmpty()) {
            items(socialResults) { result ->
                SocialResultRow(result)
            }
        }

        if (!isSearching && username.isNotBlank() && socialResults.isEmpty()) {
            item { EmptyState("该用户名未在任何平台发现") }
        }

        /* 使用说明 */
        if (!isSearching && username.isBlank()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("使用说明", style = AppTypography.Subtitle, color = MaterialTheme.colorScheme.onBackground)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("• 输入目标用户名，系统将自动遍历各大社交平台进行匹配", style = AppTypography.Caption, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("• 搜索结果实时显示，无需等待全部完成", style = AppTypography.Caption, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("• 支持全球主流社交平台、开发者社区、论坛等", style = AppTypography.Caption, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
