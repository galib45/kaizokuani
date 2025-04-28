package com.galib.kaizokuani.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.galib.kaizokuani.data.AnimeApi
import com.galib.kaizokuani.data.EpisodeLink
import com.galib.kaizokuani.data.JsonParser
import com.galib.kaizokuani.data.ShowInfo
import com.galib.kaizokuani.ui.AnimeDetailsScreen
import com.galib.kaizokuani.ui.VideoPlaybackScreen
import com.galib.kaizokuani.ui.components.AnimeInfoCardComponent
import com.galib.kaizokuani.ui.components.DialogComponent
import com.galib.kaizokuani.ui.components.DialogListPickerComponent
import com.galib.kaizokuani.ui.components.EpisodesComponent
import com.galib.kaizokuani.ui.components.LoadingDialogComponent
import com.galib.kaizokuani.ui.components.LoadingPageComponent
import com.galib.kaizokuani.utils.decodeSourceUrl
import com.galib.kaizokuani.utils.downloadFile
import com.galib.kaizokuani.utils.extractWixmpLinks
import com.galib.ui.theme.AppTypography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

enum class ScreenState {
    GET_INFO,
    WAIT_FOR_ACTION,
    CHOOSE_ACTION,
    GET_LINKS,
    CHOOSE_QUALITY,
}

enum class Action {
    PLAY, DOWNLOAD
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AnimeDetailsScreenComposable(
    animeDetailsScreen: AnimeDetailsScreen,
    snackbarHostState: SnackbarHostState,
    navController: NavHostController
) {
    if (animeDetailsScreen.id == null) Text(text = "Invalid anime id")
    val scope = rememberCoroutineScope()
    val animeInfoSaver = Saver<ShowInfo?, String>(
        save = { JsonParser.json.encodeToString(it) },
        restore = { JsonParser.json.decodeFromString<ShowInfo?>(it) }
    )
    var info by rememberSaveable(stateSaver = animeInfoSaver) { mutableStateOf<ShowInfo?>(null) }
    var episodeNo by remember { mutableStateOf("") }
    var translationType by remember { mutableStateOf("") }
    var screenState by rememberSaveable { mutableStateOf(ScreenState.GET_INFO) }
    var action by remember { mutableStateOf<Action?>(null) }
    var jobs = remember { mutableStateListOf<Job>() }
    var links = remember { mutableStateListOf<EpisodeLink>() }
    var qualities = remember { mutableStateListOf<String>() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        AnimeApi.getAnimeInfo(animeDetailsScreen.id!!) { response ->
            info = JsonParser.parseAnimeInfo(response)
            screenState = ScreenState.WAIT_FOR_ACTION
        }
    }

    if (screenState == ScreenState.GET_INFO) LoadingPageComponent()

    info?.let { info ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = info.name, style = AppTypography.titleLarge)
            if (info.englishName != null) Text(
                text = info.englishName,
                style = AppTypography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                AsyncImage(
                    model = info.thumbnail,
                    contentDescription = "thumbnail of ${info.name}",
                    modifier = Modifier.height(200.dp),
                )
                Spacer(modifier = Modifier.width(16.dp))
                AnimeInfoCardComponent(info)
            }
            Spacer(modifier = Modifier.height(16.dp))
            EpisodesComponent(info, onEpisodeClick = { ep, type ->
                episodeNo = ep
                translationType = type
                screenState = ScreenState.CHOOSE_ACTION
            })
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Description")
            Text(
                style = AppTypography.bodyMedium,
                text = AnnotatedString.fromHtml(info.description ?: "No description available") )
        }

        if (screenState == ScreenState.GET_LINKS) LoadingDialogComponent(title = "Getting links")

        if (screenState == ScreenState.CHOOSE_QUALITY) {
            DialogListPickerComponent("Select Quality", qualities, onSelect = { index ->
                val url = links[index].link
                val title = "${info.name} Episode $episodeNo"
                val filename = "${info.name} Episode $episodeNo.mp4"

                screenState = ScreenState.WAIT_FOR_ACTION
                when(action) {
                    Action.PLAY -> navController.navigate(route = VideoPlaybackScreen(
                        url = url, title = title,
                        episodeNo = episodeNo, id = info.id,
                        translationType = translationType
                    ))
                    Action.DOWNLOAD -> downloadFile(context, url, filename)
                    null -> TODO()
                }
            }, onDismissRequest = {
                screenState = ScreenState.WAIT_FOR_ACTION
            })
        }

        fun getLinks() {
            screenState = ScreenState.GET_LINKS
            AnimeApi.getSourceUrls(
                id = info.id,
                episodeNo = episodeNo,
                translationType = translationType
            ) { response ->
                val sourceUrls = JsonParser.parseSourceUrls(response)
                links.clear()

                sourceUrls.forEach {
                    val decoded = decodeSourceUrl(it)
                    val job = scope.launch(Dispatchers.IO) {
                        val response = AnimeApi.getEpisodeLink(decoded)
                        val episodeLink = JsonParser.parseEpisodeLink(response)
                        when {
                            episodeLink.link.contains("repackager.wixmp.com") -> links.addAll(extractWixmpLinks(episodeLink.link))
                            episodeLink.link.contains("vipanicdn") || episodeLink.link.contains("anifastcdn") -> TODO()
                            else -> links.add(episodeLink)
                        }
                    }
                    jobs.add(job)
                }
                scope.launch {
                    jobs.joinAll()
                    qualities.clear()
                    links.forEach { qualities.add(it.resolutionStr) }
                    screenState = ScreenState.CHOOSE_QUALITY
                }
            }
        }

        if (screenState == ScreenState.CHOOSE_ACTION) DialogComponent(
            title = "What do you want to do?",
            leftButtonText = "Download", onLeftButtonClick = {
                getLinks()
                action = Action.DOWNLOAD
            },
            rightButtonText = "Play", onRightButtonClick = {
                getLinks()
                action = Action.PLAY
            },
            onDismissRequest = { screenState = ScreenState.WAIT_FOR_ACTION }
        )
    }
}