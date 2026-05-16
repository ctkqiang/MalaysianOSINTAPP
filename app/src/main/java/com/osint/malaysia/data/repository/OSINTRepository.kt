/* 马来西亚OSINT — 数据仓库层（核心OSINT引擎） */

package com.osint.malaysia.data.repository

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.osint.malaysia.data.api.ApiClient
import com.osint.malaysia.data.api.OSINTService
import com.osint.malaysia.model.*
import com.osint.malaysia.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.regex.Pattern

class OSINTRepository {

    private val tag = "OSINTRepository"

    /* Retrofit服务实例 */
    private val semakMuleService = ApiClient.createRetrofit("https://semakmule.rmp.gov.my/")
        .create(OSINTService::class.java)

    private val rmpService = ApiClient.createRetrofit("https://www.rmp.gov.my/")
        .create(OSINTService::class.java)

    private val bnmService = ApiClient.createRetrofit("https://www.bnm.gov.my/")
        .create(OSINTService::class.java)

    private val httpClient = ApiClient.getHttpClient()

    /* ===== PDRM Semak Mule 反诈骗查询 ===== */
    suspend fun querySemakMule(query: String): Result<String> = withContext(Dispatchers.IO) {
        LogUtil.network(tag, "POST", "semakmule.rmp.gov.my", mapOf("query" to query))

        try {
            val body = mapOf(
                "data" to mapOf(
                    "category" to "telefon",
                    "bankAccount" to query,
                    "telNo" to query,
                    "companyName" to "",
                    "captcha" to ""
                )
            )

            val response = semakMuleService.checkSemakMule(body = body)
            LogUtil.networkResponse(tag, "semakmule", response.code(), response.body()?.length ?: 0)

            if (response.isSuccessful) {
                val responseBody = response.body() ?: ""
                Result.success(responseBody)
            } else {
                Result.failure(Exception("Semak Mule返回HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            LogUtil.apiError(tag, "semakmule", e.message ?: "未知错误")
            Result.failure(e)
        }
    }

    /* ===== SSPI 移民局身份证状态查询 ===== */
    suspend fun querySSPI(icNumber: String): Result<SSPIResult> = withContext(Dispatchers.IO) {
        LogUtil.network(tag, "POST", "sspi.imi.gov.my", mapOf("ic" to icNumber))

        try {
            val formBody = "txtIcNo=$icNumber&btnSemak=Semak"
            val requestBody = formBody.toRequestBody("application/x-www-form-urlencoded".toMediaType())

            val request = Request.Builder()
                .url("https://sspi.imi.gov.my/sspi/index.php?page=sspi/bm")
                .post(requestBody)
                .header("User-Agent", "libcurl-agent/1.0")
                .build()

            val response = httpClient.newCall(request).execute()
            val html = response.body?.string() ?: ""

            /* 解析 <span id="lblStatuscode"> */
            val statusCode = extractBetween(html, "<span id=\"lblStatuscode\">", "</span>")
                ?: extractBetween(html, "<span id='lblStatuscode'>", "</span>")
                ?: "查无记录"

            LogUtil.i(tag, "SSPI查询结果: $statusCode")
            Result.success(SSPIResult(statusCode = statusCode, rawHtml = html))
        } catch (e: Exception) {
            LogUtil.apiError(tag, "sspi", e.message ?: "未知错误")
            Result.failure(e)
        }
    }

    /* ===== PDRM 通缉名单查询 ===== */
    suspend fun queryWantedList(icNumber: String): Result<List<WantedPerson>> = withContext(Dispatchers.IO) {
        LogUtil.network(tag, "GET", "rmp.gov.my/orang-dikehendaki")

        try {
            val response = rmpService.getWantedList()

            if (response.isSuccessful) {
                val html = response.body() ?: ""
                val document = Jsoup.parse(html)
                val wantedElements = document.select("div.wanted-person")
                val wantedList = mutableListOf<WantedPerson>()

                for (element in wantedElements) {
                    val name = element.select("h3").text()
                    val age = element.select("span.age").text()
                    val photoUrl = element.select("img").attr("src")

                    if (name.contains(icNumber, ignoreCase = true)) {
                        wantedList.add(WantedPerson(name = name, age = age, photoUrl = photoUrl))
                    }
                }

                LogUtil.i(tag, "通缉名单匹配: ${wantedList.size}条记录")
                Result.success(wantedList)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            LogUtil.apiError(tag, "wanted", e.message ?: "未知错误")
            Result.success(emptyList())
        }
    }

    /* ===== SPRM 反贪会腐败罪犯查询 ===== */
    suspend fun querySPRM(keyword: String): Result<List<SPRMCase>> = withContext(Dispatchers.IO) {
        LogUtil.network(tag, "GET", "sprm.gov.my")

        try {
            val response = rmpService.getSPRMList("https://www.sprm.gov.my/index.php?page_id=96")

            if (response.isSuccessful) {
                val html = response.body() ?: ""
                val cases = parseSPRMHtml(html, keyword)
                LogUtil.i(tag, "SPRM匹配: ${cases.size}条记录")
                Result.success(cases)
            } else {
                Result.success(emptyList())
            }
        } catch (e: Exception) {
            LogUtil.apiError(tag, "sprm", e.message ?: "未知错误")
            Result.success(emptyList())
        }
    }

    /* ===== e-Court 电子法庭判决搜索 ===== */
    suspend fun searchECourt(name: String): Result<String> = withContext(Dispatchers.IO) {
        val url = "https://ejudgment.kehakiman.gov.my/EJudgmentWeb/eJudgmentService.asmx/GetEJudgmentPortalSearchList"
        LogUtil.network(tag, "POST", url, mapOf("name" to name))

        /* 构造JSON体 — null值用于空日期字段 */
        val jsonBody = """{"Param":{"CourtCategory":"","Court":"","JurisdictionType":"ALL","DateOfAPFrom":null,"DateOfAPTo":null,"DateOfResultFrom":null,"DateOfResultTo":null,"Search":"$name","JudgeName":"","CaseType":"","CurrPage":1,"Ordering":"DATE_OF_AP_DESC"}}"""

        val mediaType = "application/json; charset=UTF-8".toMediaType()
        val requestBody = jsonBody.toRequestBody(mediaType)

        /* 最多重试3次，间隔3秒 */
        var lastError: Exception? = null
        repeat(3) { attempt ->
            try {
                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .header("Accept", "application/json, text/javascript, */*; q=0.01")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Origin", "https://ejudgment.kehakiman.gov.my")
                    .header("Referer", "https://ejudgment.kehakiman.gov.my/ejudgmentweb/searchpage.aspx?JurisdictionType=ALL")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
                    .build()

                val response = httpClient.newCall(request).execute()
                val body = response.body?.string() ?: "{}"
                LogUtil.networkResponse(tag, "ecourt", response.code, body.length)

                if (response.isSuccessful && body.isNotBlank()) {
                    return@withContext Result.success(body)
                } else if (!response.isSuccessful) {
                    LogUtil.w(tag, "e-Court HTTP ${response.code}: ${body.take(200)}")
                }
            } catch (e: Exception) {
                lastError = e
                LogUtil.w(tag, "e-Court第${attempt + 1}次尝试失败: ${e.message}")
            }

            if (attempt < 2) delay(3000)
        }

        LogUtil.apiError(tag, "ecourt", lastError?.message ?: "3次重试后仍失败")
        Result.failure(lastError ?: Exception("e-Court请求失败(3次重试耗尽)"))
    }

    /* ===== BNM 国家银行消费者警示名单 ===== */
    suspend fun queryBNMAlert(): Result<BNMResponse> = withContext(Dispatchers.IO) {
        LogUtil.network(tag, "GET", "bnm.gov.my")

        try {
            val response = bnmService.getBNMAlertList()

            if (response.isSuccessful) {
                val html = response.body() ?: ""
                val document = Jsoup.parse(html)
                val tbody = document.select("tbody").first()
                val entries = mutableListOf<BNMAlert>()

                tbody?.select("tr")?.forEach { row ->
                    val cols = row.select("td")
                    if (cols.size >= 3) {
                        entries.add(
                            BNMAlert(
                                name = cols[0].text().trim(),
                                website = cols[1].text().trim(),
                                date = cols[2].text().trim()
                            )
                        )
                    }
                }

                val result = BNMResponse(count = entries.size, entries = entries)
                LogUtil.i(tag, "BNM警示名单: ${entries.size}条记录")
                Result.success(result)
            } else {
                Result.success(BNMResponse())
            }
        } catch (e: Exception) {
            LogUtil.apiError(tag, "bnm", e.message ?: "未知错误")
            Result.success(BNMResponse())
        }
    }

    /* ===== 企业黄页搜索 ===== */
    suspend fun searchCompany(keyword: String): Result<CompanyInfo?> = withContext(Dispatchers.IO) {
        LogUtil.network(tag, "GET", "malaysiayp.com", mapOf("s" to keyword))

        try {
            val url = "https://malaysiayp.com/?s=$keyword&location-address=&a=true"
            val request = Request.Builder().url(url).get().build()
            val response = httpClient.newCall(request).execute()
            val html = response.body?.string() ?: ""

            val document = Jsoup.parse(html)
            val name = document.select("h3").first()?.text() ?: ""
            val category = document.select("span.item-category").first()?.text() ?: ""
            val address = document.select("span.value").first()?.text() ?: ""
            val website = document.select("div.item-web").first()?.text() ?: ""

            if (name.isNotBlank()) {
                val company = CompanyInfo(
                    name = name,
                    category = category,
                    address = address,
                    website = website
                )
                LogUtil.i(tag, "企业搜索匹配: $name")
                Result.success(company)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            LogUtil.apiError(tag, "company", e.message ?: "未知错误")
            Result.success(null)
        }
    }

    /* ===== 社交媒体用户名搜索 ===== */
    suspend fun searchSocialMedia(
        username: String,
        onProgress: (SocialResult) -> Unit
    ) = withContext(Dispatchers.IO) {
        LogUtil.i(tag, "社交媒体搜索开始: $username")

        for (platform in SocialPlatforms.ALL) {
            try {
                val url = SocialPlatforms.getUrl(platform, username)
                val request = Request.Builder()
                    .url(url)
                    .head()
                    .build()

                val response = httpClient.newCall(request).execute()

                if (response.isSuccessful) {
                    val result = SocialResult(
                        platform = platform.name,
                        url = url,
                        username = username
                    )
                    LogUtil.d(tag, "[${platform.name}] 匹配: $url")
                    withContext(Dispatchers.Main) { onProgress(result) }
                }
            } catch (_: Exception) {
                /* 平台不可达，跳过 */
            }
        }

        LogUtil.i(tag, "社交媒体搜索完成")
    }

    /* ===== 身份证综合查询（SSPI + MyKad + Wanted + SPRM） ===== */
    suspend fun queryIDComprehensive(icNumber: String): Result<IDCheckResult> =
        withContext(Dispatchers.IO) {
            LogUtil.i(tag, "身份证综合查询开始: $icNumber")

            val myKad = MyKadParser.parse(icNumber)
            val sspiResult = querySSPI(icNumber).getOrNull()
            val wantedList = queryWantedList(icNumber).getOrDefault(emptyList())
            val sprmCases = querySPRM(icNumber).getOrDefault(emptyList())

            val result = IDCheckResult(
                sspi = sspiResult,
                myKad = myKad,
                wanted = wantedList,
                sprmCases = sprmCases
            )

            LogUtil.i(tag, "身份证综合查询完成 → SSPI:${sspiResult != null}, MyKad:${myKad != null}, 通缉:${wantedList.size}, 反贪:${sprmCases.size}")
            Result.success(result)
        }

    /* ===== 企业综合查询（Yellow Pages + Semak Mule） ===== */
    suspend fun queryCompanyComprehensive(keyword: String): Result<Pair<CompanyInfo?, String>> =
        withContext(Dispatchers.IO) {
            LogUtil.i(tag, "企业综合查询开始: $keyword")

            val company = searchCompany(keyword).getOrNull()
            val semakMuleResult = querySemakMule(keyword).getOrDefault("")

            LogUtil.i(tag, "企业综合查询完成")
            Result.success(Pair(company, semakMuleResult))
        }

    /* ===== HTML解析辅助函数 ===== */
    private fun extractBetween(text: String, start: String, end: String): String? {
        val startIndex = text.indexOf(start)
        if (startIndex == -1) return null
        val contentStart = startIndex + start.length
        val endIndex = text.indexOf(end, contentStart)
        if (endIndex == -1) return null
        return text.substring(contentStart, endIndex).trim()
    }

    private fun parseSPRMHtml(html: String, keyword: String): List<SPRMCase> {
        val cases = mutableListOf<SPRMCase>()
        try {
            val document = Jsoup.parse(html)
            val offenderDivs = document.select("div.div-pesalah")

            for (div in offenderDivs) {
                val text = div.text()
                if (text.contains(keyword, ignoreCase = true)) {
                    val img = div.select("img").first()?.attr("src") ?: ""
                    /* 从文本中提取各字段 */
                    val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }

                    cases.add(
                        SPRMCase(
                            name = lines.getOrElse(0) { "" },
                            ic = lines.getOrElse(1) { "" },
                            state = lines.getOrElse(2) { "" },
                            employer = lines.getOrElse(3) { "" },
                            position = lines.getOrElse(4) { "" },
                            caseNo = lines.getOrElse(5) { "" },
                            charge = lines.getOrElse(6) { "" },
                            law = if (html.contains("Seksyen 165")) "Seksyen 165 Kanun Keseksaan" else "",
                            sentence = lines.getOrElse(7) { "" },
                            imageUrl = img
                        )
                    )
                }
            }
        } catch (e: Exception) {
            LogUtil.e(tag, "SPRM HTML解析异常", e)
        }
        return cases
    }
}
