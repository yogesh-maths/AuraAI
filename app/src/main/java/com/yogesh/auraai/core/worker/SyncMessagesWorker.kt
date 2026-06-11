package com.yogesh.auraai.core.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yogesh.auraai.AuraAIApplication
class SyncMessagesWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): androidx.work.ListenableWorker.Result {
        val container = (applicationContext as AuraAIApplication).appContainer
        return when (val syncResult = container.syncRepository.syncPendingMessages()) {
            is com.yogesh.auraai.core.common.Result.Success -> androidx.work.ListenableWorker.Result.success()
            is com.yogesh.auraai.core.common.Result.Error -> {
                if (runAttemptCount < MAX_RETRIES) {
                    androidx.work.ListenableWorker.Result.retry()
                } else {
                    androidx.work.ListenableWorker.Result.failure()
                }
            }
            com.yogesh.auraai.core.common.Result.Loading -> androidx.work.ListenableWorker.Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "sync_messages"
        private const val MAX_RETRIES = 5
    }
}
