
import 'package:flutter/services.dart';

class AppsPermissionsHeartbeat {
  static const MethodChannel _channel =
  MethodChannel('permission_heartbeat');

  Future<String?> getPlatformVersion() async {
    return "AppsPermissionsHeartbeatPlatform.instance.getPlatformVersion()";
  }


  static Future<void> requestRuntimePermissions() =>
      _channel.invokeMethod('requestRuntimePermissions');


  static Future<void> setPermissionApiConfig({
    required String baseUrl,
    required String endpoint,
  }) async {
    await _channel.invokeMethod('setConfig', {
      'endPoint': endpoint,
      'baseUrl': baseUrl,
    });
  }

  static Future<Map<String, dynamic>> getPermissionApiConfig() async {
    final result = await _channel.invokeMethod('getConfig');
    return Map<String, dynamic>.from(result);
  }

  static Future<void> requestOverlayPermission() =>
      _channel.invokeMethod('requestOverlayPermission');

  static Future<void> startOneTimePermissionSync() =>
      _channel.invokeMethod('startOneTimeWorker');

  static Future<void> stopOneTimePermissionSync() =>
      _channel.invokeMethod('stopOneTimeWorker');

  static Future<void> startHeartbeatPermissionSync() =>
      _channel.invokeMethod('startPeriodicWorker');

  static Future<void> stopHeartbeatPermissionSync() =>
      _channel.invokeMethod('stopPeriodicWorker');
}
