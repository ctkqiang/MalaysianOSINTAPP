package xin.ctkqiang.malaysianosint.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

/** OSINT 模块定义 */
private data class ModuleInfo(val id: String, val label: String, val desc: String, val icon: String)

private val modules = listOf(
    ModuleInfo("pdrm", "PDRM 反洗钱", "核查银行账户/手机号是否涉及诈骗", "D"),
    ModuleInfo("sspi", "SSPI 身份核查", "移民局出入境限制 + MyKad 身份信息", "S"),
    ModuleInfo("ecourt", "E-Court 法院", "查询马来西亚法院案件记录", "C"),
    ModuleInfo("wanted", "PDRM 通缉名单", "核查是否被警方通缉", "W"),
    ModuleInfo("sprm", "SPRM 反贪会", "查询反贪污委员会调查记录", "A"),
    ModuleInfo("ssm", "SSM 公司注册", "核查公司注册号与实体类型", "R"),
    ModuleInfo("company", "公司详情搜索", "按名称搜索企业注册信息", "E"),
    ModuleInfo("social", "社交媒体搜索", "跨平台用户名搜索", "X"),
    ModuleInfo("bnm", "BNM 银行警示", "国家银行消费者警示名单", "B"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedModule by viewModel.selectedModule.collectAsState()
    val status by viewModel.queryStatus.collectAsState()
    val rawResult by viewModel.rawResult.collectAsState()
    val resultCount by viewModel.resultCount.collectAsState()
    val log by viewModel.log.collectAsState()
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        /** 顶部搜索栏 */
        TopAppBar(
            title = {
                Text("马来西亚 OSINT", fontWeight = FontWeight.Bold)
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
        )

        /** 搜索输入区 */
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.weight(1f),
                placeholder = { Text("输入查询关键词…") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    if (selectedModule == "social") viewModel.searchSocial()
                    else viewModel.executeQuery()
                }),
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (selectedModule == "social") viewModel.searchSocial()
                    else viewModel.executeQuery()
                },
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)
                ),
            ) {
                Icon(Icons.Filled.Search, "搜索", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }

        /** 模块选择横向滚动 */
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    modules.take(5).forEach { mod -> ModuleChip(mod, selectedModule) { viewModel.selectModule(it) } }
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    modules.drop(5).forEach { mod -> ModuleChip(mod, selectedModule) { viewModel.selectModule(it) } }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        /** 状态指示 */
        when (status) {
            QueryStatus.LOADING -> LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            QueryStatus.SUCCESS -> Text(
                "查询完成，返回 $resultCount 条结果",
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 12.sp, color = MaterialTheme.colorScheme.primary,
            )
            QueryStatus.ERROR -> Text(
                "查询失败，请检查网络连接与 API 地址",
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 12.sp, color = MaterialTheme.colorScheme.error,
            )
            QueryStatus.IDLE -> {}
        }

        Spacer(Modifier.height(4.dp))

        /** 结果展示 / 原始 JSON */
        if (rawResult.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).weight(0.3f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(4.dp),
            ) {
                Text(
                    text = rawResult.take(2000),
                    modifier = Modifier.padding(8.dp),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 20,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        /** 操作日志 */
        Text(
            "操作日志 (${log.size})",
            modifier = Modifier.padding(horizontal = 12.dp),
            fontSize = 13.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxWidth().weight(0.5f).padding(horizontal = 12.dp, vertical = 4.dp)
                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp),
        ) {
            if (log.isEmpty()) {
                item { Text("暂无日志", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(8.dp)) }
            }
            items(log.takeLast(200), key = { it.timestamp }) { entry ->
                LogRow(entry, timeFormat)
            }
        }
    }
}

@Composable
private fun ModuleChip(mod: ModuleInfo, selected: String, onSelect: (String) -> Unit) {
    val isSel = selected == mod.id
    FilterChip(
        selected = isSel,
        onClick = { onSelect(mod.id) },
        label = {
            Column {
                Text(mod.label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(mod.desc, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        ),
    )
}

@Composable
private fun LogRow(entry: LogEntry, tf: SimpleDateFormat) {
    val color = when (entry.level) {
        xin.ctkqiang.malaysianosint.data.model.LogLevel.INFO -> MaterialTheme.colorScheme.primary
        xin.ctkqiang.malaysianosint.data.model.LogLevel.WARN -> MaterialTheme.colorScheme.error
        xin.ctkqiang.malaysianosint.data.model.LogLevel.ERROR -> MaterialTheme.colorScheme.error
    }
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp)) {
        Text(tf.format(Date(entry.timestamp)), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(" [${entry.module}] ", fontSize = 10.sp, color = color)
        Text(entry.message, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}
