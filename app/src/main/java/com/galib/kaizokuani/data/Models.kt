package com.galib.kaizokuani.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnimeSearchResult (
    @SerialName("_id") val id: String,
    val name: String,
    val englishName: String?
)

@Serializable
data class ShowInfo(
    @SerialName("_id") val id: String,
    val name: String,
    val englishName: String?,
    val status: String?,
    val description: String?,
    val thumbnail: String?,
    val score: Double?,
    val genres: List<String>,
    val tags: List<String>?,
    val season: Season?,
    val rating: String?,
    val episodeCount: String?,
    val episodeDuration: String?,
    val availableEpisodesDetail: AvailableEpisodesDetail?
)

@Serializable
data class Season(
    val quarter: String,
    val year: Int
)

@Serializable
data class AvailableEpisodesDetail(
    val sub: List<String>,
    val dub: List<String>,
    val raw: List<String>
)

@Serializable
data class EpisodeLink(
    val link: String,
    val resolutionStr: String
)

@Serializable
data class PopularAnimeResult(
    @SerialName("_id") val id: String,
    val name: String,
    val englishName: String?,
    val thumbnail: String?
)

@Serializable
data class LastPlayedEpisode(
    val name: String,
    val contentPosition: Long
)

@Serializable
data class AnimeProgress(
    var lastPlayedSub: LastPlayedEpisode? = null,
    var lastPlayedDub: LastPlayedEpisode? = null,
    var playedEpisodesSub: MutableSet<String> = mutableSetOf(),
    var playedEpisodesDub: MutableSet<String> = mutableSetOf()
)

@Serializable
data class AppData(
    val searchHistory: MutableSet<String> = mutableSetOf(),
    var showEnglishName: Boolean = false,
)