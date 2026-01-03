package com.greenlime.apps_permissions_heartbeat.worker

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.greenlime.apps_permissions_heartbeat.network.HeartbeatSender
import com.greenlime.apps_permissions_heartbeat.utils.HeartbeatSessionStore
import com.greenlime.apps_permissions_heartbeat.worker.PermissionHeartbeatScheduler.stopHeartBeatWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PermissionHeartbeatWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun doWork(): Result {

        val checkInDay = HeartbeatSessionStore.getCheckInDay(applicationContext)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

        // 🛑 HARD STOP if day changed
        if (checkInDay == null || checkInDay != today) {
            Log.d(
                "PermissionHeartbeatWorker",
                "Session expired. checkInDay=$checkInDay today=$today"
            )
            stopHeartBeatWorker(applicationContext)
            return Result.success()
        }


        HeartbeatSender.send(applicationContext)
        return Result.success()
    }
}