package com.laquysoft.sounddetector

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huawei.hms.mlsdk.sounddect.MLSoundDector
import com.laquysoft.sounddetector.ui.theme.SoundDetectorTheme
import com.laquysoft.sounddetector.util.viewModelProviderFactoryOf

class MainActivity : ComponentActivity() {


    private val RC_RECORD_CODE = 0x123

    private val perms = arrayOf(
        Manifest.permission.RECORD_AUDIO
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            SoundDetectorTheme {
                val mlSoundDetector by lazy { MLSoundDector.createSoundDector() }

                val viewModel: MainViewModel = viewModel(
                    factory = viewModelProviderFactoryOf {
                        MainViewModel(
                            mlSoundDetector,
                            application
                        )
                    }
                )
                val state by viewModel.state.observeAsState(State.Stopped())
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, perms, RC_RECORD_CODE);
                }
                Home(
                    state,
                    viewModel::startListening,
                    viewModel::stopListening
                )
            }
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
                when (state) {
                    is State.Running -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SoundDetection running",
                                style = MaterialTheme.typography.h4,
                                modifier = Modifier.padding(start = 30.dp, end = 30.dp)
                            )
                        }
                        Button(
                            onClick = { stopTimer() },
                            shape = CircleShape,
                            enabled = true,
                            modifier = Modifier.padding(top = 30.dp)
                        ) {
                            Text(text = "Stop listening")
                        }
                    }
                    is State.Stopped -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SoundDetection stopped",
                                style = MaterialTheme.typography.h4,
                                modifier = Modifier.padding(start = 30.dp, end = 30.dp)
                            )
                        }
                        Button(
                            onClick = { startTimer() },
                            shape = CircleShape,
                            enabled = true,
                            modifier = Modifier.padding(top = 30.dp)
                        ) {
                            Text(text = "Start listening")
                        }
                    }
                }

            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SoundDetectorTheme {
        Home(state = State.Running())
    }
}