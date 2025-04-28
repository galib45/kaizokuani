package com.galib.kaizokuani.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.galib.kaizokuani.data.AppDataManager

@Composable
fun SearchHistoryComponent(
    onSearchItemClick: (String) -> Unit
) {
    var searchHistory by remember { mutableStateOf<Set<String>>(emptySet()) }

    // Collect search history using LaunchedEffect
    LaunchedEffect(Unit) {
        searchHistory = AppDataManager.appData.value.searchHistory
    }

    Column {
        searchHistory.forEach { item ->
            SearchHistoryItemComponent(
                item,
                onClick =  { onSearchItemClick(item) },
                onRemoveClick = {
                    val temp = searchHistory.toMutableSet()
                    temp.remove(item)
                    searchHistory = temp
                    AppDataManager.removeHistory(item)
                }
            )
        }
        if (searchHistory.isNotEmpty()) {
            Text(
                text = "Clear History",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clickable(onClick = {
                        searchHistory = emptySet()
                        AppDataManager.clearHistory()
                    })
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp),
            )
        }
    }
}