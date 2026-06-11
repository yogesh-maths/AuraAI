package com.yogesh.auraai.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yogesh.auraai.domain.model.SyncStatus

@Composable
fun SyncStatusIndicator(
    status: SyncStatus,
    errorMessage: String?,
    modifier: Modifier = Modifier,
) {
    if (status == SyncStatus.SYNCED) return

    Row(
        modifier = modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val (icon, label, tint) = when (status) {
            SyncStatus.PENDING -> Triple(Icons.Default.Schedule, "Pending", MaterialTheme.colorScheme.outline)
            SyncStatus.SYNCING -> Triple(Icons.Default.Sync, "Syncing…", MaterialTheme.colorScheme.primary)
            SyncStatus.FAILED -> Triple(Icons.Default.Error, errorMessage ?: "Failed", MaterialTheme.colorScheme.error)
            SyncStatus.SYNCED -> Triple(Icons.Default.CloudOff, "", MaterialTheme.colorScheme.outline)
        }
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(14.dp),
        )
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = tint,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}
