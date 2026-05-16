/* 马来西亚OSINT — 企业查询页面 */

package com.osint.malaysia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.osint.malaysia.ui.components.*
import com.osint.malaysia.ui.theme.Accent
import com.osint.malaysia.ui.theme.AppTypography
import com.osint.malaysia.viewmodel.MainViewModel
import com.osint.malaysia.util.SSMParser

@Composable
fun CompanyScreen(viewModel: MainViewModel) {
    var ssmInput by remember { mutableStateOf("") }
    var companyKeyword by remember { mutableStateOf("") }
    var activeTab by remember { mutableIntStateOf(0) }
    val isLoading by viewModel.isLoading.collectAsState()
    val ssmResult by viewModel.ssmResult.collectAsState()
    val companyResult by viewModel.companyResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        /* 标签切换 */
        item {
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Text("SSM注册号", Modifier.padding(12.dp), style = AppTypography.Body)
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Text("企业黄页", Modifier.padding(12.dp), style = AppTypography.Body)
                }
            }
        }

        /* SSM注册号解析 */
        if (activeTab == 0) {
            item {
                Text("SSM 企业注册号解析", style = AppTypography.Title, color = MaterialTheme.colorScheme.onBackground)
                Text(
                    "解析马来西亚公司委员会(SSM)注册号，识别实体类型",
                    style = AppTypography.Caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                SearchBar(
                    query = ssmInput,
                    onQueryChange = { ssmInput = it.filter { c -> c.isDigit() || c == '-' }.take(15) },
                    onSearch = { viewModel.parseSSM(ssmInput) },
                    placeholder = "输入SSM注册号 (如 123456-01-7890)",
                    enabled = !isLoading
                )
            }

            item {
                ActionButton("解析", onClick = { viewModel.parseSSM(ssmInput) })
            }

            ssmResult?.let { result ->
                item {
                    SectionHeader("解析结果")
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            InfoRow("注册号", result.registrationNumber, isMono = true)
                            InfoRow("实体类型代码", result.entityCode, isMono = true)
                            InfoRow("实体类型", result.entityTypeCn)
                        }
                    }
                }
            }
        }

        /* 企业黄页搜索 */
        if (activeTab == 1) {
            item {
                Text("企业黄页搜索", style = AppTypography.Title, color = MaterialTheme.colorScheme.onBackground)
                Text(
                    "搜索马来西亚黄页(MalaysiaYP)企业信息，同时联动反诈骗数据库",
                    style = AppTypography.Caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                SearchBar(
                    query = companyKeyword,
                    onQueryChange = { companyKeyword = it },
                    onSearch = { viewModel.queryCompany(companyKeyword) },
                    placeholder = "输入企业名称关键词",
                    enabled = !isLoading
                )
            }

            item {
                ActionButton("企业查询", onClick = {
                    if (companyKeyword.isNotBlank()) viewModel.queryCompany(companyKeyword)
                }, isLoading = isLoading)
            }

            if (companyResult != null) {
                val company = companyResult!!
                item {
                    SectionHeader("企业信息")
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            InfoRow("企业名称", company.name)
                            if (company.category.isNotEmpty()) InfoRow("行业类别", company.category)
                            if (company.address.isNotEmpty()) InfoRow("地址", company.address)
                            if (company.website.isNotEmpty()) InfoRow("网站", company.website)
                            InfoRow("数据来源", company.source)
                        }
                    }
                }
            } else if (!isLoading && companyKeyword.isNotBlank()) {
                item { EmptyState("未找到匹配企业") }
            }
        }

        /* 错误提示 */
        errorMessage?.let { msg ->
            item { ErrorBanner(msg) { viewModel.clearError() } }
        }

        if (isLoading) {
            item { LoadingOverlay(true) }
        }
    }
}
