package com.galib.kaizokuani

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.galib.kaizokuani.screens.AnimeDetailsScreen
import com.galib.kaizokuani.screens.AnimeSearchScreen
import com.galib.kaizokuani.screens.VideoPlayerScreen
import com.galib.kaizokuani.ui.theme.AppTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            App()
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    AppTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AnimeSearch
                // startDestination = VideoPlayer("https://video.wixstatic.com/video/b42737_be840c03c1904e0c9d71a2a8eeadd088/1080p/mp4/file.mp4", null)
                // startDestination = AnimeDetails("vDTSJHSpYnrkZnAvG"),
            ) {
                composable<AnimeSearch> {
                    AnimeSearchScreen(onNavigateToInfoScreen = { id ->
                        navController.navigate(route = AnimeDetails(id = id))
                    }, navController = navController)
                }
                composable<AnimeDetails> { backStackEntry ->
                    val animeDetails: AnimeDetails = backStackEntry.toRoute()
                    AnimeDetailsScreen(
                        animeDetails = animeDetails,
                        snackbarHostState = snackbarHostState,
                        navController = navController
                    )
                }
                composable<VideoPlayer> { backStackEntry ->
                    val videoPlayer: VideoPlayer = backStackEntry.toRoute()
                    VideoPlayerScreen(
                        videoPlayer = videoPlayer,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Serializable
data class AnimeDetails (
    val id: String?
)

@Serializable
object AnimeSearch

@Serializable
data class VideoPlayer (
    val url: String?,
    val title: String?
)
