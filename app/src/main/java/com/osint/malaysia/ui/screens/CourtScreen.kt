/* 马来西亚OSINT — 电子法庭查询页面 */

package com.osint.malaysia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.osint.malaysia.model.ECourtItem
import com.osint.malaysia.ui.components.*
import com.osint.malaysia.ui.theme.Accent
import com.osint.malaysia.ui.theme.AppTypography
import com.osint.malaysia.viewmodel.MainViewModel

@Composable
fun CourtScreen(viewModel: MainViewModel) {
    var nameQuery by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val ecourtResult by viewModel.ecourtResult.collectAsState()
    val ecourtRawJson by viewModel.ecourtRawJson.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        /* 搜索头部 */
        item {
            Text(
                "电子法庭查询",
                style = AppTypography.Title,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "搜索马来西亚联邦法院电子判决 — e-Judgment Portal",
                style = AppTypography.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            SearchBar(
                query = nameQuery,
                onQueryChange = { nameQuery = it },
                onSearch = { viewModel.searchECourt(nameQuery) },
                placeholder = "输入姓名或案件关键词",
                enabled = !isLoading
            )
        }

        item {
            ActionButton("法庭搜索", onClick = {
                if (nameQuery.isNotBlank()) viewModel.searchECourt(nameQuery)
            }, isLoading = isLoading)
        }

        /* 错误提示 */
        errorMessage?.let { msg ->
            item { ErrorBanner(msg) { viewModel.clearError() } }
        }

        /* 加载状态 */
        if (isLoading) {
            item { LoadingOverlay(true) }
        }

        /* 搜索结果 */
        if (ecourtResult != null) {
            val result = ecourtResult!!
            val cases = result.searchList ?: emptyList()

            /* 结果概览 */
            item {
                SectionHeader("搜索结果")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard("匹配案件", "${cases.size}", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                    StatCard("总记录数", "${result.totalRecords}", MaterialTheme.colorScheme.onSurfaceVariant, Modifier.weight(1f))
                    StatCard("当前页", "${result.currPage}/${result.totalPage}", MaterialTheme.colorScheme.onSurfaceVariant, Modifier.weight(1f))
                }
            }

            if (cases.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.SearchOff,
                                null,
                                tint = Accent.Green,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "未找到相关法庭记录",
                                style = AppTypography.Body,
                                color = Accent.Green
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(cases) { index, item ->
                    ECourtCaseCard(index = index + 1, item = item)
                }
            }
        } else if (ecourtRawJson.isNotEmpty()) {
            /* 解析失败,回退显示原始JSON */
            item {
                SectionHeader("API原始响应（解析失败）")
            }
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = try {
                            com.google.gson.GsonBuilder().setPrettyPrinting()
                                .create().toJson(com.google.gson.JsonParser.parseString(ecourtRawJson))
                        } catch (_: Exception) { ecourtRawJson }
                            .take(8000),
                        style = AppTypography.Mono,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        }
    }
}

/* 法庭案件卡片 */
@Composable
private fun ECourtCaseCard(index: Int, item: ECourtItem) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            /* 案件标题行 */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Gavel,
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "#$index",
                    style = AppTypography.Caption.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    item.keyWord.ifBlank { item.caseNo.ifBlank { "案件 #$index" } },
                    style = AppTypography.Subtitle.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(Modifier.height(12.dp))
            /* 案件信息 */
            if (item.caseNo.isNotBlank()) {
                ECourtInfoRow("案件编号", item.caseNo)
            }
            if (item.parties.isNotBlank()) {
                ECourtInfoRow("当事人", item.parties)
            }
            if (item.judge.isNotBlank()) {
                ECourtInfoRow("法官", item.judge)
            }
            if (item.corumJudge.isNotBlank() && item.corumJudge != item.judge) {
                ECourtInfoRow("合议庭", item.corumJudge)
            }
            if (item.dateOfAP.isNotBlank()) {
                ECourtInfoRow("上诉日期", item.dateOfAP)
            }
            if (item.dateOfResult.isNotBlank()) {
                ECourtInfoRow("判决日期", item.dateOfResult)
            }
            if (item.eJudgUniqueID.isNotBlank()) {
                ECourtInfoRow("案件ID", item.eJudgUniqueID)
            }
            /* 关联文档 */
            item.listOfAPDoc?.forEach { doc ->
                if (doc.fileName.isNotBlank()) {
                    ECourtInfoRow("文件", "${doc.fileName} (${doc.documentType})")
                }
            }
        }
    }
}

@Composable
private fun ECourtInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            "$label  ",
            style = AppTypography.Caption.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(72.dp)
        )
        Text(
            value,
            style = AppTypography.Body,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/* 统计卡片 */
@Composable
private fun StatCard(
    label: String,
    value: String,
    accentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                style = AppTypography.Title.copy(fontWeight = FontWeight.Bold),
                color = accentColor,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                label,
                style = AppTypography.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
