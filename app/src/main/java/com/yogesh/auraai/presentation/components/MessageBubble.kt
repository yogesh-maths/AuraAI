package com.yogesh.auraai.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.yogesh.auraai.domain.model.Message
import com.yogesh.auraai.domain.model.MessageRole
import com.yogesh.auraai.domain.model.SyncStatus
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.yogesh.auraai.presentation.home.AuraLogo

@Composable
fun MessageBubble(
    message: Message,
    onRetry: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isUser = message.role == MessageRole.USER
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    if (isUser) {
                        Color(0xFF5049F5) // Purple
                    } else {
                        Color(0xFF101827)
                    }
                )
                .then(
                    if (message.syncStatus == SyncStatus.FAILED && isUser) {
                        Modifier.clickable { onRetry(message.id) }
                    } else {
                        Modifier
                    },
                )
                .padding(12.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
        ) {
            if (!isUser) {
                Text(
                    text = "Aura",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                            color = Color(0xFF22D3EE),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            if (!isUser) {
//                AuraLogo(
//                    modifier = Modifier.size(8.dp)
//                )
            }
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isUser) {
                    Color(0xFFF8FAFC)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
            if (isUser) {
                SyncStatusIndicator(
                    status = message.syncStatus,
                    errorMessage = message.errorMessage,
                )
                if (message.syncStatus == SyncStatus.FAILED) {
                    Text(
                        text = "Tap to retry",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }
    }
}
