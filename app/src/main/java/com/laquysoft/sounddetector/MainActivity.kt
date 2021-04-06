package com.laquysoft.sounddetector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.huawei.hms.mlsdk.sounddect.MLSoundDector
import com.laquysoft.sounddetector.ui.theme.SoundDetectorTheme
import android.widget.Toast

import android.R
import android.util.Log

import com.huawei.hms.mlsdk.sounddect.MLSoundDectListener




class MainActivity : ComponentActivity() {

    private val mlSoundDetector: MLSoundDector by lazy { MLSoundDector.createSoundDector() }

    private val listener: MLSoundDectListener = object : MLSoundDectListener {
        override fun onSoundSuccessResult(result: Bundle) {
            val soundType = result.getInt(MLSoundDector.RESULTS_RECOGNIZED)
            if (soundType in 1..12) {
                TODO("Show sound detected")
            }
        }

        override fun onSoundFailResult(errCode: Int) {
           TODO("Handle faiure")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoundDetectorTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
        mlSoundDetector.mlSoundDectListener = listener
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SoundDetectorTheme {
        Greeting("Android")
    }
}