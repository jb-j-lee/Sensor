package com.myjb.dev.sensor.fingerprint.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FingerprintViewModel : ViewModel() {
    var status: MutableLiveData<Status> = MutableLiveData(Status.NORMAL)
    var message: MutableLiveData<CharSequence> = MutableLiveData("")
}

enum class Status {
    NORMAL,
    ERROR,
    SUCCESS
}