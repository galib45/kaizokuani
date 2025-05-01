package com.galib.kaizokuani.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.OrientationEventListener
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.galib.kaizokuani.data.AnimeProgress
import com.galib.kaizokuani.data.AppDataManager
import com.galib.kaizokuani.data.LastPlayedEpisode
import com.galib.kaizokuani.ui.VideoPlaybackScreen
import com.galib.ui.theme.AppTypography
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(UnstableApi::class)
@Composable
fun VideoPlaybackScreenComposable(videoPlaybackScreen: VideoPlaybackScreen, navController: NavHostController) {
    if (videoPlaybackScreen.url == null) Text(text = "No url specified")

    val title = rememberSaveable { videoPlaybackScreen.title ?: "Untitled" }
    val id = rememberSaveable { videoPlaybackScreen.id }
    val translationType = rememberSaveable { videoPlaybackScreen.translationType }
    val episodeNo = rememberSaveable { videoPlaybackScreen.episodeNo }
    val animeProgress = AppDataManager.appData.collectAsState().value.animeProgressData.get(id)

    val context = LocalContext.current
    val activity = context as Activity
    val lifeCycleOwner = LocalLifecycleOwner.current
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var contentPosition by rememberSaveable { mutableLongStateOf(0L) }

    animeProgress?.let { progress ->
        var lastPlayed = if (translationType == "dub") progress.lastPlayedDub else progress.lastPlayedSub
        if (lastPlayed?.name == episodeNo) contentPosition = lastPlayed.contentPosition
    }

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
                                .setUri(videoPlaybackScreen.url!!.toUri())
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
                    AppDataManager.addProgressEntry(
                        id, episodeNo,
                        translationType, contentPosition
                    )
                    runBlocking { AppDataManager.saveToDataStore() }
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
                val composeView = view.findViewById<ComposeView>(R.id.exo_title_compose_view)
                val modifiedTitle = title.split(" ").let { words ->
                    if (words.size >= 2) {
                        val lastTwo = words.takeLast(2).joinToString(separator = "\u00A0")
                        (words.dropLast(2) + lastTwo).joinToString(" ")
                    } else {
                        title
                    }
                }
                composeView.setContent {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Text(
                            text = modifiedTitle,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(horizontal = 18.dp)
                        )
                    }
                }
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