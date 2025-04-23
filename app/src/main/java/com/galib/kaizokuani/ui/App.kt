package com.galib.kaizokuani.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.galib.kaizokuani.ui.components.BottomNavigationComponent
import com.galib.kaizokuani.ui.screens.AnimeDetailsScreenComposable
import com.galib.kaizokuani.ui.screens.AnimeSearchScreenComposable
import com.galib.kaizokuani.ui.screens.ExploreScreenComposable
import com.galib.kaizokuani.ui.screens.ProfileScreenComposable
import com.galib.kaizokuani.ui.screens.VideoPlaybackScreenComposable
import com.galib.kaizokuani.ui.theme.AppTheme
import com.galib.kaizokuani.ui.viewmodels.ExploreScreenViewModel
import kotlinx.serialization.Serializable

@Composable
fun App() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    var showBottomBar by rememberSaveable { mutableStateOf(true) }
    val exploreScreenViewModel: ExploreScreenViewModel = viewModel()

    AppTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (showBottomBar) {
                    BottomNavigationComponent { index ->
                        when(index) {
                            0 -> { navController.navigate(ExploreScreen) }
                            1 -> { navController.navigate(ProfileScreen) }
                            else -> {}
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = ExploreScreen,
                modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)
                // startDestination = VideoPlayer("https://video.wixstatic.com/video/b42737_be840c03c1904e0c9d71a2a8eeadd088/1080p/mp4/file.mp4", null)
                // startDestination = AnimeDetails("vDTSJHSpYnrkZnAvG"),
            ) {
                composable<ExploreScreen> {
                    showBottomBar = true
                    ExploreScreenComposable(navController = navController, viewModel = exploreScreenViewModel)
                }
                composable<AnimeSearchScreen> { backStackEntry ->
                    showBottomBar = true
                    val animeSearch: AnimeSearchScreen = backStackEntry.toRoute()
                    AnimeSearchScreenComposable(
                        animeSearchScreen = animeSearch,
                        navController = navController
                    )
                }
                composable<AnimeDetailsScreen> { backStackEntry ->
                    showBottomBar = true
                    val animeDetails: AnimeDetailsScreen = backStackEntry.toRoute()
                    AnimeDetailsScreenComposable(
                        animeDetailsScreen = animeDetails,
                        snackbarHostState = snackbarHostState,
                        navController = navController
                    )
                }
                composable<VideoPlaybackScreen> { backStackEntry ->
                    showBottomBar = false
                    val videoPlayer: VideoPlaybackScreen = backStackEntry.toRoute()
                    VideoPlaybackScreenComposable(
                        videoPlaybackScreen = videoPlayer,
                        navController = navController
                    )
                }
                composable<ProfileScreen> {
                    showBottomBar = true
                    ProfileScreenComposable(navController = navController)
                }
            }
        }
    }
}

@Serializable
data class AnimeDetailsScreen (
    val id: String?
)

@Serializable
data class AnimeSearchScreen (
    val query: String?
)

@Serializable
data class VideoPlaybackScreen (
    val url: String?,
    val title: String?
)

@Serializable
object ProfileScreen

@Serializable
object ExploreScreen
