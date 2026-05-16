package xin.ctkqiang.malaysianosint.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import xin.ctkqiang.malaysianosint.data.model.*
import xin.ctkqiang.malaysianosint.data.remote.OsintApiService
import java.text.SimpleDateFormat
import java.util.*

/**
 * OSINT 数据仓库 — 统一管理所有情报模块的查询与日志记录
 *
 * 职责：
 * - 调度 OsintApiService 发起网络请求
 * - 解析响应数据并映射为领域模型
 * - 维护中文操作日志供 UI 展示
 * - 管理查询状态 (IDLE → LOADING → SUCCESS/ERROR)
 */
class OsintRepository {

    private val api = OsintApiService()
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    private val _log = MutableStateFlow<List<LogEntry>>(emptyList())
    val log: StateFlow<List<LogEntry>> = _log.asStateFlow()

    private val _status = MutableStateFlow(QueryStatus.IDLE)
    val status: StateFlow<QueryStatus> = _status.asStateFlow()

    /** 最后一次查询的原始结果 (JSON) */
    private val _rawResult = MutableStateFlow("")
    val rawResult: StateFlow<String> = _rawResult.asStateFlow()

    /** 最后查询的模块名称 */
    private val _activeModule = MutableStateFlow("")
    val activeModule: StateFlow<String> = _activeModule.asStateFlow()

    private val _resultCount = MutableStateFlow(0)
    val resultCount: StateFlow<Int> = _resultCount.asStateFlow()

    // ──────────────────────────────────────────────────────────────────────────
    // 通用查询入口
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * 执行 OSINT 查询
     *
     * @param module 模块标识 (pdrm / sspi / ecourt / wanted / sprm / ssm / company / social / bnm)
     * @param queryValue 用户输入的查询值
     * @param baseUrl API 基础地址 (由 Settings 管理)
     */
    suspend fun executeQuery(module: String, queryValue: String, baseUrl: String) {
        _status.value = QueryStatus.LOADING
        _activeModule.value = module
        _rawResult.value = ""

        addLog(LogLevel.INFO, module, "开始查询: $queryValue")

        try {
            val (param, json) = when (module) {
                "pdrm" -> "q" to api.query(baseUrl, "q", queryValue)
                "sspi" -> "id" to api.query(baseUrl, "id", queryValue)
                "ecourt" -> "name" to api.query(baseUrl, "name", queryValue)
                "wanted" -> "wanted" to api.query(baseUrl, "wanted", queryValue)
                "sprm" -> "sprm" to api.query(baseUrl, "sprm", queryValue)
                "ssm" -> "ssm" to api.query(baseUrl, "ssm", queryValue)
                "company" -> "comp" to api.query(baseUrl, "comp", queryValue)
                "social" -> "social" to api.query(baseUrl, "social", queryValue)
                "bnm" -> "bnm" to api.query(baseUrl, "bnm", queryValue)
                else -> return
            }

            if (json.isBlank()) {
                _status.value = QueryStatus.ERROR
                addLog(LogLevel.WARN, module, "API 返回空响应, 检查网络连接")
                return
            }

            _rawResult.value = json

            val count = when (module) {
                "pdrm" -> api.parsePdrmResponse(json).cases.size
                "sspi" -> { api.parseSspiResponse(json); 1 }
                "ecourt" -> api.parseEcourtResponse(json).totalResults
                "wanted" -> api.parsePdrmResponse(json).cases.size
                "sprm" -> { api.parseSspiResponse(json); 1 }
                "ssm" -> { api.parseSsmResponse(json); 1 }
                "company" -> api.parseCompanyResponse(json).companies.size
                "social" -> api.parseSocialResponse(json).profiles.size
                "bnm" -> api.parseBnmResponse(json).alerts.size
                else -> 0
            }

            _resultCount.value = count
            _status.value = QueryStatus.SUCCESS
            addLog(LogLevel.INFO, module, "查询完成, 返回 $count 条结果")
        } catch (e: Exception) {
            _status.value = QueryStatus.ERROR
            addLog(LogLevel.ERROR, module, "查询失败: ${e.message}")
        }
    }

    /**
     * 社交媒体跨平台搜索 (纯本地逻辑，无需 API)
     */
    fun searchSocialLocally(username: String): SocialResponse {
        _activeModule.value = "social"
        _status.value = QueryStatus.LOADING
        addLog(LogLevel.INFO, "social", "跨平台搜索: $username")

        val result = api.searchSocialPlatforms(username)
        _resultCount.value = result.profiles.size
        _status.value = QueryStatus.SUCCESS
        _rawResult.value = result.profiles.joinToString("\n") { "${it.platform}: ${it.url}" }
        addLog(LogLevel.INFO, "social", "找到 ${result.profiles.size} 个平台链接")
        return result
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 日志
    // ──────────────────────────────────────────────────────────────────────────

    private fun addLog(level: LogLevel, module: String, message: String) {
        _log.value = (_log.value + LogEntry(
            timestamp = System.currentTimeMillis(),
            level = level,
            module = module,
            message = message,
        )).takeLast(500)
    }

    fun clearLog() { _log.value = emptyList() }
    fun resetStatus() { _status.value = QueryStatus.IDLE }
}
