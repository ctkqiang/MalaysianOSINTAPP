/* 马来西亚OSINT — Application初始化 */

package com.osint.malaysia

import android.app.Application
import com.osint.malaysia.util.LogUtil

class OSINTApplication : Application() {

    private val tag = "OSINTApplication"

    override fun onCreate() {
        super.onCreate()
        LogUtil.i(tag, "马来西亚OSINT Application初始化")
        LogUtil.setDebugMode(true)
    }
}
