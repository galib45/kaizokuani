package com.galib.kaizokuani.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.galib.kaizokuani.data.AppDataManager
import com.galib.kaizokuani.data.ShowInfo
import com.galib.ui.theme.AppTypography

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EpisodesComponent(info: ShowInfo, onEpisodeClick: (String, String) -> Unit) {
    val animeProgress = AppDataManager.appData.collectAsState().value.animeProgressData.get(info.id)
    var bgColor = MaterialTheme.colorScheme.surfaceContainer
    var textColor = MaterialTheme.colorScheme.onSurface

    Text(style = AppTypography.bodyLarge, text = "Available Episodes")
    Text(style = AppTypography.bodySmall, text = "SUB")
    Spacer(modifier = Modifier.height(8.dp))
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        info.availableEpisodesDetail?.sub?.forEach { ep ->
            animeProgress?.let { progress ->
                if (progress.lastPlayedSub?.name == ep) {
                    bgColor = MaterialTheme.colorScheme.tertiaryContainer
                    textColor = MaterialTheme.colorScheme.onTertiaryContainer
                } else if (progress.playedEpisodesSub.contains(ep)) {
                    bgColor = MaterialTheme.colorScheme.primaryContainer
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    bgColor = MaterialTheme.colorScheme.surfaceContainer
                    textColor = MaterialTheme.colorScheme.onSurface
                }
            }
            Text(
                text = ep,
                color = textColor,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(size = 4.dp))
                    .background(bgColor)
                    .clickable(onClick = { onEpisodeClick(ep, "sub") })
                    .padding(6.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(style = AppTypography.bodySmall, text = "DUB")
    Spacer(modifier = Modifier.height(8.dp))
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        info.availableEpisodesDetail?.dub?.forEach { ep ->
            animeProgress?.let { progress ->
                if (progress.lastPlayedDub?.name == ep) {
                    bgColor = MaterialTheme.colorScheme.tertiaryContainer
                    textColor = MaterialTheme.colorScheme.onTertiaryContainer
                } else if (progress.playedEpisodesDub.contains(ep)) {
                    bgColor = MaterialTheme.colorScheme.primaryContainer
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    bgColor = MaterialTheme.colorScheme.surfaceContainer
                    textColor = MaterialTheme.colorScheme.onSurface
                }
            }
            Text(
                text = ep,
                color = textColor,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(size = 4.dp))
                    .background(bgColor)
                    .clickable(onClick = { onEpisodeClick(ep, "dub") })
                    .padding(6.dp)
            )
        }
    }
}