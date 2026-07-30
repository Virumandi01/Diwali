package com.example.eagle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*


class MainActivity : ComponentActivity() {

    // ⚠️ Replace with your Mac Mini's local IP address (e.g., "192.168.1.50")
    private val macMiniIp = "192.168.1.XX"
    private lateinit var syncClient: VitalSyncClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize and connect WebSocket
        syncClient = VitalSyncClient(macMiniIp)
        syncClient.connect()

        setContent {
            var currentHeartRate by remember { mutableStateOf(72) }

            MaterialTheme {
                EDashScreen(
                    heartRate = currentHeartRate,
                    isConnected = true,
                    onSosClicked = {
                        // Test sending a high heart rate alert payload
                        syncClient.sendHeartRate(135)
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        syncClient.disconnect()
    }
}