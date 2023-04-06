package com.seregaklim.mytracker.utils

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*


object TimeUtils {

  @SuppressLint("SimpleDateFormat")
  @RequiresApi(Build.VERSION_CODES.N)
  private  val timeFormater = SimpleDateFormat("HH:mm:ss:SSS")

    @RequiresApi(Build.VERSION_CODES.N)
    fun getTime(timeMillis: Long): String {
        val cv = Calendar.getInstance()
        timeFormater.timeZone= TimeZone.getTimeZone("UTC")
        cv.timeInMillis = timeMillis

        return timeFormater.format(cv.time)
    }



}















