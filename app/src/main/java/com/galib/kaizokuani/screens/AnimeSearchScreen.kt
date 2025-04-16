package com.galib.kaizokuani.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.galib.kaizokuani.AnimeApi
import com.galib.kaizokuani.AnimeSearchResult
import com.galib.kaizokuani.History
import com.galib.kaizokuani.JsonParser
import com.galib.kaizokuani.components.LoadingPageComponent
import com.galib.kaizokuani.components.SearchBarComponent
import com.galib.kaizokuani.components.SearchResultItemComponent
import kotlinx.coroutines.launch

@Composable
fun AnimeSearchScreen(
    modifier: Modifier = Modifier,
    onNavigateToInfoScreen: (String) -> Unit,
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

    SearchBarComponent() { query ->
        scope.launch {
            History.addHistory(context, query)
            searching = true
            AnimeApi.searchAnime(query) { response ->
                results = JsonParser.parseAnimeSearchResult(response)
                searching = false
            }
        }
    }

    if (searching) LoadingPageComponent()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 136.dp)
    ) {
        items(results) { result ->
            SearchResultItemComponent(result, onNavigateToInfoScreen)
        }
    }
}