package com.galib.kaizokuani.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.galib.kaizokuani.data.AppData
import com.galib.kaizokuani.ui.AnimeDetailsScreen
import com.galib.kaizokuani.ui.AnimeSearchScreen
import com.galib.kaizokuani.ui.components.LoadingPageComponent
import com.galib.kaizokuani.ui.components.PopularAnimeResultsComponent
import com.galib.kaizokuani.ui.components.SearchBarComponent
import com.galib.kaizokuani.ui.viewmodels.ExploreScreenViewModel

private val categories = listOf<String>(
    "All Time", "Daily", "Weekly", "Monthly"
)

@Composable
fun ExploreScreenComposable(navController: NavController, viewModel: ExploreScreenViewModel) {
    val results = viewModel.popularAnimeResults.collectAsState().value
    val context = LocalContext.current
    var showEnglishName by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        AppData.getShowEnglishName(context).collect {
            showEnglishName = it
        }
    }

    Column {
        SearchBarComponent() { query ->
            navController.navigate(route = AnimeSearchScreen(query = query))
        }
        if (results.any { it == null }) LoadingPageComponent()
        else {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                categories.forEachIndexed { index, category ->
                    results[index]?.let { result ->
                        Text(text = "Popular Anime: $category")
                        PopularAnimeResultsComponent(result, showEnglishName) {
                            navController.navigate(route = AnimeDetailsScreen(id = it))
                        }
                    }
                }
            }
        }
    }
}