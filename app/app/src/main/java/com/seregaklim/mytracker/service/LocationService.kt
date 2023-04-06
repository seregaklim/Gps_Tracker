package com.seregaklim.mytracker.service

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.seregaklim.mytracker.MainActivity
import com.seregaklim.mytracker.R
import com.seregaklim.mytracker.location.LocationModel
import com.seregaklim.mytracker.utils.showTost
import org.osmdroid.util.GeoPoint

@Suppress("DEPRECATION")
class LocationService : Service() {
    private var distance =0.0f
    private lateinit var geoPointsList: ArrayList<GeoPoint>
    //получаем сведения о месте нахождения
    private lateinit var locProvider:FusedLocationProviderClient
    private lateinit var locRequest:LocationRequest
    private var lastLocation: Location?=null

    ///соединяет с активити (можно передать данные с сервиса)
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startNotification()
        //получение сведений о местоположении
        startLocationUpdates()
        //сераис запущен
        isRunning = true

        //перезапускает сервис
        return START_STICKY
    }


    override fun onCreate() {
        super.onCreate()
        geoPointsList = ArrayList()
        initLocation()
    }


    override fun onDestroy() {
        super.onDestroy()
        //закрываем
        isRunning = false
        locProvider.removeLocationUpdates(locCallback)
    }

    //последнее местонахождение смартфона c с координатами
    private val locCallback = object : LocationCallback() {
        override fun onLocationResult(lResult: LocationResult) {
            super.onLocationResult(lResult)
            //текущая точка
            val curentLocation = lResult.lastLocation
            //измеряем дистанцию
            if (lastLocation != null && curentLocation != null) {
                //если скорость больше 0.2 м/с
                if (curentLocation.speed > 0.2)
                //дистанция
                    distance += lastLocation?.distanceTo(curentLocation)!!
                geoPointsList.add(GeoPoint(curentLocation.latitude, curentLocation.longitude))

                val locModel = LocationModel(
                    velocity = curentLocation.speed,
                    distance = distance,
                    geoPointsList
                )
            sendLocData(locModel)
            }
            //записываем точку
            lastLocation = curentLocation
          //  Log.d("MyLog", "Location: ${distance}")
            //последнее местонахождение смартфона c с координатами
            // lResult.lastLocationю
        }
    }
  //отпавряем данные
  private fun sendLocData(locModel: LocationModel) {
      val i = Intent(LOC_MODEL_INTENT)
      i.putExtra(LOC_MODEL_INTENT, locModel)
      LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(i)
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
    //доступ к местонахождению
    private fun initLocation(){
        locRequest=LocationRequest.create()
         //интервал обновления
        locRequest.interval=5000
        locRequest.fastestInterval=5000
        //приоритет,высокая точность
        locRequest.priority=PRIORITY_HIGH_ACCURACY
        locProvider= LocationServices.getFusedLocationProviderClient(baseContext)

    }

    //передаем интеревал,
    private fun startLocationUpdates() {
        //разрешение
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        locProvider.requestLocationUpdates(
            locRequest,
            locCallback,
            //когда исполнились все команды, поток перестает существовать, используем Looper, который постоянно запускает
            Looper.myLooper()
        )

    }


    companion object {
        const val LOC_MODEL_INTENT= "loc_intent"
        const val CHANNEL_ID = "chanel_1"
       //сервис запущен
        var isRunning =false
       //cохраняет время, после закрытия приложения
        var startTime=0L
    }

}











