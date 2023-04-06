package com.seregaklim.mytracker.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.seregaklim.mytracker.databinding.TrackBinding
import com.seregaklim.mytracker.utils.DialogManager


class TrackFragment : Fragment() {

        private lateinit var binding: TrackBinding
        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View{
            binding=TrackBinding.inflate(inflater,container,false)


           DialogManager.showLocEnabledDialog(activity as AppCompatActivity,
                object : DialogManager.Listener{

                    override fun onCklick() {
                        //gps включаем
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }

                })



            return binding.root
        }


        companion object {
            @JvmStatic
            fun newInstance() =  TrackFragment()
        }

    }
