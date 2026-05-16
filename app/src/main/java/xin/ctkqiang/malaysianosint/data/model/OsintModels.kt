package xin.ctkqiang.malaysianosint.data.model

import com.google.gson.annotations.SerializedName

/** 查询状态 */
enum class QueryStatus { IDLE, LOADING, SUCCESS, ERROR }

/** 日志级别 */
enum class LogLevel { INFO, WARN, ERROR }

/** 单条日志 */
data class LogEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val level: LogLevel,
    val module: String,
    val message: String,
)

/** 通用 API 响应包装 */
data class ApiResponse<T>(
    val status: String,
    val message: String,
    val data: T?,
)

// ──────────────────────────────────────────────────────────────────────────────
// 模块1: PDRM 反洗钱核查 (Semak Mule)
// ──────────────────────────────────────────────────────────────────────────────

data class PdrmResponse(
    val cases: List<PdrmCase> = emptyList(),
    val rawJson: String = "",
)

data class PdrmCase(
    @SerializedName("bank") val bank: String = "",
    @SerializedName("account") val account: String = "",
    @SerializedName("reports") val reports: Int = 0,
)

// ──────────────────────────────────────────────────────────────────────────────
// 模块2: 移民局 SSPI 身份核查 + MyKad
// ──────────────────────────────────────────────────────────────────────────────

data class SspiResponse(
    @SerializedName("sspi_status") val sspiStatus: String = "",  /* Tiada Halangan / 有出境限制 */
    @SerializedName("name") val name: String = "",
    @SerializedName("ic") val ic: String = "",
    @SerializedName("address") val address: String = "",
    /* 关联字段 — 来自 PDRM 通缉库 */
    @SerializedName("wanted") val wanted: WantedInfo? = null,
    /* 关联字段 — 来自 SPRM 反贪会 */
    @SerializedName("sprm") val sprm: SprmInfo? = null,
    val rawJson: String = "",
)

// ──────────────────────────────────────────────────────────────────────────────
// 模块3: E-Court 法院记录查询
// ──────────────────────────────────────────────────────────────────────────────

data class EcourtResponse(
    val totalResults: Int = 0,
    val cases: List<EcourtCase> = emptyList(),
    val rawJson: String = "",
)

data class EcourtCase(
    @SerializedName("case_number") val caseNumber: String = "",
    @SerializedName("court") val court: String = "",
    @SerializedName("judge") val judge: String = "",
    @SerializedName("date_of_ap") val dateOfAppeal: String = "",
)

// ──────────────────────────────────────────────────────────────────────────────
// 模块4: PDRM 通缉名单查询
// ──────────────────────────────────────────────────────────────────────────────

data class WantedResponse(
    val wanted: List<WantedInfo> = emptyList(),
    val rawJson: String = "",
)

data class WantedInfo(
    @SerializedName("name") val name: String = "",
    @SerializedName("age") val age: String = "",
    @SerializedName("photo_url") val photoUrl: String = "",
    @SerializedName("ic") val ic: String = "",
)

// ──────────────────────────────────────────────────────────────────────────────
// 模块5: SPRM 反贪委员会记录
// ──────────────────────────────────────────────────────────────────────────────

data class SprmResponse(
    val records: List<SprmInfo> = emptyList(),
    val rawJson: String = "",
)

data class SprmInfo(
    @SerializedName("name") val name: String = "",
    @SerializedName("ic") val ic: String = "",
    @SerializedName("employer") val employer: String = "",
    @SerializedName("position") val position: String = "",
    @SerializedName("case_number") val caseNumber: String = "",
    @SerializedName("law") val law: String = "",
    @SerializedName("sentence") val sentence: String = "",
)

// ──────────────────────────────────────────────────────────────────────────────
// 模块6: SSM 公司注册号查询
// ──────────────────────────────────────────────────────────────────────────────

data class SsmResponse(
    @SerializedName("ssm_number") val ssmNumber: String = "",
    @SerializedName("entity_type") val entityType: String = "",
    val rawJson: String = "",
)

// ──────────────────────────────────────────────────────────────────────────────
// 模块7: 公司详情搜索
// ──────────────────────────────────────────────────────────────────────────────

data class CompanyResponse(
    val companies: List<CompanyInfo> = emptyList(),
    val rawJson: String = "",
)

data class CompanyInfo(
    @SerializedName("name") val name: String = "",
    @SerializedName("category") val category: String = "",
    @SerializedName("address") val address: String = "",
    @SerializedName("website") val website: String = "",
    @SerializedName("source") val source: String = "",
)

// ──────────────────────────────────────────────────────────────────────────────
// 模块8: 社交媒体用户名搜索
// ──────────────────────────────────────────────────────────────────────────────

data class SocialResponse(
    val profiles: List<SocialProfile> = emptyList(),
    val rawJson: String = "",
)

data class SocialProfile(
    @SerializedName("platform") val platform: String = "",
    @SerializedName("url") val url: String = "",
    @SerializedName("username") val username: String = "",
)

// ──────────────────────────────────────────────────────────────────────────────
// 模块9: BNM 国家银行消费者警示
// ──────────────────────────────────────────────────────────────────────────────

data class BnmResponse(
    val alerts: List<BnmAlert> = emptyList(),
    val rawJson: String = "",
)

data class BnmAlert(
    @SerializedName("name") val name: String = "",
    @SerializedName("website") val website: String = "",
)
