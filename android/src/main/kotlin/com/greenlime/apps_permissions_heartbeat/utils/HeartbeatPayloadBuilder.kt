package com.greenlime.apps_permissions_heartbeat.utils


import android.Manifest
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Build
import org.json.JSONArray

import org.json.JSONObject

object HeartbeatPayloadBuilder {

    fun build(context: Context): JSONObject {
        val prefs = context.getSharedPreferences("FlutterSharedPreferences", MODE_PRIVATE)
        val uid = prefs.getString("flutter.${Constants.sharedPrefFlutterUserKey}", null)

        val payload = JSONObject()

        // 🔹 User info (replace with real user source later)
        payload.put("userId", "$uid")
        payload.put("timestamp", System.currentTimeMillis())

        // 🔹 Device info
        payload.put(
            "device",
            JSONObject().apply {
                put("sdk", Build.VERSION.SDK_INT)
                put("manufacturer", Build.MANUFACTURER)
                put("model", Build.MODEL)
            }
        )

        // 🔹 Permissions
        val permissionsArray = JSONArray()

        PermissionCatalog.trackedPermissions.forEach { permission ->
            permissionsArray.put(
                JSONObject().apply {
                    put(
                        "name",
                        permissionName(permission)
                    )
                    put(
                        "enabled",
                        PermissionChecker.isPermissionEnabled(context, permission)
                    )
                }
            )
        }

        payload.put("permissions", permissionsArray)

        return payload
    }

    /**
     * Converts Android permission constants to
     * clean backend-friendly names
     */
    private fun permissionName(permission: String): String {
        return when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION -> "FINE_LOCATION"
            Manifest.permission.ACCESS_COARSE_LOCATION -> "COARSE_LOCATION"
            Manifest.permission.READ_PHONE_STATE -> "PHONE_STATE"
            Manifest.permission.READ_CALL_LOG -> "CALL_LOG"
            Manifest.permission.CAMERA -> "CAMERA"
            Manifest.permission.SYSTEM_ALERT_WINDOW -> "OVERLAY"
            Manifest.permission.ACCESS_NETWORK_STATE -> "ACCESS_NETWORK_STATE"
            Manifest.permission.WAKE_LOCK -> "WAKE_LOCK"
            Manifest.permission.INTERNET -> "INTERNET"
            Manifest.permission.FOREGROUND_SERVICE -> "FOREGROUND_SERVICE"
            Manifest.permission.FOREGROUND_SERVICE_PHONE_CALL ->
                "FOREGROUND_SERVICE_PHONE_CALL"
            else -> permission.substringAfterLast(".").uppercase()
        }
    }
}