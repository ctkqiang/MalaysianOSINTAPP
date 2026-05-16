/* 马来西亚OSINT — SSM企业注册号解析器 */

package com.osint.malaysia.util

import com.osint.malaysia.model.SSMEntityType
import com.osint.malaysia.model.SSMInfo

object SSMParser {

    private const val TAG = "SSMParser"
    private const val SSM_LENGTH = 12

    /* 规范化SSM注册号为12位数字 */
    fun normalize(input: String): String {
        val digits = input.filter { it.isDigit() }
        return if (digits.length >= SSM_LENGTH) {
            digits.take(SSM_LENGTH)
        } else {
            digits.padStart(SSM_LENGTH, '0')
        }
    }

    /* 解析SSM注册号 */
    fun parse(input: String): SSMInfo? {
        val digits = normalize(input)

        if (digits.length != SSM_LENGTH) {
            LogUtil.w(TAG, "SSM注册号长度异常: ${digits.length} → $input")
            return null
        }

        /* 提取实体类型代码: 第5-6位（0-indexed: 4-5） */
        val entityCode = digits.substring(4, 6)
        val entityType = SSMEntityType.fromCode(entityCode)

        val result = SSMInfo(
            registrationNumber = digits,
            entityCode = entityCode,
            entityType = entityType.name,
            entityTypeCn = entityType.nameCn
        )

        LogUtil.i(TAG, "SSM解析结果 → 注册号: ${digits}, 实体类型: ${entityType.nameCn}($entityCode)")
        return result
    }

    /* 验证SSM注册号格式 */
    fun isValid(input: String): Boolean {
        val digits = input.filter { it.isDigit() }
        return digits.length >= 10
    }
}
