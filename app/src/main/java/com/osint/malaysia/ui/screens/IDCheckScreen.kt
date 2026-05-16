/* 马来西亚OSINT — 身份证查询页面 */

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
import androidx.compose.ui.unit.dp
import com.osint.malaysia.model.WantedPerson
import com.osint.malaysia.model.SPRMCase
import com.osint.malaysia.ui.components.*
import com.osint.malaysia.ui.theme.Accent
import com.osint.malaysia.ui.theme.AppTypography
import com.osint.malaysia.ui.theme.NavyBlue
import com.osint.malaysia.viewmodel.MainViewModel

@Composable
fun IDCheckScreen(viewModel: MainViewModel) {
    var icNumber by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val idResult by viewModel.idCheckResult.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        /* 搜索区 */
        item {
            Text("身份证综合查询", style = AppTypography.Title, color = NavyBlue.N50)
            Text(
                "SSPI移民局 + MyKad解析 + 通缉名单 + 反贪会记录",
                style = AppTypography.Caption,
                color = NavyBlue.N400
            )
        }

        item {
            SearchBar(
                query = icNumber,
                onQueryChange = { icNumber = it.filter { c -> c.isDigit() || c == '-' }.take(14) },
                onSearch = { viewModel.queryIDComprehensive(icNumber) },
                placeholder = "输入12位身份证号码 (如 990101075678)",
                enabled = !isLoading
            )
        }

        item {
            ActionButton("综合查询", onClick = {
                if (icNumber.isNotBlank()) viewModel.queryIDComprehensive(icNumber)
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

        /* MyKad解析结果 */
        idResult?.myKad?.let { myKad ->
            item {
                SectionHeader("MyKad 身份证解析")
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyBlue.N900),
                    border = BorderStroke(1.dp, NavyBlue.N700)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        InfoRow("出生日期", myKad.birthday)
                        InfoRow("州属/直辖区", "${myKad.provinceCn} (${myKad.province})")
                        InfoRow("唯一标识符", myKad.identifier, isMono = true)
                    }
                }
            }
        } ?: idResult?.let {
            item { EmptyState("无法解析身份证号码，请检查输入") }
        }

        /* SSPI移民局状态 */
        idResult?.sspi?.let { sspi ->
            item {
                SectionHeader("SSPI 移民局状态")
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyBlue.N900),
                    border = BorderStroke(1.dp, NavyBlue.N700)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            null,
                            tint = if (sspi.statusCode.contains("TIDAK", ignoreCase = true))
                                Accent.Red else Accent.Green,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(sspi.statusCode, style = AppTypography.Body, color = NavyBlue.N100)
                    }
                }
            }
        }

        /* 通缉名单 */
        if (idResult?.wanted?.isNotEmpty() == true) {
            item { SectionHeader("⚠ 通缉名单匹配 (${idResult!!.wanted.size})") }
            items(idResult!!.wanted) { person -> WantedPersonCard(person) }
        }

        /* SPRM反贪会记录 */
        if (idResult?.sprmCases?.isNotEmpty() == true) {
            item { SectionHeader("反贪会腐败记录 (${idResult!!.sprmCases.size})") }
            items(idResult!!.sprmCases) { case -> SPRMCaseCard(case) }
        }
    }
}

@Composable
private fun SPRMCaseCard(case: SPRMCase) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavyBlue.N900),
        border = BorderStroke(1.dp, Accent.Orange.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(case.name, style = AppTypography.Subtitle, color = NavyBlue.N50)
            Spacer(Modifier.height(4.dp))
            InfoRow("身份证", case.ic, isMono = true)
            InfoRow("案件编号", case.caseNo, isMono = true)
            if (case.charge.isNotEmpty()) InfoRow("罪名", case.charge)
            if (case.sentence.isNotEmpty()) InfoRow("判决", case.sentence)
            if (case.law.isNotEmpty()) InfoRow("适用法律", case.law)
        }
    }
}
