package com.seregaklim.mytracker.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.seregaklim.mytracker.MainActivity
import com.seregaklim.mytracker.R

class LocationService : Service() {
    ///соединяет с активити (можно передать данные с сервиса)
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification()
        isRunning=true
        //перезапускает сервис
        return START_STICKY
    }


    override fun onCreate() {
        super.onCreate()
    }


    override fun onDestroy() {
        super.onDestroy()
      isRunning=false
    }

    private fun startNotification() {
        //   если ниже 8 версии
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nChanel = NotificationChannel(
                CHANNEL_ID,
                "Location Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val nManager = getSystemService(NotificationManager::class.java) as NotificationManager
            nManager.createNotificationChannel(nChanel)
        }

        val nIntent = Intent(this, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(
            this,
            1000,
            nIntent,
            // 0
           PendingIntent.FLAG_IMMUTABLE

        )


        val notification = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        ).setSmallIcon(R.mipmap.ic_launcher)
            //сообщение
            .setContentTitle("Tracker Running!")
            .setContentIntent(pIntent).build()

        startForeground(1000, notification)
    }

    companion object {
        const val CHANNEL_ID = "chanel_1"
       //сервис запущен
        var isRunning =false
       //cохраняет время, после закрытия приложения
        var startTime=0L
    }

}











