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
import com.osint.malaysia.ui.components.*
import com.osint.malaysia.ui.theme.*
import com.osint.malaysia.util.LocalStrings
import com.osint.malaysia.viewmodel.MainViewModel

@Composable
fun SocialScreen(vm: MainViewModel) {
    var un by remember { mutableStateOf("") }
    val searching by vm.isSocialSearching.collectAsState(); val results by vm.socialResults.collectAsState()
    val s = LocalStrings.current

    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { SectionHeader(s.soTitle) }
        item { SearchBar(un, { un = it.trim().take(50) }, { if (un.isNotBlank()) vm.searchSocial(un) }, s.soHint, !searching) }
        item { PrimaryButton(s.soBtn, { if (un.isNotBlank()) vm.searchSocial(un) }, loading = searching, modifier = Modifier.fillMaxWidth()) }

        if (searching) item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp), strokeWidth = 2.dp); Spacer(Modifier.width(10.dp)); Text(s.soSearching(results.size), style = AppTypography.Body, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
        if (!searching && un.isNotBlank()) item { SectionHeader(if (results.isEmpty()) s.soNone else s.soFound(results.size)) }
        if (results.isNotEmpty()) items(results) { SocialResultRow(it) }
        if (!searching && un.isNotBlank() && results.isEmpty()) item { EmptyState(s.soEmpty) }

        if (!searching && un.isBlank()) item { InfoCard(border = BorderStroke(0.5.dp, Colors.Secondary.copy(alpha = 0.2f))) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(10.dp)); Text(s.soGuide, style = AppTypography.Subtitle, color = MaterialTheme.colorScheme.onBackground) }; Spacer(Modifier.height(8.dp)); Text(s.soGuide1, style = AppTypography.Body, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(s.soGuide2, style = AppTypography.Body, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(s.soGuide3, style = AppTypography.Body, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
    }
}
