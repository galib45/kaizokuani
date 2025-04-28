package com.galib.kaizokuani.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.galib.kaizokuani.data.AnimeApi
import com.galib.kaizokuani.data.AnimeSearchResult
import com.galib.kaizokuani.data.AppDataManager
import com.galib.kaizokuani.data.JsonParser
import com.galib.kaizokuani.ui.AnimeDetailsScreen
import com.galib.kaizokuani.ui.AnimeSearchScreen
import com.galib.kaizokuani.ui.components.LoadingPageComponent
import com.galib.kaizokuani.ui.components.SearchBarComponent
import com.galib.kaizokuani.ui.components.SearchResultItemComponent
import kotlinx.coroutines.launch

@Composable
fun AnimeSearchScreenComposable(
    animeSearchScreen: AnimeSearchScreen,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val animeSearchResultSaver = Saver<List<AnimeSearchResult>, String>(
        save = { JsonParser.json.encodeToString(it) },
        restore = { JsonParser.json.decodeFromString<List<AnimeSearchResult>>(it) }
    )
    var results by rememberSaveable(stateSaver = animeSearchResultSaver) { mutableStateOf<List<AnimeSearchResult>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }

    fun search(query: String) {
        AppDataManager.appData.value.searchHistory.add(query)
        searching = true
        AnimeApi.searchAnime(query) { response ->
            results = JsonParser.parseAnimeSearchResult(response)
            searching = false
        }
    }

    LaunchedEffect(Unit) {
        animeSearchScreen.query?.let { query -> search(query) }
    }

    if (searching) LoadingPageComponent()

    Column {
        SearchBarComponent() { query ->
            scope.launch { search(query) }
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
        ) {
            items(results) { result ->
                SearchResultItemComponent(result) { id ->
                    navController.navigate(route = AnimeDetailsScreen(id = id))
                }
            }
        }
    }
}