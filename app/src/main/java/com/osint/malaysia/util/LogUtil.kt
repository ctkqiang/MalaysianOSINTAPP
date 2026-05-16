/* 马来西亚OSINT — 综合日志工具 */

package com.osint.malaysia.util

import android.util.Log

object LogUtil {

    private const val PREFIX = "[MYOSINT] "
    private var isDebugMode = true

    fun setDebugMode(debug: Boolean) {
        isDebugMode = debug
    }

    fun d(tag: String, message: String) {
        if (isDebugMode) Log.d(PREFIX + tag, message)
    }

    fun i(tag: String, message: String) {
        Log.i(PREFIX + tag, message)
    }

    fun w(tag: String, message: String) {
        Log.w(PREFIX + tag, message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(PREFIX + tag, message, throwable)
        } else {
            Log.e(PREFIX + tag, message)
        }
    }

    /* 网络请求日志 */
    fun network(tag: String, method: String, url: String, params: Map<String, String> = emptyMap()) {
        i(tag, "发起请求 → $method $url")
        if (params.isNotEmpty()) {
            d(tag, "请求参数: $params")
        }
    }

    /* 网络响应日志 */
    fun networkResponse(tag: String, url: String, statusCode: Int, bodyLength: Int) {
        i(tag, "收到响应 ← $url [HTTP $statusCode] [${bodyLength}字节]")
    }

    /* 错误日志 */
    fun apiError(tag: String, endpoint: String, error: String) {
        e(tag, "接口异常 [$endpoint]: $error")
    }
}
