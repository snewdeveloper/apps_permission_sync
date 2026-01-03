package com.greenlime.apps_permissions_heartbeat.utils

import android.content.Context
import androidx.core.content.edit

object Constants {
    var sharedPrefFlutterUserKey: String = "current_user_uid"
}
object AppPrefs {

    private const val PREF_NAME = "permission_heartbeat_prefs"

    private const val KEY_BASE_URL = "base_url"
    private const val KEY_ENDPOINT = "endpoint"
    private const val KEY_USER_ID = "user_id"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // -------- API CONFIG --------

    fun saveApiConfig(context: Context, baseUrl: String, endpoint: String) {
        prefs(context).edit {
            putString(KEY_BASE_URL, baseUrl)
                .putString(KEY_ENDPOINT, endpoint)
        }
    }

    fun getBaseUrl(context: Context): String? =
        prefs(context).getString(KEY_BASE_URL, null)

    fun getEndpoint(context: Context): String? =
        prefs(context).getString(KEY_ENDPOINT, null)

}