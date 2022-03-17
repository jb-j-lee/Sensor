package com.myjb.dev.sensor.light.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LightViewModel : ViewModel() {
    var lux: MutableLiveData<Float> = MutableLiveData(0f)
    var brightness: MutableLiveData<Int> = MutableLiveData(0)
}