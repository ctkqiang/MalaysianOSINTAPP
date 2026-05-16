/* 马来西亚OSINT — MyKad身份证号码本地解析器 */

package com.osint.malaysia.util

import com.osint.malaysia.model.MalaysianState
import com.osint.malaysia.model.MyKadInfo

object MyKadParser {

    private const val TAG = "MyKadParser"
    private const val MYKAD_LENGTH = 12

    /* 解析12位马来西亚身份证号码 */
    fun parse(icNumber: String): MyKadInfo? {
        val digits = icNumber.filter { it.isDigit() }

        if (digits.length < MYKAD_LENGTH) {
            LogUtil.w(TAG, "身份证号码长度不足: ${digits.length}/$MYKAD_LENGTH → $icNumber")
            return null
        }

        val normalized = digits.take(MYKAD_LENGTH)
        LogUtil.d(TAG, "解析身份证号码: $normalized")

        /* 提取出生日期: 前6位 YYMMDD */
        val yearPrefix = if (normalized.substring(0, 2).toInt() > 30) "19" else "20"
        val year = yearPrefix + normalized.substring(0, 2)
        val month = normalized.substring(2, 4)
        val day = normalized.substring(4, 6)
        val birthday = "$year-$month-$day"

        /* 提取州属代码: 第7-8位 */
        val stateCode = normalized.substring(6, 8)
        val state = MalaysianState.fromCode(stateCode)
        val province = state?.name ?: "未知"
        val provinceCn = state?.nameCn ?: "未知($stateCode)"

        /* 提取唯一标识符: 最后4位 */
        val identifier = normalized.substring(8, 12)

        val result = MyKadInfo(
            birthday = birthday,
            province = province,
            identifier = identifier,
            provinceCn = provinceCn
        )

        LogUtil.i(TAG, "身份证解析结果 → 出生日期: $birthday, 州属: $provinceCn, 尾号: $identifier")
        return result
    }

    /* 验证身份证号码格式 */
    fun isValid(icNumber: String): Boolean {
        val digits = icNumber.filter { it.isDigit() }
        return digits.length >= MYKAD_LENGTH
    }
}
