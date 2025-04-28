package com.galib.kaizokuani.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.galib.kaizokuani.data.AppDataManager
import com.galib.kaizokuani.ui.components.BottomNavigationComponent
import com.galib.kaizokuani.ui.screens.AnimeDetailsScreenComposable
import com.galib.kaizokuani.ui.screens.AnimeSearchScreenComposable
import com.galib.kaizokuani.ui.screens.ExploreScreenComposable
import com.galib.kaizokuani.ui.screens.ProfileScreenComposable
import com.galib.kaizokuani.ui.screens.VideoPlaybackScreenComposable
import com.galib.kaizokuani.ui.theme.AppTheme
import com.galib.kaizokuani.ui.viewmodels.ExploreScreenViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Composable
fun App() {
    val rootNavController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    var showBottomBar by rememberSaveable { mutableStateOf(true) }
    val exploreScreenViewModel: ExploreScreenViewModel = viewModel()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { owner, event ->
            when(event) {
                Lifecycle.Event.ON_CREATE -> { AppDataManager.init(context) }
                Lifecycle.Event.ON_RESUME -> {
                    scope.launch { AppDataManager.loadFromDataStore() }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    scope.launch { AppDataManager.saveToDataStore() }
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AppTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (showBottomBar) {
                    BottomNavigationComponent { url ->
                        rootNavController.navigate(url) {
                            popUpTo(rootNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = rootNavController,
                startDestination = "/",
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                // startDestination = VideoPlayer("https://video.wixstatic.com/video/b42737_be840c03c1904e0c9d71a2a8eeadd088/1080p/mp4/file.mp4", null)
                // startDestination = AnimeDetails("vDTSJHSpYnrkZnAvG"),
            ) {
                composable("/") {
                    val exploreNavController = rememberNavController()
                    NavHost(
                        navController = exploreNavController,
                        startDestination = ExploreScreen
                    ) {
                        composable<ExploreScreen> {
                            showBottomBar = true
                            ExploreScreenComposable(
                                navController = exploreNavController,
                                viewModel = exploreScreenViewModel
                            )
                        }
                        composable<AnimeSearchScreen> { backStackEntry ->
                            showBottomBar = true
                            val animeSearch: AnimeSearchScreen = backStackEntry.toRoute()
                            AnimeSearchScreenComposable(
                                animeSearchScreen = animeSearch,
                                navController = exploreNavController
                            )
                        }
                        composable<AnimeDetailsScreen> { backStackEntry ->
                            showBottomBar = true
                            val animeDetails: AnimeDetailsScreen = backStackEntry.toRoute()
                            AnimeDetailsScreenComposable(
                                animeDetailsScreen = animeDetails,
                                snackbarHostState = snackbarHostState,
                                navController = exploreNavController
                            )
                        }
                        composable<VideoPlaybackScreen> { backStackEntry ->
                            showBottomBar = false
                            val videoPlayer: VideoPlaybackScreen = backStackEntry.toRoute()
                            VideoPlaybackScreenComposable(
                                videoPlaybackScreen = videoPlayer,
                                navController = exploreNavController
                            )
                        }
                    }
                }

                composable("/profile") {
                    showBottomBar = true
                    ProfileScreenComposable(navController = rootNavController)
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
    val title: String?,
    val id: String,
    val translationType: String,
    val episodeNo: String
)

@Serializable
object ExploreScreen
