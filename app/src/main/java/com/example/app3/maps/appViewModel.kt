package com.example.app3.maps

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class appViewModel(application: Application): AndroidViewModel(application){
    private val locationLiveData = LocationLiveData(application)
    fun getLocationLiveData() = locationLiveData
    fun startLocationUpdates(){
        locationLiveData.startLocationUpdates()
    }
}