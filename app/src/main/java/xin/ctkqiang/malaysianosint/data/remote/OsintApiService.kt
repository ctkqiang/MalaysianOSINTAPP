package xin.ctkqiang.malaysianosint.data.remote

import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import xin.ctkqiang.malaysianosint.data.model.*
import java.util.concurrent.TimeUnit

/**
 * 马来西亚 OSINT API 服务层
 *
 * 负责向各大马政府公开数据库发起查询请求，
 * 解析 JSON 与 HTML 响应，返回结构化数据模型。
 * 所有网络请求在 IO 协程上下文中执行。
 *
 * 注意：本工具仅查询马来西亚政府公开 API / 公开网页，
 * 不涉及任何未经授权的数据访问。
 */
class OsintApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    /**
     * 根据模块类型和查询参数向本地代理 (C 后端) 或直连 API 发起请求。
     * 原始 C 项目通过 localhost:8080 代理所有请求；
     * 在 Android 版中改为直连目标 API 或通过 Duktape/WebView 桥接。
     *
     * 当前实现：直接抓取目标公开页面并解析 HTML/JSON。
     */
    suspend fun query(
        baseUrl: String,
        queryParam: String,
        queryValue: String,
    ): String = withContext(Dispatchers.IO) {
        val url = if (queryValue.isNotBlank()) {
            "$baseUrl?$queryParam=${java.net.URLEncoder.encode(queryValue, "UTF-8")}"
        } else baseUrl

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0 (Android; Malaysia OSINT)")
            .header("Accept", "application/json, text/html")
            .build()

        client.newCall(request).execute().use { response ->
            response.body?.string() ?: ""
        }
    }

    /** 解析 JSON 为指定类型 */
    inline fun <reified T> parseJson(json: String): T = gson.fromJson(json, T::class.java)

    /** 解析 HTML 为 Jsoup Document */
    fun parseHtml(html: String): Document = Jsoup.parse(html)

    // ──────────────────────────────────────────────────────────────────────────
    // 模块1: PDRM 反洗钱核查 (Semak Mule)
    // ──────────────────────────────────────────────────────────────────────────

    fun parsePdrmResponse(json: String): PdrmResponse = try {
        val root = JsonParser.parseString(json).asJsonObject
        val cases = root.getAsJsonArray("cases")?.map {
            PdrmCase(
                bank = it.asJsonObject.get("bank")?.asString ?: "",
                account = it.asJsonObject.get("account")?.asString ?: "",
                reports = it.asJsonObject.get("reports")?.asInt ?: 0,
            )
        } ?: emptyList()
        PdrmResponse(cases = cases, rawJson = json)
    } catch (e: Exception) {
        PdrmResponse(rawJson = json)
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 模块2+4+5: SSPI + 通缉 + 反贪 (联合查询)
    // ──────────────────────────────────────────────────────────────────────────

    fun parseSspiResponse(json: String): SspiResponse = try {
        gson.fromJson(json, SspiResponse::class.java).copy(rawJson = json)
    } catch (e: Exception) {
        SspiResponse(rawJson = json)
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 模块3: E-Court
    // ──────────────────────────────────────────────────────────────────────────

    fun parseEcourtResponse(json: String): EcourtResponse = try {
        val root = JsonParser.parseString(json).asJsonObject
        val cases = root.getAsJsonArray("cases")?.map {
            EcourtCase(
                caseNumber = it.asJsonObject.get("case_number")?.asString ?: "",
                court = it.asJsonObject.get("court")?.asString ?: "",
                judge = it.asJsonObject.get("judge")?.asString ?: "",
                dateOfAppeal = it.asJsonObject.get("date_of_ap")?.asString ?: "",
            )
        } ?: emptyList()
        EcourtResponse(
            totalResults = root.get("total")?.asInt ?: cases.size,
            cases = cases,
            rawJson = json,
        )
    } catch (e: Exception) {
        EcourtResponse(rawJson = json)
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 模块6: SSM 注册查询
    // ──────────────────────────────────────────────────────────────────────────

    fun parseSsmResponse(json: String): SsmResponse = try {
        gson.fromJson(json, SsmResponse::class.java).copy(rawJson = json)
    } catch (e: Exception) {
        SsmResponse(rawJson = json)
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 模块7: 公司详情搜索
    // ──────────────────────────────────────────────────────────────────────────

    fun parseCompanyResponse(json: String): CompanyResponse = try {
        val root = JsonParser.parseString(json).asJsonObject
        val companies = root.getAsJsonArray("companies")?.map {
            CompanyInfo(
                name = it.asJsonObject.get("name")?.asString ?: "",
                category = it.asJsonObject.get("category")?.asString ?: "",
                address = it.asJsonObject.get("address")?.asString ?: "",
                website = it.asJsonObject.get("website")?.asString ?: "",
                source = it.asJsonObject.get("source")?.asString ?: "SSM数据库",
            )
        } ?: emptyList()
        CompanyResponse(companies = companies, rawJson = json)
    } catch (e: Exception) {
        CompanyResponse(rawJson = json)
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 模块8: 社交媒体搜索 — 在各平台搜索用户名
    // ──────────────────────────────────────────────────────────────────────────

    fun parseSocialResponse(json: String): SocialResponse = try {
        val root = JsonParser.parseString(json).asJsonObject
        val profiles = root.getAsJsonArray("profiles")?.map {
            SocialProfile(
                platform = it.asJsonObject.get("platform")?.asString ?: "",
                url = it.asJsonObject.get("url")?.asString ?: "",
                username = it.asJsonObject.get("username")?.asString ?: "",
            )
        } ?: emptyList()
        SocialResponse(profiles = profiles, rawJson = json)
    } catch (e: Exception) {
        SocialResponse(rawJson = json)
    }

    /** 在主流平台搜索用户名，返回推测的社媒链接 */
    fun searchSocialPlatforms(username: String): SocialResponse {
        val platforms = listOf(
            SocialProfile("Facebook", "https://www.facebook.com/$username", username),
            SocialProfile("Instagram", "https://www.instagram.com/$username", username),
            SocialProfile("Twitter/X", "https://x.com/$username", username),
            SocialProfile("LinkedIn", "https://www.linkedin.com/in/$username", username),
            SocialProfile("GitHub", "https://github.com/$username", username),
            SocialProfile("TikTok", "https://www.tiktok.com/@$username", username),
            SocialProfile("YouTube", "https://www.youtube.com/@$username", username),
            SocialProfile("Reddit", "https://www.reddit.com/user/$username", username),
        )
        return SocialResponse(profiles = platforms, rawJson = "")
    }

    // ──────────────────────────────────────────────────────────────────────────
    // 模块9: BNM 消费者警示列表抓取
    // ──────────────────────────────────────────────────────────────────────────

    fun parseBnmResponse(json: String): BnmResponse = try {
        val root = JsonParser.parseString(json).asJsonObject
        val alerts = root.getAsJsonArray("alerts")?.map {
            BnmAlert(
                name = it.asJsonObject.get("name")?.asString ?: "",
                website = it.asJsonObject.get("website")?.asString ?: "",
            )
        } ?: emptyList()
        BnmResponse(alerts = alerts, rawJson = json)
    } catch (e: Exception) {
        BnmResponse(rawJson = json)
    }
}
