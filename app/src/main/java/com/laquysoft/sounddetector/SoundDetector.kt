package com.laquysoft.sounddetector

import android.content.Context
import android.os.Bundle
import com.huawei.hms.mlsdk.sounddect.MLSoundDectListener
import com.huawei.hms.mlsdk.sounddect.MLSoundDector

class SoundDetector(private val context: Context) {
    var mlSoundDetector: MLSoundDector = MLSoundDector.createSoundDector()

    fun startDetection() {
        mlSoundDetector.start(context)
    }

    fun stopDetection() {
        mlSoundDetector.stop()
    }

    fun setCallbacks(
        onSuccess: (Bundle) -> Unit = {},
        onError: (Int) -> Unit = {}
    ) {
        mlSoundDetector.setSoundDectListener(object : MLSoundDectListener {
            override fun onSoundSuccessResult(result: Bundle) {
                onSuccess(result)
            }

            override fun onSoundFailResult(errCode: Int) {
                onError(errCode)
            }
        })
    }
}