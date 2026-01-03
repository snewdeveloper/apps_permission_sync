package com.greenlime.apps_permissions_heartbeat.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.content.ContextCompat
object PermissionChecker {

    fun isPermissionEnabled(context: Context, permission: String): Boolean {
        return when (permission) {
            Manifest.permission.SYSTEM_ALERT_WINDOW ->
                Settings.canDrawOverlays(context)

            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.INTERNET ->
                true // These are normal permissions, automatically granted if in manifest

            else -> ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}
