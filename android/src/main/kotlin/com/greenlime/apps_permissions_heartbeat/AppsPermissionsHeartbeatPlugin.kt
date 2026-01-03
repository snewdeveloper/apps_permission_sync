package com.greenlime.apps_permissions_heartbeat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import com.greenlime.apps_permissions_heartbeat.worker.PermissionHeartbeatScheduler
import androidx.core.net.toUri
import com.greenlime.apps_permissions_heartbeat.utils.AppPrefs

/** AppsPermissionsHeartbeatPlugin */
class AppsPermissionsHeartbeatPlugin :
    FlutterPlugin,
    ActivityAware,
    MethodCallHandler {
    // The MethodChannel that will the communication between Flutter and native Android
    //
    // This local reference serves to register the plugin with the Flutter Engine and unregister it
    // when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private  var activity: Activity? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "permission_heartbeat")
        context = flutterPluginBinding.applicationContext
//        activity = flutterPluginBinding.
        channel.setMethodCallHandler(this)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    fun requestRuntimePermissions(){
//         val permissionLauncher =
//            registerForActivityResult(
//                ActivityResultContracts.RequestMultiplePermissions()
//            ) {}
        try {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            ).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            activity?.startActivity(intent)
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to open app info", e)
        }
    }
    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {

            "getPlatformVersion" ->{
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "setConfig" -> {
                val baseUrl = call.argument<String>("baseUrl")
                val endpoint = call.argument<String>("endPoint")

                if (baseUrl.isNullOrBlank() || endpoint.isNullOrBlank()) {
                    result.error("INVALID_ARGS", "Base URL or endpoint missing", null)
                    return
                }
                AppPrefs.saveApiConfig(context, baseUrl.toString(), endpoint.toString())
                result.success(null)
            }

            "getConfig" ->{
                val baseUrl = AppPrefs.getBaseUrl(context)
                val endpoint = AppPrefs.getEndpoint(context)
                Log.d("MainActivity","--Baseurl --$baseUrl--endpoint--$endpoint")
                result.success(mapOf("baseUrl" to baseUrl, "endpoint" to endpoint))
            }

            "requestRuntimePermissions" -> {
                requestRuntimePermissions()
                result.success(null)
            }

            "requestOverlayPermission" -> {
                if (!Settings.canDrawOverlays(context)) {
                    activity?.startActivity(
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            "package:${context.packageName}".toUri()
                        )
                    )
                }
                result.success(null)
            }

            "startOneTimeWorker" -> {
                PermissionHeartbeatScheduler.startOneTime(context)
                result.success(null)
            }

            "stopOneTimeWorker" -> {
                PermissionHeartbeatScheduler.stopOneTimeWorker(context)
                result.success(null)
            }

            "startPeriodicWorker" -> {
                PermissionHeartbeatScheduler.startPeriodic(context)
                result.success(null)
            }

            "stopPeriodicWorker" -> {
                PermissionHeartbeatScheduler.stopHeartBeatWorker(context)
                result.success(null)
            }

            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
