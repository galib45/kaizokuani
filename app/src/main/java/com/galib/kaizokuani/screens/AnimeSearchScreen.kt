package com.galib.kaizokuani.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.galib.kaizokuani.AnimeApi
import com.galib.kaizokuani.AnimeDetails
import com.galib.kaizokuani.AnimeSearch
import com.galib.kaizokuani.AnimeSearchResult
import com.galib.kaizokuani.AppData
import com.galib.kaizokuani.JsonParser
import com.galib.kaizokuani.components.LoadingPageComponent
import com.galib.kaizokuani.components.SearchBarComponent
import com.galib.kaizokuani.components.SearchResultItemComponent
import kotlinx.coroutines.launch

@Composable
fun AnimeSearchScreen(
    animeSearch: AnimeSearch,
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
    val context = LocalContext.current

    suspend fun search(query: String) {
        AppData.addHistory(context, query)
        searching = true
        AnimeApi.searchAnime(query) { response ->
            results = JsonParser.parseAnimeSearchResult(response)
            searching = false
        }
    }

    LaunchedEffect(Unit) {
        animeSearch.query?.let { query -> search(query) }
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
                    navController.navigate(route = AnimeDetails(id = id))
                }
            }
        }
    }
}