/* 马来西亚OSINT — 可复用UI组件 */

package com.osint.malaysia.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.osint.malaysia.model.*
import com.osint.malaysia.ui.theme.Accent
import com.osint.malaysia.ui.theme.AppTypography
import com.osint.malaysia.ui.theme.NavyBlue

/* ===== 搜索栏 ===== */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    placeholder: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        enabled = enabled,
        placeholder = {
            Text(
                text = placeholder,
                style = AppTypography.Body,
                color = NavyBlue.N300
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "搜索",
                tint = NavyBlue.N400
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "清除",
                        tint = NavyBlue.N400
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            if (query.isNotBlank()) {
                onSearch()
                focusManager.clearFocus()
            }
        }),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NavyBlue.N400,
            unfocusedBorderColor = NavyBlue.N600,
            focusedContainerColor = NavyBlue.N900,
            unfocusedContainerColor = NavyBlue.N900,
            cursorColor = NavyBlue.N300,
            focusedTextColor = NavyBlue.N50,
            unfocusedTextColor = NavyBlue.N100
        ),
        textStyle = AppTypography.Mono,
        modifier = modifier.fillMaxWidth()
    )
}

/* ===== 主操作按钮 ===== */
@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = NavyBlue.N600,
            contentColor = NavyBlue.N50,
            disabledContainerColor = NavyBlue.N800,
            disabledContentColor = NavyBlue.N500
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
        modifier = modifier.height(48.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = NavyBlue.N300,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = if (isLoading) "查询中…" else text,
            style = AppTypography.Subtitle
        )
    }
}

/* ===== 结果卡片 ===== */
@Composable
fun ResultCard(
    title: String,
    subtitle: String = "",
    icon: @Composable () -> Unit = {
        Icon(Icons.Default.Info, contentDescription = null, tint = NavyBlue.N400)
    },
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavyBlue.N900
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBlue.N700),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(NavyBlue.N800),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = AppTypography.Subtitle,
                    color = NavyBlue.N50
                )
                if (subtitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = AppTypography.Body,
                        color = NavyBlue.N300
                    )
                }
            }
        }
    }
}

/* ===== 通缉人员卡片 ===== */
@Composable
fun WantedPersonCard(person: WantedPerson) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = NavyBlue.N900
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Accent.Red.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = Accent.Red,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(person.name, style = AppTypography.Subtitle, color = NavyBlue.N50)
                Text("年龄: ${person.age}", style = AppTypography.Caption, color = NavyBlue.N300)
            }
        }
    }
}

/* ===== 加载指示器 ===== */
@Composable
fun LoadingOverlay(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    color = NavyBlue.N400,
                    modifier = Modifier.size(40.dp),
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "查询中…",
                    style = AppTypography.Caption,
                    color = NavyBlue.N300
                )
            }
        }
    }
}

/* ===== 错误提示条 ===== */
@Composable
fun ErrorBanner(message: String, onDismiss: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Accent.Red.copy(alpha = 0.15f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, Accent.Red.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = Accent.Red,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                style = AppTypography.Caption,
                color = Accent.Red,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDismiss, modifier = Modifier.size(20.dp)) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "关闭",
                    tint = Accent.Red
                )
            }
        }
    }
}

/* ===== 信息区块标题 ===== */
@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = AppTypography.Subtitle,
        color = NavyBlue.N200,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

/* ===== 键值对信息行 ===== */
@Composable
fun InfoRow(label: String, value: String, isMono: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label: ",
            style = AppTypography.Caption,
            color = NavyBlue.N400
        )
        Text(
            text = value,
            style = if (isMono) AppTypography.Mono else AppTypography.Body,
            color = NavyBlue.N100
        )
    }
}

/* ===== 社交媒体搜索结果行 ===== */
@Composable
fun SocialResultRow(result: SocialResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Accent.Green,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = result.platform,
            style = AppTypography.Body,
            color = NavyBlue.N100,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = result.url,
            style = AppTypography.Mono,
            color = NavyBlue.N400,
            maxLines = 1
        )
    }
}

/* ===== 空状态 ===== */
@Composable
fun EmptyState(message: String = "暂无数据") {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.SearchOff,
                contentDescription = null,
                tint = NavyBlue.N600,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = AppTypography.Body,
                color = NavyBlue.N500,
                textAlign = TextAlign.Center
            )
        }
    }
}
