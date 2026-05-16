package xin.ctkqiang.malaysianosint.ui.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import xin.ctkqiang.malaysianosint.ui.theme.ThemeMode

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "malaysian_osint_settings")

/**
 * 应用设置持久化存储
 *
 * 使用 Jetpack DataStore 保存用户偏好：
 * - 主题模式 (自动 / 浅色 / 深色)
 * - 自动翻译 (开启 / 关闭)
 * - API 基础地址
 */
class SettingsStore(private val context: Context) {

    companion object {
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_AUTO_TRANSLATE = booleanPreferencesKey("auto_translate")
        private val KEY_API_BASE_URL = stringPreferencesKey("api_base_url")

        const val DEFAULT_BASE_URL = "http://10.0.2.2:8080"
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        when (prefs[KEY_THEME_MODE]) {
            "LIGHT" -> ThemeMode.LIGHT
            "DARK" -> ThemeMode.DARK
            else -> ThemeMode.AUTO
        }
    }

    val autoTranslate: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_AUTO_TRANSLATE] ?: true
    }

    val apiBaseUrl: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_API_BASE_URL] ?: DEFAULT_BASE_URL
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = mode.name
        }
    }

    suspend fun setAutoTranslate(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_AUTO_TRANSLATE] = enabled
        }
    }

    suspend fun setApiBaseUrl(url: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_API_BASE_URL] = url
        }
    }
}
