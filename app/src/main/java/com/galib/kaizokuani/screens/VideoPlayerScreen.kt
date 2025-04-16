package com.galib.kaizokuani.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.OrientationEventListener
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.galib.kaizokuani.R
import com.galib.kaizokuani.VideoPlayer

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(videoPlayer: VideoPlayer, navController: NavHostController) {
    if (videoPlayer.url == null) Text(text = "No url specified")

    val title = rememberSaveable { videoPlayer.title ?: "Untitled" }
    val url = rememberSaveable { videoPlayer.url!! }

    val context = LocalContext.current
    val activity = context as Activity
    val lifeCycleOwner = LocalLifecycleOwner.current
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var contentPosition by rememberSaveable { mutableStateOf(0L) }

    val playerView = PlayerView(context)
    playerView.useController = true

    LaunchedEffect(Unit) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val controller = activity.window.decorView.windowInsetsController
        controller?.hide(WindowInsets.Type.statusBars())
        controller?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

   DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when(event) {
                Lifecycle.Event.ON_START -> {
                    exoPlayer = ExoPlayer.Builder(context)
                        .setSeekBackIncrementMs(10000)
                        .setSeekForwardIncrementMs(10000)
                        .build()
                    playerView.player = exoPlayer
                }
                Lifecycle.Event.ON_RESUME -> {
                    exoPlayer?.apply {
                        setMediaItem(
                            MediaItem.Builder()
                                .setUri(videoPlayer.url!!.toUri())
                                .build()
                        )
                        prepare()
                        playWhenReady = true
                    }
                    exoPlayer?.play()
                    exoPlayer?.seekTo(contentPosition)
                }
                Lifecycle.Event.ON_PAUSE -> {
                    exoPlayer?.pause()
                    contentPosition = exoPlayer?.contentPosition?.coerceAtLeast(0L) ?: 0L
                }
                Lifecycle.Event.ON_STOP -> exoPlayer?.release()
                else -> {}
            }
        }

        lifeCycleOwner.lifecycle.addObserver(observer)

        val orientationEventListener = object: OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation in 80..159) {
                    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                } else if (orientation in 200..289) {
                   activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }
        }

        orientationEventListener.enable()

        onDispose {
            exoPlayer?.release()
            lifeCycleOwner.lifecycle.removeObserver(observer)
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            orientationEventListener.disable()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { playerView },
            update = { view ->
                val titleTextView = view.findViewById<TextView>(R.id.exo_title)
                titleTextView.text = title
            },
            modifier = Modifier
                .fillMaxSize()
        )
    }

    BackHandler {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity.window.decorView.windowInsetsController?.show(WindowInsets.Type.statusBars())
        navController.popBackStack()
    }
}