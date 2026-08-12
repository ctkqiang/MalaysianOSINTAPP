package com.osint.malaysia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.osint.malaysia.ui.navigation.MainNavigation
import com.osint.malaysia.ui.theme.MalaysianOSINTTheme
import com.osint.malaysia.util.AppLanguage
import com.osint.malaysia.util.LocalStrings
import com.osint.malaysia.util.stringsFor
import com.osint.malaysia.viewmodel.MainViewModel
import com.osint.malaysia.viewmodel.SettingsViewModel
import com.osint.malaysia.viewmodel.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val svm: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(applicationContext))
            val mvm: MainViewModel = viewModel()
            val theme by svm.themeMode.collectAsState()
            val lang by svm.language.collectAsState()
            val accepted by svm.disclaimerAccepted.collectAsState()
            val init by svm.initialized.collectAsState()
            val dark = when (theme) { ThemeMode.DARK -> true; ThemeMode.LIGHT -> false; ThemeMode.AUTO -> androidx.compose.foundation.isSystemInDarkTheme() }

            CompositionLocalProvider(LocalStrings provides stringsFor(lang)) {
                MalaysianOSINTTheme(dark = dark) {
                    if (!init) { /* loading */ }
                    else if (!accepted) DisclaimerDialog(lang = lang, onAccept = { svm.acceptDisclaimer() })
                    else MainNavigation(vm = mvm, svm = svm)
                }
            }
        }
    }
}

private data class TosTexts(val title: String, val intro: String, val pdpa: String, val misuse: String, val liability: String, val disagree: String, val agree: String, val exit: String)

private val TosEN = TosTexts(
    title = "Terms of Use",
    intro = "Welcome to MalaysianOSINT. This tool helps verify and prevent online scams in Malaysia. By using this app, you agree to the following:",
    pdpa = "This app does NOT collect, store, or transmit personal data. We comply with the Malaysian Personal Data Protection Act 2010 (PDPA).",
    misuse = "This tool is for scam prevention and authorized security research only. Do NOT use for harassment, stalking, or illegal activity.",
    liability = "All data from public government sources (PDRM, SSPI, SSM, e-Judgment, BNM). No liability for misuse.",
    disagree = "If you disagree, please uninstall this application.",
    agree = "I Agree",
    exit = "Exit"
)

private val TosMS = TosTexts(
    title = "Syarat Penggunaan",
    intro = "Selamat datang ke MalaysianOSINT. Alat ini membantu mengesahkan dan mencegah penipuan dalam talian di Malaysia. Dengan menggunakan aplikasi ini, anda bersetuju dengan:",
    pdpa = "Aplikasi ini TIDAK mengumpul, menyimpan, atau menghantar data peribadi. Kami mematuhi Akta Perlindungan Data Peribadi 2010 (PDPA).",
    misuse = "Alat ini hanya untuk pencegahan penipuan dan penyelidikan keselamatan. JANGAN guna untuk gangguan, mengintai, atau aktiviti haram.",
    liability = "Semua data dari sumber kerajaan awam (PDRM, SSPI, SSM, e-Judgment, BNM). Tiada liabiliti atas penyalahgunaan.",
    disagree = "Jika anda tidak bersetuju, sila nyahpasang aplikasi ini.",
    agree = "Saya Setuju",
    exit = "Keluar"
)

private val TosZH = TosTexts(
    title = "使用条款",
    intro = "欢迎使用 MalaysianOSINT。本工具帮助验证和预防马来西亚的网络诈骗。使用本应用即表示您同意以下条款：",
    pdpa = "本应用不会收集、存储或传输任何个人数据。我们遵守马来西亚《2010年个人数据保护法》(PDPA)。",
    misuse = "本工具仅供防诈骗和安全研究使用。请勿用于骚扰、跟踪或任何非法活动。",
    liability = "所有数据来源于公开政府渠道(PDRM, SSPI, SSM, e-Judgment, BNM)。对任何滥用行为不承担任何责任。",
    disagree = "如果您不同意，请卸载本应用。",
    agree = "我同意",
    exit = "退出"
)

@Composable
private fun DisclaimerDialog(lang: AppLanguage, onAccept: () -> Unit) {
    val t = when (lang) { AppLanguage.MS -> TosMS; AppLanguage.ZH -> TosZH; else -> TosEN }

    AlertDialog(
        onDismissRequest = {},
        title = { Text(t.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
        text = {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Text(t.intro, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp)); HorizontalDivider(); Spacer(Modifier.height(12.dp))
                Bullet(t.pdpa); Spacer(Modifier.height(8.dp))
                Bullet(t.misuse); Spacer(Modifier.height(8.dp))
                Bullet(t.liability)
                Spacer(Modifier.height(12.dp))
                Text(t.disagree, style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onAccept, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) { Text(t.agree) }
        },
        dismissButton = {
            TextButton(onClick = { android.os.Process.killProcess(android.os.Process.myPid()) }) { Text(t.exit) }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large
    )
}

@Composable
private fun Bullet(text: String) {
    Row(Modifier.padding(vertical = 2.dp)) {
        Text("\u2022 ", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
        Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }
}
