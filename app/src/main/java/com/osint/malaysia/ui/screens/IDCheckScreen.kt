package com.osint.malaysia.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.osint.malaysia.model.*
import com.osint.malaysia.ui.components.*
import com.osint.malaysia.ui.theme.*
import com.osint.malaysia.util.LocalStrings
import com.osint.malaysia.viewmodel.MainViewModel

@Composable
fun IDCheckScreen(vm: MainViewModel) {
    var ic by remember { mutableStateOf("") }; val loading by vm.isLoading.collectAsState(); val res by vm.idCheckResult.collectAsState(); val err by vm.errorMessage.collectAsState()
    val s = LocalStrings.current

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { SectionHeader(s.idTitle) }
        item { SearchBar(ic, { ic = it.filter { c -> c.isDigit() || c == '-' }.take(14) }, { vm.queryIDComprehensive(ic) }, s.idHint, !loading) }
        item { PrimaryButton(s.idBtn, { if (ic.isNotBlank()) vm.queryIDComprehensive(ic) }, loading = loading, modifier = Modifier.fillMaxWidth()) }
        err?.let { item { ErrorBanner(it) { vm.clearError() } } }
        if (loading) item { LoadingOverlay(true) }

        res?.myKad?.let {
            item { SectionHeader(s.idMykad) }
            item { FadeIn { InfoCard(border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))) { InfoRow(s.lbBirthday, it.birthday); InfoRow(s.lbProvince, "${it.provinceCn} (${it.province})"); InfoRow(s.lbId, it.identifier, mono = true) } } }
        } ?: res?.let { item { EmptyState(s.idMykadFail) } }

        res?.sspi?.let {
            item { SectionHeader(s.idSspi) }
            item { FadeIn { InfoCard { Row(verticalAlignment = Alignment.CenterVertically) { Icon(if (it.statusCode.contains("TIDAK", true)) Icons.Default.Cancel else Icons.Default.CheckCircle, null, tint = if (it.statusCode.contains("TIDAK", true)) Colors.Red else Colors.Green, modifier = Modifier.size(22.dp)); Spacer(Modifier.width(10.dp)); Text(it.statusCode, style = AppTypography.Body, color = MaterialTheme.colorScheme.onSurface) } } } }
        }

        if (res?.wanted?.isNotEmpty() == true) { item { SectionHeader(s.idWanted) }; items(res!!.wanted) { WantedPersonCard(it) } }
        if (res?.sprmCases?.isNotEmpty() == true) { item { SectionHeader(s.idSprm) }; items(res!!.sprmCases) { c -> SPRMCaseCard(c, s) } }
    }
}

@Composable private fun SPRMCaseCard(case: SPRMCase, s: com.osint.malaysia.util.UiStrings) {
    InfoCard(border = BorderStroke(0.5.dp, Colors.Orange.copy(alpha = 0.2f))) {
        Text(case.name, style = AppTypography.Subtitle, color = MaterialTheme.colorScheme.onBackground); Spacer(Modifier.height(4.dp))
        InfoRow(s.lbId, case.ic, mono = true); InfoRow(s.lbCaseNo, case.caseNo, mono = true)
        if (case.charge.isNotEmpty()) InfoRow(s.lbCharge, case.charge)
        if (case.sentence.isNotEmpty()) InfoRow(s.lbSentence, case.sentence)
        if (case.law.isNotEmpty()) InfoRow(s.lbLaw, case.law)
    }
}
