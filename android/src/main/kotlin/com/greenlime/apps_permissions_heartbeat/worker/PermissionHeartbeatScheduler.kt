package com.greenlime.apps_permissions_heartbeat.worker


import android.content.Context
import android.util.Log
import androidx.work.*
import com.greenlime.apps_permissions_heartbeat.utils.HeartbeatSessionStore
import java.util.concurrent.TimeUnit

object PermissionHeartbeatScheduler {

    private const val HEARTBEAT_PERIODIC = "heartbeat_periodic"
    private const val ONE_TIME_WORKER = "one_time_worker"

    fun startPeriodic(context: Context) {
        HeartbeatSessionStore.saveCheckInDay(context)

        val request = PeriodicWorkRequestBuilder<PermissionHeartbeatWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30,
                TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            HEARTBEAT_PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
        Log.d("PermissionHeartbeatScheduler", "Periodic time worker started")

    }

    fun startOneTime(context: Context) {
        val request = OneTimeWorkRequestBuilder<PermissionHeartbeatWorker>()
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30,
                TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            ONE_TIME_WORKER,
            ExistingWorkPolicy.REPLACE,
            request
        )
        Log.d("PermissionHeartbeatScheduler", "One time worker started")

    }

    fun stopHeartBeatWorker(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(HEARTBEAT_PERIODIC)
        Log.d("PermissionHeartbeatScheduler", "Heartbeat stopped")
    }

    fun stopOneTimeWorker(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(ONE_TIME_WORKER)
        Log.d("PermissionHeartbeatScheduler", "One time worker stopped")
    }
}