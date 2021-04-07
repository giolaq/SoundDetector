package com.laquysoft.sounddetector

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.huawei.hms.mlsdk.sounddect.MLSoundDectListener
import com.huawei.hms.mlsdk.sounddect.MLSoundDector

class MainViewModel(private val mlSoundDetector: MLSoundDector, application: Application) :
    AndroidViewModel(application) {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state


    private val listener: MLSoundDectListener = object : MLSoundDectListener {
        override fun onSoundSuccessResult(result: Bundle) {
            val soundType = result.getInt(MLSoundDector.RESULTS_RECOGNIZED)
            if (soundType in 1..12) {
                _state.value = State(isDetectorRunning = true, detectedSound = SoundEvent.values()[soundType])
            }
        }

        override fun onSoundFailResult(errCode: Int) {
            _state.value = State(isDetectorRunning = true, error = errCode.toString())
        }
    }

    fun startListening() {
        mlSoundDetector.setSoundDectListener(listener)
        mlSoundDetector.start(getApplication())

        val currentState = _state.value
        if (currentState?.isDetectorRunning == true) {
            return
        }

        _state.value = State(isDetectorRunning = true)
    }

    fun stopListening() {
        mlSoundDetector.stop()

        _state.value = State(isDetectorRunning = false)
    }

}

data class State(
    val isDetectorRunning: Boolean = false,
    val detectedSound: SoundEvent? = null,
    val error: String? = null
)

enum class SoundEvent{
    LAUGHTER,
    BABY_CRY,
    SNORING,
    SNEEZE,
    SCREAMING,
    MEOW,
    BARK,
    WATER,
    CAR_ALARM,
    DOORBELL,
    KNOCK,
    ALARM,
    STEAM_WHISTLE
}