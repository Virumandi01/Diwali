package com.example.eagle
import android.util.Log
import okhttp3.*
import org.json.JSONObject

class VitalSyncClient(private val serverIp: String) {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var isConnected = false

    fun connect() {
        // Points to FastAPI WebSocket endpoint on your Mac Mini
        val request = Request.Builder()
            .url("ws://$serverIp:8000/api/v1/ws/vitals")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                isConnected = true
                Log.d("VitalSync", "Connected to Mac Mini Server!")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("VitalSync", "Received from server: $text")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = false
                Log.d("VitalSync", "Connection closed: $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                Log.e("VitalSync", "WebSocket Error: ${t.message}")
            }
        })
    }

    fun sendHeartRate(bpm: Int) {
        if (!isConnected || webSocket == null) return

        // Create structured JSON payload
        val json = JSONObject().apply {
            put("device_id", "galaxy_watch_4")
            put("vital_type", "heart_rate")
            put("value", bpm)
            put("timestamp", System.currentTimeMillis())
        }

        webSocket?.send(json.toString())
    }

    fun disconnect() {
        webSocket?.close(1000, "App closed")
    }
}