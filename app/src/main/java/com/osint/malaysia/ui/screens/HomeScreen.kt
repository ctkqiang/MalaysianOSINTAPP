/* 马来西亚OSINT — 首页（反诈骗 + BNM警示） */

package com.osint.malaysia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.osint.malaysia.ui.components.*
import com.osint.malaysia.ui.theme.Accent
import com.osint.malaysia.ui.theme.AppTypography
import com.osint.malaysia.ui.theme.NavyBlue
import com.osint.malaysia.viewmodel.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val semakMuleResult by viewModel.semakMuleResult.collectAsState()
    val bnmResult by viewModel.bnmResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    /* 首次加载时获取BNM警示 */
    LaunchedEffect(Unit) {
        if (bnmResult == null) {
            viewModel.loadBNMAlert()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        /* 搜索区 */
        item {
            Text(
                "反诈骗查询",
                style = AppTypography.Title,
                color = NavyBlue.N50
            )
            Text(
                "PDRM Semak Mule — 查询电话号码或银行账号是否涉及诈骗",
                style = AppTypography.Caption,
                color = NavyBlue.N400
            )
        }

        item {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { viewModel.querySemakMule(searchQuery) },
                placeholder = "输入电话号码或银行账号",
                enabled = !isLoading
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton("查询", onClick = {
                    if (searchQuery.isNotBlank()) viewModel.querySemakMule(searchQuery)
                }, isLoading = isLoading, modifier = Modifier.weight(1f))

                OutlinedButton(
                    onClick = { viewModel.loadBNMAlert() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyBlue.N300),
                    border = BorderStroke(1.dp, NavyBlue.N600)
                ) {
                    Icon(Icons.Default.Refresh, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("刷新警示", style = AppTypography.Caption)
                }
            }
        }

        /* 错误提示 */
        errorMessage?.let { msg ->
            item { ErrorBanner(msg) { viewModel.clearError() } }
        }

        /* 加载状态 */
        if (isLoading) {
            item { LoadingOverlay(true) }
        }

        /* Semak Mule结果 */
        if (semakMuleResult.isNotEmpty()) {
            item {
                SectionHeader("查询结果")
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyBlue.N900),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NavyBlue.N700)
                ) {
                    Text(
                        text = semakMuleResult,
                        style = AppTypography.Mono,
                        color = NavyBlue.N100,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        /* BNM消费者警示 */
        bnmResult?.let { result ->
            item {
                SectionHeader("国行消费者警示名单 (${result.count}条)")
            }

            if (result.entries.isEmpty()) {
                item { EmptyState("暂无警示记录") }
            } else {
                items(result.entries.take(50)) { alert ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyBlue.N900),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Accent.Orange.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(alert.name, style = AppTypography.Subtitle, color = NavyBlue.N50)
                            Text(alert.website, style = AppTypography.Caption, color = NavyBlue.N400)
                            if (alert.date.isNotEmpty()) {
                                Text(alert.date, style = AppTypography.Caption, color = NavyBlue.N500)
                            }
                        }
                    }
                }
            }
        }
    }
}
