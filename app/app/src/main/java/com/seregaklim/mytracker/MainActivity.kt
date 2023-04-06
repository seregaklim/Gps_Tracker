package com.seregaklim.mytracker

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.seregaklim.mytracker.databinding.ActivityMainBinding
import com.seregaklim.mytracker.fragments.MainFragment
import com.seregaklim.mytracker.fragments.SettingsFragment
import com.seregaklim.mytracker.fragments.TrackFragment
import com.seregaklim.mytracker.service.LocationService
import com.seregaklim.mytracker.utils.openFragment

class MainActivity : AppCompatActivity() {

  private  lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBottonNavClicks()
        openFragment(MainFragment.newInstance())


    }

    private fun onBottonNavClicks(){
        binding.bottomNavigationView.setOnItemSelectedListener{
          when(it.itemId){
              R.id.id_home->{
                  openFragment(MainFragment.newInstance())
              }

              R.id.id_tracks->{
                openFragment(TrackFragment.newInstance())
              }

              R.id.id_settings->{
                  openFragment(SettingsFragment())

              }
          }
            true

        }

    }


}