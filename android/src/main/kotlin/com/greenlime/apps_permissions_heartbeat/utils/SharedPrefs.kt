package com.greenlime.apps_permissions_heartbeat.utils

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.edit
object HeartbeatSessionStore {

    private const val PREF = "heartbeat_prefs"
    private const val KEY_DAY = "checkin_day"

    fun saveCheckInDay(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit {
                putString(KEY_DAY, today())
            }
    }

    fun getCheckInDay(context: Context): String? {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getString(KEY_DAY, null)
    }

    private fun today(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}