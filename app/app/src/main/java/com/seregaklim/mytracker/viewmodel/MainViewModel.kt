package com.seregaklim.mytracker.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.seregaklim.mytracker.location.LocationModel

class MainViewModel:ViewModel() {
    //следит за циклом жизни фрагмента
    val locationUpdates=MutableLiveData<LocationModel>()
    val timeData = MutableLiveData<String>()


}