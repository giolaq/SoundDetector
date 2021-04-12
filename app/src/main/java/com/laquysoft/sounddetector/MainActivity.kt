package com.laquysoft.sounddetector

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.laquysoft.sounddetector.ui.theme.SoundDetectorTheme
import com.laquysoft.sounddetector.util.viewModelProviderFactoryOf

class MainActivity : ComponentActivity() {

    val soundDetector = SoundDetector(this)

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SoundDetectorTheme {

                val viewModel: MainViewModel = viewModel(
                    factory = viewModelProviderFactoryOf { MainViewModel(soundDetector) }
                )
                val state by viewModel.state.observeAsState(State())

                checkMicrophonePermission()

                Home(
                    state,
                    viewModel::startListening,
                    viewModel::stopListening
                )
            }
        }
    }

    private fun checkMicrophonePermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.RECORD_AUDIO
                ), 0x123
            )
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun Home(state: State, startTimer: () -> Unit = {}, stopTimer: () -> Unit = {}) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Box(contentAlignment = Alignment.Center) {
            Card {
                var expanded by remember { mutableStateOf(false) }

                Column {

                    DetectorStatus(state, Modifier.clickable {
                        if (state.isDetectorRunning) stopTimer() else startTimer()
                        expanded = !expanded
                    })

                    DetectionStatus(state = state, expanded = expanded)
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun DetectionStatus(state: State, expanded: Boolean) {
    AnimatedVisibility(expanded) {
        Spacer(Modifier.size(20.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(128.dp, 128.dp)
                .padding(16.dp)
        ) {
            if (state.detectedSound == null) {
                CircularProgressIndicator(Modifier.size(64.dp, 64.dp))
            } else {
                DetectedSound(state.detectedSound)
            }
        }
    }
}

@Composable
private fun DetectorStatus(state: State, modifier: Modifier) {
    if (state.isDetectorRunning) {
        Image(
            painterResource(R.drawable.notlistening),
            contentDescription = null,
            modifier = modifier.size(128.dp, 128.dp)
        )
    } else {
        Image(
            painterResource(R.drawable.listening),
            contentDescription = null,
            modifier = modifier.size(128.dp, 128.dp)
        )
    }
}

@Composable
fun DetectedSound(detectedSoundEvent: SoundEvent?) {
    when (detectedSoundEvent) {
        SoundEvent.KNOCK -> {
            Image(
                painterResource(R.drawable.knock),
                contentDescription = null
            )
        }
        SoundEvent.BABY_CRY -> {
            Image(
                painterResource(R.drawable.cry),
                contentDescription = null
            )
        }
        else -> {
        }
    }
}

@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SoundDetectorTheme {
        Home(state = State(isDetectorRunning = true))
    }
}

@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreviewDetectorNotRunning() {
    SoundDetectorTheme {
        Home(state = State(isDetectorRunning = false))
    }
}

@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreviewKnockDetected() {
    SoundDetectorTheme {
        Home(state = State(isDetectorRunning = true, detectedSound = SoundEvent.KNOCK))
    }
}