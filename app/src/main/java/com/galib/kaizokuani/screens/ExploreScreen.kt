package com.galib.kaizokuani.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.galib.kaizokuani.AnimeDetails
import com.galib.kaizokuani.AnimeSearch
import com.galib.kaizokuani.AppData
import com.galib.kaizokuani.PopularAnimeResult
import com.galib.kaizokuani.components.PopularAnimeResultsComponent
import com.galib.kaizokuani.components.SearchBarComponent

val ALL_TIME: Int = 0
val DAILY: Int = 1
val WEEKLY: Int = 7
val MONTHLY: Int = 30

@Composable
fun ExploreScreen(navController: NavController, results: List<List<PopularAnimeResult>>) {
    val context = LocalContext.current

    var showEnglishName by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        AppData.getShowEnglishName(context).collect {
            showEnglishName = it
        }
    }

    Column {
        SearchBarComponent() { query ->
            navController.navigate(route = AnimeSearch(query = query))
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = "Popular Anime: All Time")
            PopularAnimeResultsComponent(results[0], showEnglishName) {
                navController.navigate(route = AnimeDetails(id = it))
            }

            Text(text = "Popular Anime: Daily")
            PopularAnimeResultsComponent(results[1], showEnglishName) {
                navController.navigate(route = AnimeDetails(id = it))
            }

            Text(text = "Popular Anime: Weekly")
            PopularAnimeResultsComponent(results[2], showEnglishName) {
                navController.navigate(route = AnimeDetails(id = it))
            }

            Text(text = "Popular Anime: Monthly")
            PopularAnimeResultsComponent(results[3], showEnglishName) {
                navController.navigate(route = AnimeDetails(id = it))
            }
        }
    }

}