package com.seregaklim.mytracker.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.seregaklim.mytracker.R

// открывает указанные фрагменты
fun Fragment.openFragment (fragment: Fragment){
    (activity as AppCompatActivity).supportFragmentManager
        .beginTransaction()
      //анимация  (fade_in-начало, fade_out-конец)
        .setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
        .replace(R.id.placeHolder,fragment).commit()
}

// открывает указанные фрагменты
fun AppCompatActivity.openFragment (fragment: Fragment){
   //  Log.d("MyLog","Frag name: ${fragment.javaClass}")

    if (supportFragmentManager.fragments.isNotEmpty()){
        //запускаем фрагмент один раз
        if (supportFragmentManager.fragments[0].javaClass ==fragment.javaClass) return
    }

      supportFragmentManager
        .beginTransaction()
        //анимация  (fade_in-начало, fade_out-конец)
        .setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
        .replace(R.id.placeHolder,fragment).commit()
}



fun Fragment.showTost (s:String){
      Toast.makeText(activity,s,Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.showTost (s:String){
    Toast.makeText(this,s,Toast.LENGTH_LONG).show()
}

//разрешение
fun Fragment.сhekPermisson(p: String): Boolean {
    return when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(activity as AppCompatActivity, p) -> true
        else  ->false
    }
}


//разрешение
fun AppCompatActivity.сhekPermisson(p: String): Boolean {
    return when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(this , p) -> true
        else  ->false
    }
}
















