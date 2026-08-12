package com.osint.malaysia.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.osint.malaysia.model.ECourtItem
import com.osint.malaysia.ui.components.*
import com.osint.malaysia.ui.navigation.Routes
import com.osint.malaysia.ui.theme.*
import com.osint.malaysia.util.LocalStrings
import com.osint.malaysia.viewmodel.MainViewModel

@Composable
fun CourtScreen(vm: MainViewModel, nav: NavHostController? = null) {
    var q by remember { mutableStateOf("") }; val loading by vm.isLoading.collectAsState(); val ec by vm.ecourtResult.collectAsState(); val raw by vm.ecourtRawJson.collectAsState(); val err by vm.errorMessage.collectAsState()
    val s = LocalStrings.current

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { SectionHeader(s.ctTitle) }
        item { SearchBar(q, { q = it }, { vm.searchECourt(q) }, s.ctHint, !loading) }
        item { PrimaryButton(s.ctBtn, { if (q.isNotBlank()) vm.searchECourt(q) }, loading = loading, modifier = Modifier.fillMaxWidth()) }
        err?.let { item { ErrorBanner(it) { vm.clearError() } } }
        if (loading) item { LoadingOverlay(true) }

        ec?.let { r ->
            val cases = r.searchList ?: emptyList()
            item { SectionHeader(s.ctResult) }
            item { FadeIn { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) { StatCard(s.statMatch, "${cases.size}", Colors.Primary, Modifier.weight(1f)); StatCard(s.statTotal, "${r.totalRecords}", Colors.Secondary, Modifier.weight(1f)); StatCard(s.statPage, "${r.currPage}/${r.totalPage}", Colors.Secondary, Modifier.weight(1f)) } } }
            if (cases.isEmpty()) item { FadeIn(delay = 80) { InfoCard { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.CheckCircleOutline, null, tint = Colors.Green, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(10.dp)); Text(s.ctNoResult, style = AppTypography.Body, color = Colors.Green) } } } }
            else itemsIndexed(cases) { i, it -> CaseCard(i + 1, it, s, nav) }
        }
        if (ec == null && raw.isNotEmpty()) { item { SectionHeader(s.ctRaw) }; item { InfoCard { Text(try { com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(com.google.gson.JsonParser.parseString(raw)) } catch (_: Exception) { raw }.take(6000), style = AppTypography.Mono, color = MaterialTheme.colorScheme.onSurface) } } }
    }
}

@Composable private fun CaseCard(idx: Int, item: ECourtItem, s: com.osint.malaysia.util.UiStrings, nav: NavHostController?) {
    val docId = item.listOfAPDoc?.firstOrNull()?.documentId?.ifBlank { null } ?: item.eJudgUniqueID.ifBlank { null }
    val url = docId?.let { "https://efs.kehakiman.gov.my/EFSWeb/DocDownloader.aspx?DocumentID=$it&Inline=true" }

    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), modifier = Modifier.fillMaxWidth().then(if (url != null && nav != null) Modifier.clickable { nav.navigate(Routes.webview(url)) } else Modifier)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Balance, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("#$idx", style = AppTypography.Caption.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(8.dp)); Text(item.cleanKeyWord.ifBlank { item.cleanCaseNo.ifBlank { "Case #$idx" } }, style = AppTypography.Subtitle.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f)); if (url != null) { Icon(Icons.Default.PictureAsPdf, null, tint = Colors.Red, modifier = Modifier.size(20.dp)) } }
            Spacer(Modifier.height(10.dp)); HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant); Spacer(Modifier.height(10.dp))
            if (item.cleanCaseNo.isNotBlank()) CourtRow(s.lbCaseNo, item.cleanCaseNo)
            if (item.cleanParties.isNotBlank()) CourtRow(s.lbParties, item.cleanParties)
            if (item.cleanJudge.isNotBlank()) CourtRow(s.lbJudge, item.cleanJudge)
            if (url != null) { Spacer(Modifier.height(8.dp)); Text(s.ctPdf, style = AppTypography.Caption.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.primary) }
        }
    }
}

@Composable private fun CourtRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) { Text("$label  ", style = AppTypography.Caption.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(80.dp)); Text(value, style = AppTypography.Body, color = MaterialTheme.colorScheme.onSurface, maxLines = 2, overflow = TextOverflow.Ellipsis) }
}
