package com.yogesh.auraai.data.repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.yogesh.auraai.core.worker.SyncMessagesWorker

class WorkManagerSyncScheduler(
    context: Context,
) : SyncScheduler {

    private val workManager = WorkManager.getInstance(context)

    override fun enqueueSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SyncMessagesWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            SyncMessagesWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }
}
