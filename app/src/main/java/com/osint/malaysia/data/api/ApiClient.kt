/* 马来西亚OSINT — 网络客户端配置 */

package com.osint.malaysia.data.api

import com.osint.malaysia.util.LogUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ApiClient {

    private const val TAG = "ApiClient"
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 60L

    /* 信任所有证书 — 马来西亚政府网站部分证书可能过期 */
    private val unsafeOkHttpClient: OkHttpClient by lazy {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, SecureRandom())

            val loggingInterceptor = HttpLoggingInterceptor { message ->
                LogUtil.d(TAG, "[HTTP] $message")
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .addInterceptor(loggingInterceptor)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .header("User-Agent", "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
                        .build()
                    chain.proceed(request)
                }
                .build()
        } catch (e: Exception) {
            LogUtil.e(TAG, "创建OkHttpClient失败", e)
            OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .build()
        }
    }

    fun createRetrofit(baseUrl: String): Retrofit {
        LogUtil.d(TAG, "创建Retrofit实例: $baseUrl")
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(unsafeOkHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /* 直接获取OkHttpClient用于原生请求（HTML抓取等） */
    fun getHttpClient(): OkHttpClient = unsafeOkHttpClient
}
