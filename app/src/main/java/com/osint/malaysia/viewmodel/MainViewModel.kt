/* 马来西亚OSINT — 主视图模型 */

package com.osint.malaysia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osint.malaysia.data.repository.OSINTRepository
import com.osint.malaysia.model.*
import com.osint.malaysia.util.LogUtil
import com.osint.malaysia.util.SSMParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val tag = "MainViewModel"
    private val repository = OSINTRepository()

    /* 查询状态 */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /* Semak Mule 反诈骗结果 */
    private val _semakMuleResult = MutableStateFlow("")
    val semakMuleResult: StateFlow<String> = _semakMuleResult.asStateFlow()

    /* 身份证综合查询结果 */
    private val _idCheckResult = MutableStateFlow<IDCheckResult?>(null)
    val idCheckResult: StateFlow<IDCheckResult?> = _idCheckResult.asStateFlow()

    /* SSM解析结果 */
    private val _ssmResult = MutableStateFlow<SSMInfo?>(null)
    val ssmResult: StateFlow<SSMInfo?> = _ssmResult.asStateFlow()

    /* 企业搜索结果 */
    private val _companyResult = MutableStateFlow<CompanyInfo?>(null)
    val companyResult: StateFlow<CompanyInfo?> = _companyResult.asStateFlow()

    /* e-Court法庭结果 */
    private val _ecourtResult = MutableStateFlow("")
    val ecourtResult: StateFlow<String> = _ecourtResult.asStateFlow()

    /* BNM警示名单 */
    private val _bnmResult = MutableStateFlow<BNMResponse?>(null)
    val bnmResult: StateFlow<BNMResponse?> = _bnmResult.asStateFlow()

    /* 社交媒体搜索结果（流式） */
    private val _socialResults = MutableStateFlow<List<SocialResult>>(emptyList())
    val socialResults: StateFlow<List<SocialResult>> = _socialResults.asStateFlow()

    private val _isSocialSearching = MutableStateFlow(false)
    val isSocialSearching: StateFlow<Boolean> = _isSocialSearching.asStateFlow()

    /* 执行Semak Mule查询 */
    fun querySemakMule(query: String) {
        LogUtil.i(tag, "执行Semak Mule查询: $query")
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.querySemakMule(query)
                .onSuccess { _semakMuleResult.value = it }
                .onFailure { _errorMessage.value = "查询失败: ${it.message}" }

            _isLoading.value = false
        }
    }

    /* 执行身份证综合查询 */
    fun queryIDComprehensive(icNumber: String) {
        LogUtil.i(tag, "执行身份证综合查询: $icNumber")
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.queryIDComprehensive(icNumber)
                .onSuccess { _idCheckResult.value = it }
                .onFailure { _errorMessage.value = "身份证查询失败: ${it.message}" }

            _isLoading.value = false
        }
    }

    /* 解析SSM注册号 */
    fun parseSSM(input: String) {
        LogUtil.i(tag, "解析SSM注册号: $input")
        val result = SSMParser.parse(input)
        _ssmResult.value = result
        if (result == null) {
            _errorMessage.value = "SSM注册号格式无效"
        }
    }

    /* 企业综合查询 */
    fun queryCompany(keyword: String) {
        LogUtil.i(tag, "执行企业综合查询: $keyword")
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.queryCompanyComprehensive(keyword)
                .onSuccess { (company, semakMule) ->
                    _companyResult.value = company
                    _semakMuleResult.value = semakMule
                }
                .onFailure { _errorMessage.value = "企业查询失败: ${it.message}" }

            _isLoading.value = false
        }
    }

    /* e-Court法庭搜索 */
    fun searchECourt(name: String) {
        LogUtil.i(tag, "执行e-Court搜索: $name")
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.searchECourt(name)
                .onSuccess { _ecourtResult.value = it }
                .onFailure { _errorMessage.value = "法庭查询失败: ${it.message}" }

            _isLoading.value = false
        }
    }

    /* BNM消费者警示 */
    fun loadBNMAlert() {
        LogUtil.i(tag, "加载BNM消费者警示名单")
        viewModelScope.launch {
            _isLoading.value = true
            repository.queryBNMAlert()
                .onSuccess { _bnmResult.value = it }
                .onFailure { _errorMessage.value = "BNM数据加载失败: ${it.message}" }
            _isLoading.value = false
        }
    }

    /* 社交媒体搜索 */
    fun searchSocial(username: String) {
        LogUtil.i(tag, "执行社交媒体搜索: $username")
        viewModelScope.launch {
            _isSocialSearching.value = true
            _socialResults.value = emptyList()

            repository.searchSocialMedia(username) { result ->
                _socialResults.value = _socialResults.value + result
            }

            _isSocialSearching.value = false
        }
    }

    /* 清除错误 */
    fun clearError() {
        _errorMessage.value = null
    }
}
