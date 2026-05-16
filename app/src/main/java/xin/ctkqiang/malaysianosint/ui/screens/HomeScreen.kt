package xin.ctkqiang.malaysianosint.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xin.ctkqiang.malaysianosint.data.model.LogEntry
import xin.ctkqiang.malaysianosint.data.model.QueryStatus
import xin.ctkqiang.malaysianosint.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

private data class ModuleDef(val id: String, val label: String, val desc: String)

private val modules = listOf(
    ModuleDef("pdrm", "PDRM 反洗钱", "核查银行/手机号涉诈"),
    ModuleDef("sspi", "SSPI 身份核查", "移民局 + MyKad"),
    ModuleDef("ecourt", "E-Court 法院", "法院案件记录"),
    ModuleDef("wanted", "通缉名单", "PDRM 通缉核查"),
    ModuleDef("sprm", "SPRM 反贪会", "反贪记录查询"),
    ModuleDef("ssm", "SSM 公司注册", "注册号核实"),
    ModuleDef("company", "公司搜索", "企业详情检索"),
    ModuleDef("social", "社交媒体", "跨平台用户名"),
    ModuleDef("bnm", "BNM 银行警示", "消费者警示名单"),
)

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedModule by viewModel.selectedModule.collectAsState()
    val status by viewModel.queryStatus.collectAsState()
    val rawResult by viewModel.rawResult.collectAsState()
    val resultCount by viewModel.resultCount.collectAsState()
    val log by viewModel.log.collectAsState()
    val activeModule by viewModel.activeModule.collectAsState()
    val listState = rememberLazyListState()
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    LaunchedEffect(log.size) {
        if (log.isNotEmpty()) listState.animateScrollToItem(log.size - 1)
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        /** 模块选择 — 水平滚动 */
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(modules) { mod ->
                val sel = selectedModule == mod.id
                FilterChip(
                    selected = sel,
                    onClick = { viewModel.selectModule(mod.id) },
                    label = {
                        Column {
                            Text(mod.label, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(mod.desc, fontSize = 10.sp,
                                color = if (sel) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        /** 搜索栏 */
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.weight(1f),
                placeholder = { Text("输入查询内容…", fontSize = 14.sp) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (selectedModule == "social") viewModel.searchSocial()
                        else viewModel.executeQuery()
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    if (selectedModule == "social") viewModel.searchSocial()
                    else viewModel.executeQuery()
                },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(4.dp))
                Text("查询")
            }
        }

        /** 状态条 */
        when (status) {
            QueryStatus.LOADING -> LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
            )
            QueryStatus.SUCCESS -> Row(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("[${activeModule}] 查询完成", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                Text("$resultCount 条结果", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            QueryStatus.ERROR -> Row(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text("查询失败，检查网络与 API 地址", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
            }
            QueryStatus.IDLE -> {}
        }

        /** 结果区域 */
        if (rawResult.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth().weight(0.35f).padding(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("查询结果", fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = rawResult.ifBlank { "暂无数据" }.take(3000),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 25,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp,
                    )
                }
            }
        }

        /** 操作日志 */
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("操作日志", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary)
            Row {
                Text("${log.size} 条", fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(12.dp))
                TextButton(onClick = { viewModel.clearLog() },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("清空", fontSize = 12.sp)
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth().weight(0.4f)
                .padding(horizontal = 12.dp)
                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(6.dp))
                .padding(6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            if (log.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center) {
                        Text("暂无日志 — 执行查询后将在此处显示操作记录",
                            fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            items(log.takeLast(300), key = { it.timestamp }) { entry ->
                LogRow(entry, timeFormat)
            }
        }
    }
}

@Composable
private fun LogRow(entry: LogEntry, tf: SimpleDateFormat) {
    val color = when (entry.level) {
        xin.ctkqiang.malaysianosint.data.model.LogLevel.INFO -> MaterialTheme.colorScheme.primary
        xin.ctkqiang.malaysianosint.data.model.LogLevel.WARN -> MaterialTheme.colorScheme.error
        xin.ctkqiang.malaysianosint.data.model.LogLevel.ERROR -> MaterialTheme.colorScheme.error
    }
    val levelTag = when (entry.level) {
        xin.ctkqiang.malaysianosint.data.model.LogLevel.INFO -> "I"
        xin.ctkqiang.malaysianosint.data.model.LogLevel.WARN -> "W"
        xin.ctkqiang.malaysianosint.data.model.LogLevel.ERROR -> "E"
    }
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 1.5.dp)) {
        Text(tf.format(Date(entry.timestamp)), fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(" $levelTag ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
        Text("[${entry.module}]", fontSize = 10.sp, color = color)
        Text(" ${entry.message}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
