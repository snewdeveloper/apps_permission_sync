package com.greenlime.apps_permissions_heartbeat.network


import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker.Result
import com.greenlime.apps_permissions_heartbeat.utils.AppPrefs.getBaseUrl
import com.greenlime.apps_permissions_heartbeat.utils.AppPrefs.getEndpoint
import com.greenlime.apps_permissions_heartbeat.utils.HeartbeatPayloadBuilder
import org.json.JSONException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL


object HeartbeatSender {

    private const val TAG = "PermissionHeartbeat"
//    private const val API_URL =
//        "https://client-glfunnel-service-144135692330.asia-south1.run.app/api/v1/mobile/workmanager/permissionLogs"

    fun send(context: Context): Result {
        var connection: HttpURLConnection? = null
        val startTime = System.currentTimeMillis()
        var responseBody: String? = null
        var errorBody: String? = null
  val API_URL = "${getBaseUrl(context)}${getEndpoint(context)}"
        return try {
            connection = (URL(API_URL).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                connectTimeout = 15_000
                readTimeout = 15_000
                doOutput = true
            }

            val payload = HeartbeatPayloadBuilder.build(context)
            Log.d(TAG, "Payload: $payload")

            connection.outputStream.use {
                it.write(payload.toString().toByteArray())
            }
            val responseCode = connection.responseCode
            val duration = System.currentTimeMillis() - startTime

            // Read success response
            responseBody = if (responseCode in 200..299) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                null
            }

            // Read error response
            errorBody = if (responseCode !in 200..299) {
                connection.errorStream?.bufferedReader()?.use { it.readText() }
            } else {
                null
            }

            // 🔍 Detailed logging
            Log.d(
                "PermissionHeartbeatWorker",
                """
        Heartbeat response:
        ├─ code: $responseCode
        ├─ duration: ${duration}ms
        ├─ headers: ${connection.headerFields}
        ├─ body: $responseBody
        ├─ error: $errorBody
        """.trimIndent()
            )
            when (connection.responseCode) {
                in 200..299 -> Result.success()
                in 400..499 -> Result.failure()
                else -> Result.retry()
            }

        } catch (_: SocketTimeoutException) {
            Result.retry()
        } catch (_: IOException) {
            Result.retry()
        } catch (_: JSONException) {
            Result.failure()
        } finally {
            connection?.disconnect()
        }
    }
}