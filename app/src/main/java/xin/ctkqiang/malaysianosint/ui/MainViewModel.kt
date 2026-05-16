package xin.ctkqiang.malaysianosint.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import xin.ctkqiang.malaysianosint.data.model.*
import xin.ctkqiang.malaysianosint.data.repository.OsintRepository
import xin.ctkqiang.malaysianosint.ui.settings.SettingsStore
import xin.ctkqiang.malaysianosint.ui.theme.ThemeMode

/**
 * 主视图模型 — MVVM 架构核心
 *
 * 持有 OsintRepository (数据层) 和 SettingsStore (设置层)，
 * 通过 StateFlow 驱动 Compose UI 更新。
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = OsintRepository()
    val settingsStore = SettingsStore(application)

    /** 主题模式 */
    val themeMode: StateFlow<ThemeMode> = settingsStore.themeMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeMode.AUTO)

    /** 自动翻译开关 */
    val autoTranslate: StateFlow<Boolean> = settingsStore.autoTranslate
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    /** API 地址 */
    val apiBaseUrl: StateFlow<String> = settingsStore.apiBaseUrl
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsStore.DEFAULT_BASE_URL)

    /** 操作日志 */
    val log: StateFlow<List<LogEntry>> = repository.log

    /** 查询状态 */
    val queryStatus: StateFlow<QueryStatus> = repository.status

    /** 活跃模块 */
    val activeModule: StateFlow<String> = repository.activeModule

    /** 原始结果 JSON */
    val rawResult: StateFlow<String> = repository.rawResult

    /** 结果数量 */
    val resultCount: StateFlow<Int> = repository.resultCount

    /** 搜索栏文本 */
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /** 当前选中的 OSINT 模块 */
    private val _selectedModule = MutableStateFlow("pdrm")
    val selectedModule: StateFlow<String> = _selectedModule.asStateFlow()

    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun selectModule(module: String) { _selectedModule.value = module }

    fun executeQuery() {
        val query = _searchQuery.value.trim()
        if (query.isBlank()) {
            repository.resetStatus()
            return
        }
        viewModelScope.launch {
            repository.executeQuery(_selectedModule.value, query, apiBaseUrl.value)
        }
    }

    fun searchSocial() {
        val query = _searchQuery.value.trim()
        if (query.isBlank()) return
        repository.searchSocialLocally(query)
    }

    fun clearLog() = repository.clearLog()

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsStore.setThemeMode(mode) }
    }

    fun setAutoTranslate(enabled: Boolean) {
        viewModelScope.launch { settingsStore.setAutoTranslate(enabled) }
    }
}
