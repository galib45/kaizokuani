package com.galib.kaizokuani.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.galib.kaizokuani.data.PopularAnimeResult
import com.galib.ui.theme.AppTypography

@Composable
fun PopularAnimeResultsComponent(
    results: List<PopularAnimeResult>,
    showEnglishName: Boolean,
    onClick: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(results) { item ->
            Card(
                modifier = Modifier
                    .size(100.dp, 200.dp)
                    .clickable(onClick = { onClick(item.id) })
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    AsyncImage(
                        model = item.thumbnail,
                        contentDescription = "thumbnail of ${item.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp, 150.dp)
                    )
                    Text(
                        text = if (showEnglishName) item.englishName ?: item.name else item.name,
                        style = AppTypography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}