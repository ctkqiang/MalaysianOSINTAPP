/* 马来西亚OSINT — 数据模型定义 */

package com.osint.malaysia.model

import com.google.gson.annotations.SerializedName

/* PDRM Semak Mule 反诈骗查询结果 */
data class SemakMuleResponse(
    @SerializedName("count") val count: Int? = 0,
    @SerializedName("table_data") val tableData: List<List<String>>? = null
)

/* SSPI 移民局身份证状态 */
data class SSPIResult(
    val statusCode: String = "",
    val rawHtml: String = ""
)

/* MyKad 本地解析结果 */
data class MyKadInfo(
    val birthday: String = "",
    val province: String = "",
    val identifier: String = "",
    val provinceCn: String = ""
)

/* PDRM 通缉人员 */
data class WantedPerson(
    val name: String = "",
    val age: String = "",
    val photoUrl: String = ""
)

/* SPRM 反贪会腐败罪犯 */
data class SPRMCase(
    val name: String = "",
    val ic: String = "",
    val state: String = "",
    val employer: String = "",
    val position: String = "",
    val caseNo: String = "",
    val charge: String = "",
    val law: String = "",
    val sentence: String = "",
    val imageUrl: String = ""
)

/* e-Court 电子法庭判决 */
data class ECourtCase(
    val caseNo: String = "",
    val title: String = "",
    val date: String = "",
    val court: String = "",
    val judge: String = "",
    val parties: String = "",
    val documentId: String = ""
)

data class ECourtSearchRequest(
    @SerializedName("Param") val param: ECourtParam
)

data class ECourtParam(
    @SerializedName("Search") val search: String,
    @SerializedName("JurisdictionType") val jurisdictionType: String = "ALL",
    @SerializedName("CourtCategory") val courtCategory: String = "",
    @SerializedName("Court") val court: String = "",
    @SerializedName("JudgeName") val judgeName: String = "",
    @SerializedName("CaseType") val caseType: String = "",
    @SerializedName("DateOfAPFrom") val dateOfAPFrom: String = "",
    @SerializedName("DateOfAPTo") val dateOfAPTo: String = "",
    @SerializedName("DateOfResultFrom") val dateOfResultFrom: String = "",
    @SerializedName("DateOfResultTo") val dateOfResultTo: String = "",
    @SerializedName("CurrPage") val currPage: Int = 1,
    @SerializedName("Ordering") val ordering: String = "DATE_OF_AP_DESC"
)

/* e-Court ASMX响应封装 — 匹配identity_scanner的JSON键名 */
data class ECourtEnvelope(
    @SerializedName("d") val d: ECourtResult? = null
)

data class ECourtResult(
    @SerializedName("__type") val type: String = "",
    @SerializedName("ListOfSearchItem") val listOfSearchItem: List<ECourtItem>? = null,
    @SerializedName("RECS_PER_PAGE") val recsPerPage: Int = 0,
    @SerializedName("TOTAL_RECORD") val totalRecord: Int = 0,
    @SerializedName("TOTAL_PAGE") val totalPage: Int = 0
) {
    val searchList: List<ECourtItem> get() = listOfSearchItem ?: emptyList()
    val totalRecords: Int get() = totalRecord
    val currPage: Int get() = recsPerPage
}

data class ECourtItem(
    @SerializedName("No") val no: String = "",
    @SerializedName("CaseNo") val caseNo: String = "",
    @SerializedName("Parties") val parties: String = "",
    @SerializedName("KeyWord") val keyWord: String = "",
    @SerializedName("DateOfAP") val dateOfAP: String = "",
    @SerializedName("DateOfResult") val dateOfResult: String = "",
    @SerializedName("Judge") val judge: String = "",
    @SerializedName("CorumJudge") val corumJudge: String = "",
    @SerializedName("eJudgUniqueID") val eJudgUniqueID: String = "",
    @SerializedName("ListOfAPDoc") val listOfAPDoc: List<ECourtDoc>? = null
) {
    /* 去除HTML标签的干净字段 — 匹配identity_scanner的cleanCaseNo/cleanParties/cleanCorumJudge */
    val cleanCaseNo: String get() = caseNo.stripHtml()
    val cleanParties: String get() = parties.stripHtml()
    val cleanCorumJudge: String get() = corumJudge.stripHtml()
    val cleanJudge: String get() = judge.stripHtml()
    val cleanKeyWord: String get() = keyWord.stripHtml()
}

data class ECourtDoc(
    @SerializedName("FileName") val fileName: String = "",
    @SerializedName("DocumentID") val documentId: String = "",
    @SerializedName("DocumentType") val documentType: String = ""
)

/* SSM 企业注册号信息 */
data class SSMInfo(
    val registrationNumber: String = "",
    val entityCode: String = "",
    val entityType: String = "",
    val entityTypeCn: String = ""
)

/* 企业黄页搜索结果 */
data class CompanyInfo(
    val name: String = "",
    val category: String = "",
    val address: String = "",
    val website: String = "",
    val source: String = "MalaysiaYP"
)

/* BNM 国家银行消费者警示 */
data class BNMAlert(
    val name: String = "",
    val website: String = "",
    val date: String = ""
)

data class BNMResponse(
    val count: Int = 0,
    val entries: List<BNMAlert> = emptyList()
)

/* 社交媒体搜索结果 */
data class SocialResult(
    val platform: String = "",
    val url: String = "",
    val username: String = ""
)

/* 身份证综合查询结果 */
data class IDCheckResult(
    val sspi: SSPIResult? = null,
    val myKad: MyKadInfo? = null,
    val wanted: List<WantedPerson> = emptyList(),
    val sprmCases: List<SPRMCase> = emptyList()
)

/* 枚举：马来西亚州属/直辖区 */
enum class MalaysianState(val code: String, val nameCn: String) {
    JOHOR("01", "柔佛"),
    KEDAH("02", "吉打"),
    KELANTAN("03", "吉兰丹"),
    MALACCA("04", "马六甲"),
    NEGERI_SEMBILAN("05", "森美兰"),
    PAHANG("06", "彭亨"),
    PENANG("07", "槟城"),
    PERAK("08", "霹雳"),
    PERLIS("09", "玻璃市"),
    SELANGOR("10", "雪兰莪"),
    TERENGGANU("11", "登嘉楼"),
    SABAH("12", "沙巴"),
    SARAWAK("13", "砂拉越"),
    KUALA_LUMPUR("14", "吉隆坡"),
    LABUAN("15", "纳闽"),
    PUTRAJAYA("16", "布城");

    companion object {
        fun fromCode(code: String): MalaysianState? =
            entries.find { it.code == code }
    }
}

/* 枚举：SSM实体类型 */
enum class SSMEntityType(val code: String, val nameCn: String) {
    LOCAL_COMPANY("01", "本地公司"),
    FOREIGN_COMPANY("02", "外国公司"),
    BUSINESS("03", "商业实体"),
    LOCAL_LLP("04", "本地有限责任合伙企业"),
    FOREIGN_LLP("05", "外国有限责任合伙企业"),
    PROFESSIONAL_LLP("06", "本地专业执业有限责任合伙企业"),
    UNKNOWN("00", "未知实体");

    companion object {
        fun fromCode(code: String): SSMEntityType =
            entries.find { it.code == code } ?: UNKNOWN
    }
}

/* HTML标签清除 — 匹配identity_scanner的RegExp(r'<[^>]*>') */
private val htmlTagRegex = Regex("<[^>]*>")

fun String.stripHtml(): String = this.replace(htmlTagRegex, "").trim()
