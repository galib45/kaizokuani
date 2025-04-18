package com.galib.kaizokuani.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchHistoryComponent(
    searchHistory: Set<String>, onSearchItemClick: (String) -> Unit,
    onClearHistoryClick: () -> Unit
) {
    LazyColumn {
        items(searchHistory.toList()) { item ->
            SearchHistoryItemComponent(item) {
                onSearchItemClick(item)
            }
        }
        if (searchHistory.isNotEmpty()) item {
            Text(
                text = "Clear History",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clickable(onClick = onClearHistoryClick)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp),
            )
        }
    }
}