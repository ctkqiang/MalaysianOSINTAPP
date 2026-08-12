/* 马来西亚OSINT — SettingsVM: auto-detect locale + user override */
package com.osint.malaysia.viewmodel

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.osint.malaysia.util.AppLanguage
import com.osint.malaysia.util.LogUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

private val Context.dataStore by preferencesDataStore(name = "osint_settings")

enum class ThemeMode { AUTO, LIGHT, DARK }

class SettingsViewModel(private val context: Context) : ViewModel() {

    private val tag = "SettingsVM"

    companion object {
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode_v2")
        val KEY_TRANSLATE_CHINESE = booleanPreferencesKey("translate_chinese")
        val KEY_LANGUAGE = stringPreferencesKey("app_language")
        val KEY_DISCLAIMER_ACCEPTED = booleanPreferencesKey("disclaimer_accepted")
        val KEY_FIRST_RUN_DONE = booleanPreferencesKey("first_run_done")
    }

    private val _themeMode = MutableStateFlow(ThemeMode.AUTO)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _translateChinese = MutableStateFlow(true)
    val translateChinese: StateFlow<Boolean> = _translateChinese.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.ZH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _disclaimerAccepted = MutableStateFlow(false)
    val disclaimerAccepted: StateFlow<Boolean> = _disclaimerAccepted.asStateFlow()

    private val _initialized = MutableStateFlow(false)
    val initialized: StateFlow<Boolean> = _initialized.asStateFlow()

    init {
        viewModelScope.launch {
            context.dataStore.data.collect { prefs ->
                _themeMode.value = try { ThemeMode.valueOf(prefs[KEY_THEME_MODE] ?: "AUTO") } catch (_: Exception) { ThemeMode.AUTO }
                _translateChinese.value = prefs[KEY_TRANSLATE_CHINESE] ?: true
                _disclaimerAccepted.value = prefs[KEY_DISCLAIMER_ACCEPTED] ?: false
                _initialized.value = true

                val savedLang = prefs[KEY_LANGUAGE]
                val isFirstRun = prefs[KEY_FIRST_RUN_DONE] != true

                if (savedLang != null) {
                    _language.value = AppLanguage.fromCode(savedLang)
                } else {
                    // Auto-detect from device locale
                    val deviceLang = detectDeviceLanguage()
                    _language.value = deviceLang
                    context.dataStore.edit { it[KEY_LANGUAGE] = deviceLang.code }
                    LogUtil.i(tag, "首次运行 — 自动检测语言: ${deviceLang.code}")
                }

                if (isFirstRun) {
                    context.dataStore.edit { it[KEY_FIRST_RUN_DONE] = true }
                }

                LogUtil.i(tag, "设置: 主题=${_themeMode.value} 语言=${_language.value.code} 翻译=${_translateChinese.value}")
            }
        }
    }

    private fun detectDeviceLanguage(): AppLanguage {
        val loc = Locale.getDefault()
        return when {
            loc.language.equals("ms", ignoreCase = true) -> AppLanguage.MS
            loc.language.equals("zh", ignoreCase = true) -> AppLanguage.ZH
            loc.country.equals("MY", ignoreCase = true) && loc.language.equals("en", ignoreCase = true) -> AppLanguage.MS // Malaysian English → Malay
            else -> AppLanguage.EN
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { context.dataStore.edit { it[KEY_THEME_MODE] = mode.name }; _themeMode.value = mode }
    }

    fun setTranslateChinese(enabled: Boolean) {
        viewModelScope.launch { context.dataStore.edit { it[KEY_TRANSLATE_CHINESE] = enabled }; _translateChinese.value = enabled }
    }

    fun setLanguage(language: AppLanguage) {
        LogUtil.i(tag, "用户切换语言 → ${language.code}")
        viewModelScope.launch {
            context.dataStore.edit { it[KEY_LANGUAGE] = language.code }
            _language.value = language
        }
    }

    fun acceptDisclaimer() {
        viewModelScope.launch {
            context.dataStore.edit { it[KEY_DISCLAIMER_ACCEPTED] = true }
            _disclaimerAccepted.value = true
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) return SettingsViewModel(context.applicationContext) as T
            throw IllegalArgumentException("未知ViewModel: ${modelClass.name}")
        }
    }
}
