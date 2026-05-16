package xin.ctkqiang.malaysianosint.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Spacer(Modifier.height(12.dp))
                Text("马来西亚 OSINT", fontSize = 28.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary)
                Text("开源情报收集工具", fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(28.dp))
            }

            /** 开发者卡片 */
            item {
                SectionCard("开发者信息") {
                    InfoLine("作者", "钟智强")
                    InfoLine("代号", "哪吒网络安全 / ctkqiang")
                    InfoLine("邮箱", "ctkqiang@dingtalk.com")
                }
                Spacer(Modifier.height(14.dp))
            }

            /** 项目卡片 */
            item {
                SectionCard("项目信息") {
                    InfoLine("仓库", "gitcode.com/ctkqiang_sr/MalaysianOSINTAPP")
                    InfoLine("C 语言原版", "github.com/ctkqiang/MalaysianOSINT")
                    InfoLine("最低平台", "Android 6.0 (API 23)")
                    InfoLine("目标平台", "Android 14 (API 34)")
                }
                Spacer(Modifier.height(14.dp))
            }

            /** 技术栈卡片 */
            item {
                SectionCard("技术栈") {
                    InfoLine("语言", "Kotlin 2.0.21")
                    InfoLine("UI 框架", "Jetpack Compose + Material 3")
                    InfoLine("架构模式", "MVVM + StateFlow + Coroutines")
                    InfoLine("网络层", "OkHttp 4.12 + Jsoup 1.18")
                    InfoLine("持久化", "DataStore Preferences")
                    InfoLine("JSON 解析", "Gson 2.11")
                }
                Spacer(Modifier.height(14.dp))
            }

            /** OSINT 模块列表 */
            item {
                SectionCard("内置情报模块 (9 项)") {
                    ModuleLine("PDRM 反洗钱", "核查银行账户/手机号涉诈")
                    ModuleLine("SSPI 身份核查", "移民局出入境限制 + MyKad")
                    ModuleLine("E-Court 法院", "马来西亚法院案件记录")
                    ModuleLine("PDRM 通缉名单", "警方通缉人员核查")
                    ModuleLine("SPRM 反贪会", "反贪污委员会记录")
                    ModuleLine("SSM 公司注册", "企业注册号核实")
                    ModuleLine("公司详情搜索", "按名称检索企业信息")
                    ModuleLine("社交媒体搜索", "跨平台用户名查询")
                    ModuleLine("BNM 银行警示", "国家银行消费者警示")
                }
                Spacer(Modifier.height(14.dp))
            }

            /** 法律声明 */
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
                    ),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("法律声明", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "本工具仅供开源情报研究与教育用途。\n"
                                    + "所查询数据均为马来西亚政府公开信息。\n"
                                    + "开发者不对任何滥用行为承担责任。",
                            fontSize = 13.sp, lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                Text("PNM · APMM · 马来情报", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text("$label：", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun ModuleLine(name: String, desc: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text("$name  ", fontSize = 13.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary)
        Text(desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
