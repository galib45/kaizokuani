package com.galib.kaizokuani.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .clickable(onClick = onClearHistoryClick),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Outlined.Clear, contentDescription = "")
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Clear History")
            }
        }
    }
}