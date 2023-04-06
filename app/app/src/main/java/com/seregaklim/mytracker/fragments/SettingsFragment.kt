package com.seregaklim.mytracker.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.seregaklim.mytracker.R

class SettingsFragment :PreferenceFragmentCompat(){
    private lateinit var timePref:Preference
    private lateinit var colorPref:Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preference,rootKey)
         init()

    }
     private fun init(){
         //находим по ключу
         timePref=findPreference("update_time_key")!!
         colorPref=findPreference("color_key")!!

          //один слушатель, следит за изменениями
         val changeListener=onChangeListener()
         timePref.onPreferenceChangeListener=changeListener
          colorPref.onPreferenceChangeListener=changeListener

         initPrefs()
     }
     //слушатель изменений-  показываем изменения, которые выбрал пользователь (если они есть)
    private fun onChangeListener():Preference.OnPreferenceChangeListener{
        return Preference.OnPreferenceChangeListener{
            pref,value->
            //cледим изменения по ключу
            when(pref.key){
                "update_time_key"->onTimeChange(value.toString())
                "color_key"-> onColorChange (value.toString())
            }

       true
        }
    }
    private fun onTimeChange (value:String){
        val nameArray=resources.getStringArray(R.array.loc_time_update_name)
        val valueArray=resources.getStringArray(R.array.loc_time_update_value)

        //можем показать любой текст
        //.substringBefore(":") показывает текст до:
        val title=timePref.title.toString().substringBefore(":")
        val pos=valueArray.indexOf(value)
        timePref.title="$title: ${nameArray[pos]}"
    }

    private fun onColorChange(value: String) {
        colorPref.icon?.setTint(Color.parseColor(value))
    }

    //показывает из памяти, то что выбрали
    private fun initPrefs() {
        //есть доступ к памяти
        val pref = timePref.preferenceManager.sharedPreferences

        val nameArray = resources.getStringArray(R.array.loc_time_update_name)
        val valueArray = resources.getStringArray(R.array.loc_time_update_value)

        //можем показать любой текст
        val title = timePref.title.toString()
        val pos = valueArray.indexOf(pref?.getString("update_time_key", "3000"))

        timePref.title = "$title:${nameArray[pos]}"


        //цвет
        val trackColor = pref?.getString("color_key", "#61ABEC")
        //цвет по умолчанию
        colorPref.icon?.setTint(Color.parseColor(trackColor))
    }




}