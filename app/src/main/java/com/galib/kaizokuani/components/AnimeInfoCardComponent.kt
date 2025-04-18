package com.galib.kaizokuani.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.galib.kaizokuani.ShowInfo
import com.galib.ui.theme.AppTypography

@Composable
fun AnimeInfoCardComponent(info: ShowInfo) {
    val availableEpisodes: Int = info.availableEpisodesDetail?.sub?.size ?: 0

    var episodesText = when(info.status) {
        "Finished" -> "$availableEpisodes/$availableEpisodes"
        else -> "$availableEpisodes/?"
    }

    Column {
        Text(style = AppTypography.bodyMedium, text = "Status: ${info.status ?: "?"}")
        Text(style = AppTypography.bodyMedium, text = "Score: ${info.score ?: "?"}")
        Text(style = AppTypography.bodyMedium, text = "Rating: ${info.rating ?: "?"}")
        Text(
            style = AppTypography.bodyMedium,
            text = "Season: ${info.season?.quarter}, ${info.season?.year}"
        )
        Text(
            style = AppTypography.bodyMedium,
            text = "Episodes: $episodesText"
        )
        Text(style = AppTypography.bodyMedium, text = "Duration: ${info.episodeDuration?.toInt()
            ?.div(60000)} min")
    }
}