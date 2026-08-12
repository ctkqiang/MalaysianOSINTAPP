package com.osint.malaysia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.osint.malaysia.ui.components.*
import com.osint.malaysia.ui.theme.*
import com.osint.malaysia.util.LocalStrings
import com.osint.malaysia.viewmodel.MainViewModel

@Composable
fun HomeScreen(vm: MainViewModel) {
    var q by remember { mutableStateOf("") }
    val loading by vm.isLoading.collectAsState(); val res by vm.semakMuleResult.collectAsState()
    val bnm by vm.bnmResult.collectAsState(); val err by vm.errorMessage.collectAsState()
    val s = LocalStrings.current
    LaunchedEffect(Unit) { if (bnm == null) vm.loadBNMAlert() }

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {

        item {
            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))).padding(20.dp)) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Shield, null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(26.dp)); Spacer(Modifier.width(10.dp)); Text(s.homeTitle, style = AppTypography.Display.copy(color = androidx.compose.ui.graphics.Color.White)) }
                        Spacer(Modifier.height(4.dp)); Text(s.homeSubtitle, style = AppTypography.Body.copy(color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f)))
                        Spacer(Modifier.height(14.dp))
                        Surface(shape = RoundedCornerShape(10.dp), color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.18f)) {
                            Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Search, null, tint = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f), modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp))
                                androidx.compose.foundation.text.BasicTextField(value = q, onValueChange = { q = it }, textStyle = AppTypography.Body.copy(color = androidx.compose.ui.graphics.Color.White), modifier = Modifier.weight(1f), decorationBox = { if (q.isEmpty()) Text(s.homeHint, style = AppTypography.Body.copy(color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.5f))); it() }, singleLine = true)
                                if (q.isNotEmpty()) IconButton({ q = "" }, Modifier.size(16.dp)) { Icon(Icons.Default.Close, null, tint = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.5f)) }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Button({ if (q.isNotBlank()) vm.querySemakMule(q) }, enabled = !loading, shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.White, contentColor = MaterialTheme.colorScheme.primary), modifier = Modifier.fillMaxWidth().height(46.dp)) {
                            if (loading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary) else { Text(s.btnSearch, style = AppTypography.Subtitle) }
                        }
                    }
                }
            }
        }

        if (res != null || bnm != null) {
            item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val rows = res?.tableData ?: emptyList(); val has = rows.isNotEmpty() && rows.any { it.size >= 2 && it[1].isNotBlank() }
                StatCard(s.statMatch, "${rows.size}", if (has) Colors.Orange else Colors.Green, Modifier.weight(1f))
                StatCard(s.statSource, s.riskPdrm, Colors.Secondary, Modifier.weight(1f))
                StatCard("BNM", "${bnm?.count ?: 0}", Colors.Secondary, Modifier.weight(1f))
            } }
        }
        err?.let { item { ErrorBanner(it) { vm.clearError() } } }
        if (loading) item { LoadingOverlay(true) }

        res?.let { r ->
            val rows = r.tableData ?: emptyList()
            val has = rows.isNotEmpty() && rows.any { it.size >= 2 && it[1].isNotBlank() }
            val (icon, title, desc, color) = when { has && rows.size > 3 -> listOf<Any>(Icons.Default.GppBad, s.riskHigh, s.riskHighD, Colors.Orange); has -> listOf<Any>(Icons.Default.ErrorOutline, s.riskMed, s.riskMedD, Colors.Orange); else -> listOf<Any>(Icons.Default.CheckCircleOutline, s.riskClean, s.riskCleanD, Colors.Green) }
            item { FadeIn { RiskBanner(icon = icon as androidx.compose.ui.graphics.vector.ImageVector, title = title as String, desc = desc as String, color = color as androidx.compose.ui.graphics.Color) } }

            if (rows.isNotEmpty()) { item { FadeIn(delay = 80) { SectionHeader(s.lbRecord) } }; rows.forEachIndexed { idx, row -> item { FadeIn(delay = idx * 30) { InfoCard { Text("Record #${idx + 1}", style = AppTypography.Caption, color = MaterialTheme.colorScheme.primary); Spacer(Modifier.height(8.dp)); row.forEach { cell -> if (cell.isNotBlank()) Row(Modifier.padding(vertical = 1.dp)) { Icon(Icons.Default.Circle, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), modifier = Modifier.size(5.dp)); Spacer(Modifier.width(6.dp)); Text(cell, style = AppTypography.Body, color = MaterialTheme.colorScheme.onSurface) } } } } } } }
            else item { FadeIn(delay = 80) { InfoCard(border = BorderStroke(1.dp, Colors.Green.copy(alpha = 0.15f))) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Shield, null, tint = Colors.Green, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(10.dp)); Text(s.riskNoData, style = AppTypography.Body, color = Colors.Green) } } } }
        }

        bnm?.let { r -> item { SectionHeader(s.homeBnmTitle) }; if (r.entries.isEmpty()) item { EmptyState(s.homeBnmEmpty) } else items(r.entries.take(30)) { a -> InfoCard(border = BorderStroke(0.5.dp, Colors.Orange.copy(alpha = 0.2f))) { Text(a.name, style = AppTypography.Subtitle, color = MaterialTheme.colorScheme.onBackground); if (a.website.isNotBlank()) Text(a.website, style = AppTypography.Caption, color = MaterialTheme.colorScheme.onSurfaceVariant); if (a.date.isNotEmpty()) Text(a.date, style = AppTypography.Caption, color = MaterialTheme.colorScheme.onSurfaceVariant) } } }
    }
}
