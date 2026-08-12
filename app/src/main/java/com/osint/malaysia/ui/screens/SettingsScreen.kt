package com.osint.malaysia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.osint.malaysia.ui.components.*
import com.osint.malaysia.ui.theme.*
import com.osint.malaysia.util.AppLanguage
import com.osint.malaysia.util.LocalStrings
import com.osint.malaysia.viewmodel.SettingsViewModel
import com.osint.malaysia.viewmodel.ThemeMode

@Composable
fun SettingsScreen(vm: SettingsViewModel) {
    val theme by vm.themeMode.collectAsState(); val trans by vm.translateChinese.collectAsState(); val lang by vm.language.collectAsState()
    val s = LocalStrings.current

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary))).padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.size(56.dp).clip(CircleShape).background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.25f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Shield, null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(30.dp)) }
                        Spacer(Modifier.height(10.dp)); Text(s.aboutApp, style = AppTypography.Title.copy(color = androidx.compose.ui.graphics.Color.White)); Text(s.aboutVer, style = AppTypography.Caption.copy(color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)))
                    }
                }
            }
        }

        item { SectionHeader(s.setAppear) }
        item { InfoCard { ThemeChip(Icons.Default.LightMode, s.setLight, s.setLightD, theme == ThemeMode.LIGHT) { vm.setThemeMode(ThemeMode.LIGHT) }; Spacer(Modifier.height(8.dp)); ThemeChip(Icons.Default.DarkMode, s.setDark, s.setDarkD, theme == ThemeMode.DARK) { vm.setThemeMode(ThemeMode.DARK) }; Spacer(Modifier.height(8.dp)); ThemeChip(Icons.Default.BrightnessAuto, s.setAuto, s.setAutoD, theme == ThemeMode.AUTO) { vm.setThemeMode(ThemeMode.AUTO) } } }

        item { SectionHeader(s.setLang) }
        item { InfoCard { LangChip("EN", s.setLangEn, lang == AppLanguage.EN) { vm.setLanguage(AppLanguage.EN) }; Spacer(Modifier.height(8.dp)); LangChip("MS", s.setLangMs, lang == AppLanguage.MS) { vm.setLanguage(AppLanguage.MS) }; Spacer(Modifier.height(8.dp)); LangChip("ZH", s.setLangZh, lang == AppLanguage.ZH) { vm.setLanguage(AppLanguage.ZH) } } }

        item { InfoCard { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Translate, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(s.setTrans, style = AppTypography.Body, color = MaterialTheme.colorScheme.onBackground); Text(s.setTransD, style = AppTypography.Caption, color = MaterialTheme.colorScheme.onSurfaceVariant) }; Switch(trans, { vm.setTranslateChinese(it) }, colors = SwitchDefaults.colors(checkedThumbColor = androidx.compose.ui.graphics.Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)) } } }

        item { SectionHeader(s.setAbout) }
        item {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)), border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FavoriteBorder, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp)); Spacer(Modifier.height(8.dp))
                    Text(s.aboutBestTitle, style = AppTypography.Title, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center); Spacer(Modifier.height(10.dp))
                    Text(s.aboutBestDesc, style = AppTypography.Body, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(14.dp)); HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant); Spacer(Modifier.height(14.dp))
                    Column(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(10.dp)); Text(s.aboutAuthor, style = AppTypography.Body, color = MaterialTheme.colorScheme.onSurface) }
                        Row(Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Email, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(10.dp)); Text(s.aboutEmail, style = AppTypography.Body, color = MaterialTheme.colorScheme.onSurface) }
                        Row(Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Code, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(10.dp)); Text(s.aboutRepo, style = AppTypography.Body, color = MaterialTheme.colorScheme.onSurface) }
                    }
                }
            }
        }
        item { Text(s.aboutDisc, style = AppTypography.Caption, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) }
    }
}

@Composable private fun ThemeChip(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String, sel: Boolean, onClick: () -> Unit) {
    Surface(shape = RoundedCornerShape(10.dp), color = if (sel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface, border = if (sel) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant), onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(12.dp)); Column { Text(title, style = AppTypography.Body, color = MaterialTheme.colorScheme.onBackground); Text(desc, style = AppTypography.Caption, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
    }
}

@Composable private fun LangChip(code: String, label: String, sel: Boolean, onClick: () -> Unit) {
    Surface(shape = RoundedCornerShape(10.dp), color = if (sel) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface, border = if (sel) BorderStroke(1.dp, MaterialTheme.colorScheme.secondary) else BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant), onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Text(code, style = AppTypography.Body.copy(fontWeight = FontWeight.Bold), color = if (sel) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(36.dp)); Spacer(Modifier.width(8.dp)); Text(label, style = AppTypography.Body, color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.weight(1f)) }
    }
}
