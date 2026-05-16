/* 马来西亚OSINT — 首页（反诈骗 + BNM警示） */

package com.osint.malaysia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.osint.malaysia.model.SemakMuleResponse
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
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "PDRM Semak Mule — 查询电话号码或银行账号是否涉及诈骗",
                style = AppTypography.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
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
        semakMuleResult?.let { result ->
            item {
                val rows = result.tableData ?: emptyList()
                val totalCount = result.count ?: 0

                /* 风险判断: 基于table_data实际内容，而非顶层count字段 */
                val hasData = rows.isNotEmpty() && rows.any { row -> row.size >= 2 && row[1].isNotBlank() }
                val riskLevel = when {
                    hasData && rows.size > 3 -> Triple("⚠ 存在记录", Accent.Orange, "该号码/账号在PDRM数据库中有多条记录，请注意核实")
                    hasData -> Triple("⚡ 需核实", Accent.Orange, "该号码/账号在PDRM数据库中有记录，请查看详情")
                    else -> Triple("✓ 未发现记录", Accent.Green, "PDRM Semak Mule 数据库中未查询到相关记录")
                }

                /* 风险状态横幅 */
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = riskLevel.second.copy(alpha = 0.12f)
                    ),
                    border = BorderStroke(1.dp, riskLevel.second.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(riskLevel.second.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                when {
                                    hasData -> Icons.Default.Info
                                    else -> Icons.Default.CheckCircle
                                },
                                contentDescription = null,
                                tint = riskLevel.second,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                riskLevel.first,
                                style = AppTypography.Title,
                                color = riskLevel.second
                            )
                            Text(
                                riskLevel.third,
                                style = AppTypography.Caption,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                /* 统计概览 */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard("匹配记录", "${rows.size}", if (hasData) Accent.Orange else Accent.Green, Modifier.weight(1f))
                    StatCard("数据字段", "$totalCount", MaterialTheme.colorScheme.onSurfaceVariant, Modifier.weight(1f))
                    StatCard("数据来源", "PDRM", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                }

                /* 详细数据 — 展示所有table_data行 */
                if (rows.isNotEmpty()) {
                    SectionHeader("PDRM 数据库记录")
                    rows.forEachIndexed { index, row ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    "记录 #${index + 1}",
                                    style = AppTypography.Caption,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(8.dp))
                                row.forEachIndexed { colIndex, cell ->
                                    if (cell.isNotBlank()) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 3.dp)
                                        ) {
                                            Text(
                                                "▸ ",
                                                style = AppTypography.Caption,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                cell,
                                                style = AppTypography.Body,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        if (index < rows.lastIndex) {
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                } else {
                    /* table_data为空 → 干净号码 */
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Accent.Green.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                null,
                                tint = Accent.Green,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "此号码/账号未在PDRM诈骗数据库中登记",
                                style = AppTypography.Body,
                                color = Accent.Green
                            )
                        }
                    }
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
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Accent.Orange.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(alert.name, style = AppTypography.Subtitle, color = MaterialTheme.colorScheme.onBackground)
                            Text(alert.website, style = AppTypography.Caption, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (alert.date.isNotEmpty()) {
                                Text(alert.date, style = AppTypography.Caption, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

/* 统计卡片 — 用于Semak Mule结果概览 */
@Composable
private fun StatCard(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                style = AppTypography.Title.copy(fontWeight = FontWeight.Bold),
                color = accentColor,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(2.dp))
            Text(
                label,
                style = AppTypography.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
