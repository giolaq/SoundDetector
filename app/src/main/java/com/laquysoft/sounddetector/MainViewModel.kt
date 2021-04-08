package com.laquysoft.sounddetector

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.huawei.hms.mlsdk.sounddect.MLSoundDector

class MainViewModel(private val soundDetector: SoundDetector) : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    init {
        soundDetector.setCallbacks(
            onSuccess = { result -> onSoundDetected(result) },
            onError = { errCode -> onDetectorError(errCode) })
    }

    fun startListening() {
        soundDetector.startDetection()

        val currentState = _state.value
        if (currentState?.isDetectorRunning == true) {
            return
        }

        _state.value = State(isDetectorRunning = true)
    }

    fun stopListening() {
        soundDetector.stopDetection()

        _state.value = State(isDetectorRunning = false)
    }

    private fun onSoundDetected(result: Bundle) {
        val soundType = result.getInt(MLSoundDector.RESULTS_RECOGNIZED)
        if (soundType in 1..12) {
            _state.value = State(
                isDetectorRunning = true,
                detectedSound = SoundEvent.values()[soundType]
            )
        }
    }

    private fun onDetectorError(errorCode: Int) {
        _state.value = State(isDetectorRunning = true, error = errorCode.toString())
    }

}

data class State(
    val isDetectorRunning: Boolean = false,
    val detectedSound: SoundEvent? = null,
    val error: String? = null
)

enum class SoundEvent {
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