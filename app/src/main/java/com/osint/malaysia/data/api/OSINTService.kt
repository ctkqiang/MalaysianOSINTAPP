/* 马来西亚OSINT — Retrofit API服务接口 */

package com.osint.malaysia.data.api

import com.osint.malaysia.model.ECourtSearchRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface OSINTService {

    /* PDRM Semak Mule 反诈骗查询 */
    @POST("api/mule/get_search_data.php")
    suspend fun checkSemakMule(
        @Header("apikey") apiKey: String = "j3j389#nklala2",
        @Header("Origin") origin: String = "https://semakmule.rmp.gov.my",
        @Header("Referer") referer: String = "https://semakmule.rmp.gov.my/",
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Response<String>

    /* SSPI 移民局身份证状态查询 */
    @POST
    suspend fun checkSSPI(
        @Url url: String,
        @Body body: okhttp3.RequestBody
    ): Response<String>

    /* PDRM 通缉名单 */
    @GET("orang-dikehendaki")
    suspend fun getWantedList(): Response<String>

    /* SPRM 反贪会腐败罪犯名单 */
    @GET
    suspend fun getSPRMList(@Url url: String): Response<String>

    /* e-Court 电子法庭判决搜索 */
    @POST("EJudgmentWeb/Search")
    suspend fun searchECourt(@Body request: ECourtSearchRequest): Response<String>

    /* 企业黄页搜索 */
    @GET
    suspend fun searchYellowPages(
        @Url url: String
    ): Response<String>

    /* BNM 国家银行消费者警示名单 */
    @GET("financial-consumer-alert-list")
    suspend fun getBNMAlertList(): Response<String>
}
