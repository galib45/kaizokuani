package com.galib.kaizokuani.ui.components

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.galib.kaizokuani.data.AnimeSearchResult
import com.galib.kaizokuani.data.AppData

@Composable
fun SearchResultItemComponent(item: AnimeSearchResult, onNavigateToInfoScreen: (String) -> Unit) {
    var showEnglishName by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        AppData.getShowEnglishName(context).collect {
            showEnglishName = it
        }
    }

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
            text = if (showEnglishName) item.englishName ?: item.name else item.name,
            style = MaterialTheme.typography.bodyLarge, // Use predefined typography
            color = MaterialTheme.colorScheme.onBackground, // Use theme-defined text color
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Divider between items
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant, // Subtle divider color
            thickness = 1.dp
        )
    }
}