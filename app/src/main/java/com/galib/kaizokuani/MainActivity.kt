package com.galib.kaizokuani

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.galib.kaizokuani.components.BottomNavigationComponent
import com.galib.kaizokuani.components.LoadingPageComponent
import com.galib.kaizokuani.screens.ALL_TIME
import com.galib.kaizokuani.screens.AnimeDetailsScreen
import com.galib.kaizokuani.screens.AnimeSearchScreen
import com.galib.kaizokuani.screens.DAILY
import com.galib.kaizokuani.screens.ExploreScreen
import com.galib.kaizokuani.screens.MONTHLY
import com.galib.kaizokuani.screens.ProfileScreen
import com.galib.kaizokuani.screens.VideoPlayerScreen
import com.galib.kaizokuani.screens.WEEKLY
import com.galib.kaizokuani.ui.theme.AppTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
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

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun App() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val popularAnimeResultSaver = Saver<List<PopularAnimeResult>, String>(
        save = { JsonParser.json.encodeToString(it) },
        restore = { JsonParser.json.decodeFromString<List<PopularAnimeResult>>(it) }
    )
    var resultsAllTime by rememberSaveable(stateSaver = popularAnimeResultSaver) { mutableStateOf<List<PopularAnimeResult>>(emptyList()) }
    var resultsDaily by rememberSaveable(stateSaver = popularAnimeResultSaver) { mutableStateOf<List<PopularAnimeResult>>(emptyList()) }
    var resultsWeekly by rememberSaveable(stateSaver = popularAnimeResultSaver) { mutableStateOf<List<PopularAnimeResult>>(emptyList()) }
    var resultsMonthly by rememberSaveable(stateSaver = popularAnimeResultSaver) { mutableStateOf<List<PopularAnimeResult>>(emptyList()) }
    var loading by rememberSaveable { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    var jobs = remember { mutableStateListOf<Job>() }

    LaunchedEffect(Unit) {
        jobs.add(scope.launch {
            AnimeApi.getPopularAnime(ALL_TIME) { response ->
                resultsAllTime = JsonParser.parsePopularAnimeResult(response)
            }
        })
        jobs.add(scope.launch {
            AnimeApi.getPopularAnime(DAILY) { response ->
                resultsDaily = JsonParser.parsePopularAnimeResult(response)
            }
        })
        jobs.add(scope.launch {
            AnimeApi.getPopularAnime(WEEKLY) { response ->
                resultsWeekly = JsonParser.parsePopularAnimeResult(response)
            }
        })
        jobs.add(scope.launch {
            AnimeApi.getPopularAnime(MONTHLY) { response ->
                resultsMonthly = JsonParser.parsePopularAnimeResult(response)
            }
        })
        jobs.joinAll()
        loading = false
    }

    if (loading) LoadingPageComponent()

    AppTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                BottomNavigationComponent { index ->
                    when(index) {
                        0 -> { navController.navigate(Explore) }
                        1 -> { navController.navigate(Profile) }
                        else -> {}
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Explore,
                modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)
                // startDestination = VideoPlayer("https://video.wixstatic.com/video/b42737_be840c03c1904e0c9d71a2a8eeadd088/1080p/mp4/file.mp4", null)
                // startDestination = AnimeDetails("vDTSJHSpYnrkZnAvG"),
            ) {
                composable<Explore> {
                    ExploreScreen(navController = navController, results = listOf(
                        resultsAllTime, resultsDaily,
                        resultsWeekly, resultsMonthly
                    ))
                }
                composable<AnimeSearch> { backStackEntry ->
                    val animeSearch: AnimeSearch = backStackEntry.toRoute()
                    AnimeSearchScreen(
                        animeSearch = animeSearch,
                        navController = navController
                    )
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
                composable<Profile> {
                    ProfileScreen(navController = navController)
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
data class AnimeSearch (
    val query: String?
)

@Serializable
data class VideoPlayer (
    val url: String?,
    val title: String?
)

@Serializable
object Profile

@Serializable
object Explore
