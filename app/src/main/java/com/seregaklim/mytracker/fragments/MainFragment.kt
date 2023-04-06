package com.seregaklim.mytracker.fragments


import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.seregaklim.mytracker.R
import com.seregaklim.mytracker.databinding.FragmentMainBinding
import com.seregaklim.mytracker.service.LocationService
import com.seregaklim.mytracker.utils.DialogManager
import com.seregaklim.mytracker.utils.TimeUtils
import com.seregaklim.mytracker.utils.сhekPermisson
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.*


class MainFragment() : Fragment() {
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var binding: FragmentMainBinding
    private var isServiceRunning=false
    private var timer: Timer?=null
    private var startTime =0L
   //следит за циклом жизни фрагмента
    private val timeData = MutableLiveData<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ///!!загружать settingOsm()первым!!
        settingOsm()
        binding = FragmentMainBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment


        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //запускаем первым
        registerPermission()
        setOnClicks()
        checkLockPermisson()
        chekServiceState()
        updateTime()
    }

    //проверка запуска сервича
    private fun startStopService() {
        if (!isServiceRunning) {
            startLocService()
        } else {
            activity?.stopService(Intent(activity, LocationService::class.java))
            binding.bStartStop.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            //останавливаем таймер
            timer?.cancel()
        }
        isServiceRunning = !isServiceRunning
    }

    override fun onResume() {
        super.onResume()
        //запускать проверку в  onResume()
      // checkLockPermisson()

    }

    private fun setOnClicks()= with(binding){
     val listener=onClicks()
     bStartStop.setOnClickListener(listener)
    }

    private fun onClicks(): View.OnClickListener {
        return View.OnClickListener {
           when(it.id) {

               R.id.b_start_stop -> { startStopService() }
           }

        }

    }

    private fun updateTime() {
        timeData.observe(viewLifecycleOwner) {
            binding.tvTime.text=it

        }

    }


    private fun startTimer() {
        //если таймер работаем, останавливаем
        timer?.cancel()
        timer = Timer()
         //c какого времени закрыли сервис
        startTime = LocationService.startTime
        //запускаем
        timer?.schedule(object : TimerTask() {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun run() {
                //запускаем на основном потоке
                activity?.runOnUiThread {
                 timeData.value=getCurrentTime()
                }
            }
        }, 1, 1)
    }

@RequiresApi(Build.VERSION_CODES.N)
private fun getCurrentTime():String{
//вычитаем аремя ,когда начали отсчет
    return TimeUtils.getTime(System.currentTimeMillis() - startTime)
}

    //запускаем сервис
    private fun startLocService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(Intent(activity, LocationService::class.java))
        } else {
            activity?.startService(Intent(activity, LocationService::class.java))

        }
        binding.bStartStop.setImageResource(R.drawable.ic_baseline_stop_24)
       //записываем настоящее время
        LocationService.startTime=System.currentTimeMillis()
        //включаем таймер
        startTimer()
    }

    private fun chekServiceState() {
        isServiceRunning = LocationService.isRunning
        //значит серевис работает
        if (isServiceRunning) {
            binding.bStartStop.setImageResource(R.drawable.ic_baseline_stop_24)
            startTimer()
        }
    }
    //map
    private fun settingOsm() {
        Configuration.getInstance().load(
            activity as AppCompatActivity,
            //класс для записи и считывния из памяти (только наше приложение будет иметь доступ к данным которые будут сохраняться в приложение Context.MODE_PRIVATE)
            activity?.getSharedPreferences("osm_pref", Context.MODE_PRIVATE)
        )

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    }

    //маркер
    private fun initOSM() = with(binding) {
        map.controller.setZoom(20.0)
        //map.controller.animateTo(GeoPoint(40.4167, -3.70327))

        //gps
        val mayLocProVaider = GpsMyLocationProvider(activity)
        //наложение нашего местонахождения на карте
        val mayLocOverlay = MyLocationNewOverlay(mayLocProVaider, map)
        //включаем нахождение нашего устройства
        mayLocOverlay.enableMyLocation()
        //включаем движение карты за устройством
        mayLocOverlay.enableFollowLocation()
        //как только местонахождение устройства нашли
        mayLocOverlay.runOnFirstFix {
            // если что то есть на карте очищаем (при открытии экрана)
            map.overlays.clear()

            //добавляем слой на карту
            map.overlays.add(mayLocOverlay)
        }
    }

    // разрешение пользователя
    private fun registerPermission() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                initOSM()
                checkLocationEnabled()
            }
  //   showTost("${R.string.you_have_not_given_your_permission_to_determine_your_location}")

        }
    }


    //определяем версию сдк (выше10) и  разрешение
    private fun checkLockPermisson() {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
           checPermisson10()
        } else {
            checPermissonBefor10()
        }
    }

    //сдк 10
    private fun checPermisson10() {

        if (сhekPermisson(Manifest.permission.ACCESS_FINE_LOCATION)
            && сhekPermisson(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ) {
            initOSM()
            checkLocationEnabled()
       } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                pLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                )
            }
        }
    }




    private fun checPermissonBefor10() {
        if (сhekPermisson(Manifest.permission.ACCESS_FINE_LOCATION)
       ) {
            initOSM()
            checkLocationEnabled()
       } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                pLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                )
            }
        }
    }

    //включение GPS
    private fun checkLocationEnabled() {
        val lManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!isEnabled) {
           DialogManager.showLocEnabledDialog(activity as AppCompatActivity,
               object :DialogManager.Listener{

                   override fun onCklick() {
                     //gps включаем
                       startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                   }

               })

        } else {
            //работает
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }

}