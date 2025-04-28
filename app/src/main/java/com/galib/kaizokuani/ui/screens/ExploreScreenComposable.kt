package com.galib.kaizokuani.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.galib.kaizokuani.data.AppDataManager
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
                        PopularAnimeResultsComponent(result, AppDataManager.appData.collectAsState().value.showEnglishName) {
                            navController.navigate(route = AnimeDetailsScreen(id = it))
                        }
                    }
                }
            }
        }
    }
}