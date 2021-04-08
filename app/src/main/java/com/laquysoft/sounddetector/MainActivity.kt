package com.laquysoft.sounddetector

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@Composable
fun Home(state: State, startTimer: () -> Unit = {}, stopTimer: () -> Unit = {}) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                DetectedSound(state.detectedSound)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    DetectorStatus(state)
                }

                Button(
                    onClick = { if (state.isDetectorRunning) stopTimer() else startTimer() },
                    shape = CircleShape,
                    enabled = true,
                    modifier = Modifier.padding(top = 30.dp)
                ) {
                    Text(text = if (state.isDetectorRunning) "Stop listening" else "Start listening")
                }
            }
        }
    }
}

@Composable
private fun DetectorStatus(state: State) {
    if (state.isDetectorRunning) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painterResource(R.drawable.listening),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp, 128.dp)
                    .padding(top = 30.dp)
            )
            Text(
                text = "SoundDetection running",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(start = 30.dp, end = 30.dp)
            )
        }
    } else {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painterResource(R.drawable.notlistening),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp, 128.dp)
                    .padding(top = 30.dp)
            )
            Text(
                text = "SoundDetection stopped",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(start = 30.dp, end = 30.dp)
            )
        }
    }
}

@Composable
fun DetectedSound(detectedSoundEvent: SoundEvent?) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(128.dp, 128.dp)
    ) {
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
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SoundDetectorTheme {
        Home(state = State(isDetectorRunning = true))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewDetectorNotRunning() {
    SoundDetectorTheme {
        Home(state = State(isDetectorRunning = false))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewKnockDetected() {
    SoundDetectorTheme {
        Home(state = State(isDetectorRunning = true, detectedSound = SoundEvent.KNOCK))
    }
}