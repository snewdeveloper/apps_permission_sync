import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:apps_permissions_heartbeat/apps_permissions_heartbeat.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'Constants.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _appsPermissionsHeartbeatPlugin = AppsPermissionsHeartbeat();

  writeSharedPrefData()async{
    // Obtain shared preferences.
    final SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setString('current_user_uid', "8kLAdhvEXlgCPtDSFzLAoF6vEZl2");
  }

  setPermissionApiConfig()async{
    await AppsPermissionsHeartbeat.setPermissionApiConfig(baseUrl: Constants.permissionLogsBaseUrl,
        endpoint: Constants.permissionLogsEndPoint);
  }

  getPermissionApiConfig()async{
      var result = await  AppsPermissionsHeartbeat.getPermissionApiConfig();
      print(result);
  }

  @override
  void initState() {
    super.initState();
    initPlatformState();
    writeSharedPrefData();
    setPermissionApiConfig();
    getPermissionApiConfig();
  }

  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion =
          await _appsPermissionsHeartbeatPlugin.getPlatformVersion() ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }



  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body:
        Column(
          children: [
            // Text('Running on: $_platformVersion\n'),
            ElevatedButton(
              onPressed: AppsPermissionsHeartbeat.requestRuntimePermissions,
              child: const Text('Request Runtime Permissions'),
            ),
            ElevatedButton(
              onPressed: AppsPermissionsHeartbeat.requestOverlayPermission,
              child: const Text('Request Overlay Permission'),
            ),
            const Divider(),
            ElevatedButton(
              onPressed: AppsPermissionsHeartbeat.startOneTimePermissionSync,
              child: const Text('Start One-Time Worker'),
            ),
            ElevatedButton(
              onPressed: AppsPermissionsHeartbeat.stopOneTimePermissionSync,
              child: const Text('Stop One-Time Worker'),
            ),
            const Divider(),
            ElevatedButton(
              onPressed: AppsPermissionsHeartbeat.startHeartbeatPermissionSync,
              child: const Text('Start Periodic Worker (Check-in)'),
            ),
            ElevatedButton(
              onPressed: AppsPermissionsHeartbeat.stopHeartbeatPermissionSync,
              child: const Text('Stop Periodic Worker (Checkout)'),
            ),
          ],
        ),
      ),
    );
  }
}
