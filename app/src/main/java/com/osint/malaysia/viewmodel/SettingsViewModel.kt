/* 马来西亚OSINT — 设置视图模型 */

package com.osint.malaysia.viewmodel

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.osint.malaysia.util.LogUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "osint_settings")

enum class ThemeMode { AUTO, LIGHT, DARK }

class SettingsViewModel(private val context: Context) : ViewModel() {

    private val tag = "SettingsViewModel"

    companion object {
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_TRANSLATE_CHINESE = booleanPreferencesKey("translate_chinese")
    }

    private val _themeMode = MutableStateFlow(ThemeMode.DARK)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _translateChinese = MutableStateFlow(true)
    val translateChinese: StateFlow<Boolean> = _translateChinese.asStateFlow()

    init {
        viewModelScope.launch {
            context.dataStore.data.collect { preferences ->
                val modeStr = preferences[KEY_THEME_MODE] ?: "DARK"
                _themeMode.value = try { ThemeMode.valueOf(modeStr) } catch (_: Exception) { ThemeMode.DARK }
                _translateChinese.value = preferences[KEY_TRANSLATE_CHINESE] ?: true
                LogUtil.i(tag, "设置加载完成 → 主题: ${_themeMode.value}, 中文翻译: ${_translateChinese.value}")
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        LogUtil.i(tag, "设置主题模式: $mode")
        viewModelScope.launch {
            context.dataStore.edit { it[KEY_THEME_MODE] = mode.name }
            _themeMode.value = mode
        }
    }

    fun setTranslateChinese(enabled: Boolean) {
        LogUtil.i(tag, "设置中文翻译: $enabled")
        viewModelScope.launch {
            context.dataStore.edit { it[KEY_TRANSLATE_CHINESE] = enabled }
            _translateChinese.value = enabled
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(context.applicationContext) as T
            }
            throw IllegalArgumentException("未知ViewModel类: ${modelClass.name}")
        }
    }
}
