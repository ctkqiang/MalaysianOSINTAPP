package com.osint.malaysia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.osint.malaysia.ui.components.*
import com.osint.malaysia.ui.theme.*
import com.osint.malaysia.util.LocalStrings
import com.osint.malaysia.viewmodel.MainViewModel

@Composable
fun CompanyScreen(vm: MainViewModel) {
    var ssm by remember { mutableStateOf("") }; var kw by remember { mutableStateOf("") }; var tab by remember { mutableIntStateOf(0) }
    val loading by vm.isLoading.collectAsState(); val ssmR by vm.ssmResult.collectAsState(); val coR by vm.companyResult.collectAsState(); val err by vm.errorMessage.collectAsState()
    val s = LocalStrings.current

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { FilterChip(tab == 0, { tab = 0 }, { Text(s.coSsmtab) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)); FilterChip(tab == 1, { tab = 1 }, { Text(s.coYptab) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer)) } }

        if (tab == 0) {
            item { SectionHeader(s.coSsmTitle) }
            item { SearchBar(ssm, { ssm = it.filter { c -> c.isDigit() || c == '-' }.take(15) }, { vm.parseSSM(ssm) }, s.coSsmHint, !loading) }
            item { PrimaryButton(s.coSsmBtn, { vm.parseSSM(ssm) }, modifier = Modifier.fillMaxWidth()) }
            ssmR?.let { item { SectionHeader(s.coSsmResult) }; item { FadeIn { InfoCard { InfoRow(s.lbRegNo, it.registrationNumber, mono = true); InfoRow(s.lbEntityCode, it.entityCode, mono = true); InfoRow(s.lbEntityType, it.entityTypeCn) } } } }
        } else {
            item { SectionHeader(s.coYpTitle) }
            item { SearchBar(kw, { kw = it }, { vm.queryCompany(kw) }, s.coYpHint, !loading) }
            item { PrimaryButton(s.coYpBtn, { if (kw.isNotBlank()) vm.queryCompany(kw) }, loading = loading, modifier = Modifier.fillMaxWidth()) }
            coR?.let { item { SectionHeader(s.coInfo) }; item { FadeIn { InfoCard { InfoRow(s.lbCompany, it.name); if (it.category.isNotEmpty()) InfoRow(s.lbCategory, it.category); if (it.address.isNotEmpty()) InfoRow(s.lbAddress, it.address); if (it.website.isNotEmpty()) InfoRow(s.lbWebsite, it.website); InfoRow(s.lbSource, it.source) } } } }
            if (coR == null && !loading && kw.isNotBlank()) item { EmptyState(s.coNotFound) }
        }
        err?.let { item { ErrorBanner(it) { vm.clearError() } } }
        if (loading) item { LoadingOverlay(true) }
    }
}
