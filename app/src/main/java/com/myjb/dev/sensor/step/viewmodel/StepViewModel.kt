package com.myjb.dev.sensor.step.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StepViewModel : ViewModel() {
    var counter: MutableLiveData<Int> = MutableLiveData(0)
    var detector: MutableLiveData<Int> = MutableLiveData(0)
}