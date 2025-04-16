package com.galib.kaizokuani.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.galib.kaizokuani.AnimeSearchResult

@Composable
fun SearchResultItemComponent(item: AnimeSearchResult, onNavigateToInfoScreen: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable {
                onNavigateToInfoScreen(item.id)
            }
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyLarge, // Use predefined typography
            color = MaterialTheme.colorScheme.onBackground, // Use theme-defined text color
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Divider between items
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outline, // Subtle divider color
            thickness = 1.dp
        )
    }
}