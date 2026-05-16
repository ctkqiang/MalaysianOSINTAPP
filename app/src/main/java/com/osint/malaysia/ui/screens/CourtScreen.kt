/* 马来西亚OSINT — 电子法庭查询页面 */

package com.osint.malaysia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.osint.malaysia.ui.components.*
import com.osint.malaysia.ui.theme.AppTypography
import com.osint.malaysia.ui.theme.NavyBlue
import com.osint.malaysia.viewmodel.MainViewModel

@Composable
fun CourtScreen(viewModel: MainViewModel) {
    var nameQuery by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val ecourtResult by viewModel.ecourtResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("电子法庭查询", style = AppTypography.Title, color = NavyBlue.N50)
            Text(
                "搜索马来西亚联邦法院电子判决系统 — e-Court e-Judgment",
                style = AppTypography.Caption,
                color = NavyBlue.N400
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

        errorMessage?.let { msg ->
            item { ErrorBanner(msg) { viewModel.clearError() } }
        }

        if (isLoading) {
            item { LoadingOverlay(true) }
        }

        /* 法庭结果 */
        if (ecourtResult.isNotEmpty()) {
            item { SectionHeader("判决结果") }

            try {
                val formattedJson = GsonBuilder().setPrettyPrinting().create()
                    .toJson(JsonParser.parseString(ecourtResult))

                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyBlue.N900),
                        border = BorderStroke(1.dp, NavyBlue.N700)
                    ) {
                        Text(
                            text = formattedJson.take(5000),
                            style = AppTypography.Mono,
                            color = NavyBlue.N100,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            } catch (_: Exception) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyBlue.N900),
                        border = BorderStroke(1.dp, NavyBlue.N700)
                    ) {
                        Text(
                            text = ecourtResult.take(5000),
                            style = AppTypography.Mono,
                            color = NavyBlue.N100,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}
