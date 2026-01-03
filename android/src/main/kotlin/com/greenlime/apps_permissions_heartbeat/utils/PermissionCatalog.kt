package com.greenlime.apps_permissions_heartbeat.utils

import android.Manifest
import android.os.Build

object PermissionCatalog {

    /** Permissions that require runtime request */
    val runtimePermissions = listOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_CALL_LOG,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    /** Permissions that are tracked in heartbeat */
    val trackedPermissions: List<String> = buildList {
        addAll(
            listOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.INTERNET,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.FOREGROUND_SERVICE
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            add(Manifest.permission.FOREGROUND_SERVICE_PHONE_CALL)
        }
    }
}