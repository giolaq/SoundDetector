package com.laquysoft.sounddetector

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import com.huawei.hms.mlsdk.sounddect.MLSoundDectListener
import com.huawei.hms.mlsdk.sounddect.MLSoundDector

sealed class State() {
    class Stopped() : State()

    class Running() : State()
}


class MainViewModel(private val mlSoundDetector: MLSoundDector, application: Application) :
    AndroidViewModel(application) {

    private val _state = MutableLiveData<State>(State.Stopped())
    val state: LiveData<State> = _state


    private val listener: MLSoundDectListener = object : MLSoundDectListener {
        override fun onSoundSuccessResult(result: Bundle) {
            val soundType = result.getInt(MLSoundDector.RESULTS_RECOGNIZED)
            if (soundType in 1..12) {
                Log.d("MLSoundDectListener", "Show sound detected")
            }
        }

        override fun onSoundFailResult(errCode: Int) {
            Log.d("MLSoundDectListener", "failure")
        }
    }

    fun startListening() {
        mlSoundDetector.setSoundDectListener(listener)
        mlSoundDetector.start(getApplication())

        val currentState = _state.value
        if (currentState !is State.Stopped) {
            return
        }

        _state.value = State.Running()
    }

    fun stopListening() {
        mlSoundDetector.stop()

        _state.value = State.Stopped()
    }

}