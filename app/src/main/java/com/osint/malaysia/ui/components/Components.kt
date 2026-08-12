/* Components — Clean Professional */
package com.osint.malaysia.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.osint.malaysia.model.*
import com.osint.malaysia.ui.theme.*
import com.osint.malaysia.util.LocalStrings

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, onSearch: () -> Unit, hint: String, enabled: Boolean = true) {
    val fm = LocalFocusManager.current
    OutlinedTextField(
        value = query, onValueChange = onQueryChange, enabled = enabled,
        placeholder = { Text(hint, style = AppTypography.Body, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)) },
        leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp)) },
        trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { onQueryChange("") }) { Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) } },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { if (query.isNotBlank()) { onSearch(); fm.clearFocus() } }),
        singleLine = true, shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outline, focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh, unfocusedContainerColor = MaterialTheme.colorScheme.surface, focusedTextColor = MaterialTheme.colorScheme.onSurface, unfocusedTextColor = MaterialTheme.colorScheme.onSurface),
        textStyle = AppTypography.Body, modifier = Modifier.fillMaxWidth().height(52.dp)
    )
}

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, enabled: Boolean = true, loading: Boolean = false, modifier: Modifier = Modifier) {
    val haptic = LocalHapticFeedback.current
    Button(onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove); onClick() }, enabled = enabled && !loading, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary, disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)), contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp), elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp), modifier = modifier.height(50.dp)) {
        if (loading) { CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp), strokeWidth = 2.5.dp); Spacer(Modifier.width(10.dp)) }
        Text(text, style = AppTypography.Subtitle.copy(fontWeight = FontWeight.SemiBold))
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)) {
        Box(Modifier.width(3.dp).height(18.dp).clip(RoundedCornerShape(2.dp)).background(MaterialTheme.colorScheme.primary))
        Spacer(Modifier.width(10.dp))
        Text(title, style = AppTypography.Subtitle.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun InfoCard(modifier: Modifier = Modifier, border: BorderStroke? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), border = border ?: BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.06f)), border = BorderStroke(0.5.dp, color.copy(alpha = 0.15f)), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), modifier = modifier) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = AppTypography.Title.copy(fontWeight = FontWeight.Bold), color = color, textAlign = TextAlign.Center)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun RiskBanner(icon: ImageVector, title: String, desc: String, color: Color) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.06f)), border = BorderStroke(1.dp, color.copy(alpha = 0.18f)), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(CircleShape).background(color.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(22.dp)) }
            Spacer(Modifier.width(14.dp))
            Column { Text(title, style = AppTypography.Title, color = color); Text(desc, style = AppTypography.Body, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, mono: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(label, style = AppTypography.Caption.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(90.dp))
        Text(value, style = if (mono) AppTypography.Mono else AppTypography.Body, color = MaterialTheme.colorScheme.onSurface, maxLines = 4, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun ErrorBanner(msg: String, onDismiss: () -> Unit) {
    Card(shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.15f)), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.ErrorOutline, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text(msg, style = AppTypography.Body, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.weight(1f)); IconButton(onClick = onDismiss, modifier = Modifier.size(20.dp)) { Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.onErrorContainer) } }
    }
}

@Composable
fun LoadingOverlay(visible: Boolean) {
    AnimatedVisibility(visible, enter = fadeIn(spring()), exit = fadeOut(tween(200))) {
        Box(Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
        Text(message, style = AppTypography.Body, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f), textAlign = TextAlign.Center)
    }
}

@Composable
fun WantedPersonCard(person: WantedPerson) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.15f)), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.GppBad, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(26.dp)); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(person.name, style = AppTypography.Subtitle.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface); if (person.age.isNotBlank()) Text(person.age, style = AppTypography.Caption, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
    }
}

@Composable
fun SocialResultRow(result: SocialResult) {
    Card(shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.CheckCircleOutline, null, tint = Colors.Green, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(10.dp)); Text(result.platform, style = AppTypography.Body.copy(fontWeight = FontWeight.Medium), color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f)); Text(result.url, style = AppTypography.Caption, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}

@Composable
fun FadeIn(visible: Boolean = true, delay: Int = 0, content: @Composable () -> Unit) {
    AnimatedVisibility(visible, enter = fadeIn(tween(400, delayMillis = delay)) + slideInVertically(tween(400, delayMillis = delay) { it / 20 })) { content() }
}
