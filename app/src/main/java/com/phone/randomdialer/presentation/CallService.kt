package com.phone.randomdialer.presentation

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CallService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d("CallService===>", "Service started")

        startForeground(NOTIFICATION_ID, createNotification())
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val telephonyCallback = getTelephonyCallback {
                Log.d("CallService===>", "Phone call detected")
                callToNumber("7727860686")
            }
            telephonyManager.registerTelephonyCallback(
                mainExecutor,
                telephonyCallback
            )
        } else {
            val phoneStateListener = getPhoneStateListener {
                Log.d("CallService===>", "Phone call detected")
                callToNumber("7727860686")
            }
            telephonyManager.listen(
                phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE
            )
        }
        callToNumber("7727860686")
        return START_STICKY
    }

    fun callToNumber(number: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$number")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        // Start the activity with the dialer
        ContextCompat.startActivity(this, intent, null)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun getTelephonyCallback(
        onCallStateChanged: () -> Unit,
    ): TelephonyCallback {
        return object : TelephonyCallback(), TelephonyCallback.CallStateListener {
            override fun onCallStateChanged(state: Int) {
                if (state == TelephonyManager.CALL_STATE_IDLE) {
                    onCallStateChanged()
                }
            }
        }
    }

    fun getPhoneStateListener(
        onCallStateChanged: () -> Unit
    ): PhoneStateListener {
        return object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String?) {
                super.onCallStateChanged(state, incomingNumber)
                if (state == TelephonyManager.CALL_STATE_IDLE) {
                    onCallStateChanged()
                }
            }
        }
    }

    private fun createNotification(): Notification {
        createNotificationChannel()

        val notificationIntent = Intent(this, CallService::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Phone Call in Progress")
            .setContentText("You are currently on a phone call.")
            .setSmallIcon(androidx.loader.R.drawable.notification_bg)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        return notification
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "ForegroundServiceChannel"
        private const val NOTIFICATION_ID = 12345
    }

    override fun onDestroy() {

        Log.d("CallService===>", "Stop service")
        // Stop the phone call and dismiss the notification
        stopForeground(true)
        super.onDestroy()
    }

    private fun startPhoneCall() {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:123456789") // Replace with the desired phone number
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        ContextCompat.startActivity(this, intent, null)
    }


}