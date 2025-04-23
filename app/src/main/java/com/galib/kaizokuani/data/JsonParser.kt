package com.galib.kaizokuani.data

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

object JsonParser {
    val json = Json {
        ignoreUnknownKeys = true
    }

    fun parseAnimeSearchResult(jsonString: String) : List<AnimeSearchResult> {
        var results = mutableListOf<AnimeSearchResult>()
        val root = json.parseToJsonElement(jsonString) as? JsonObject
        val data = root?.get("data") as? JsonObject
        val shows = data?.get("shows") as? JsonObject
        val edges = shows?.get("edges") as? JsonArray
        edges?.forEach { edge ->
            val result = json.decodeFromString<AnimeSearchResult>(json.encodeToString(edge))
            results.add(result)
        }
        return results
    }

    fun parseAnimeInfo(jsonString: String) : ShowInfo {
        val root = json.parseToJsonElement(jsonString) as? JsonObject
        val data = root?.get("data") as? JsonObject
        val show = data?.get("show") as? JsonObject
        return json.decodeFromString<ShowInfo>(json.encodeToString(show))
    }

    fun parseSourceUrls(jsonString: String) : List<String> {
        var result = mutableListOf<String>()
        val root = json.parseToJsonElement(jsonString) as? JsonObject
        val data = root?.get("data") as? JsonObject
        val episode = data?.get("episode") as? JsonObject
        val sourceUrls = episode?.get("sourceUrls") as? JsonArray
        sourceUrls?.forEach {
            val item = it as? JsonObject
            val sourceName = item?.get("sourceName")?.jsonPrimitive?.content
            if (sourceName == "Default" || sourceName == "Sak"
                || sourceName == "Kir" || sourceName == "S-mp4"
                || sourceName == "Luf-mp4") {
                val sourceUrl = item.get("sourceUrl")?.jsonPrimitive?.content?.drop(2).toString()
                result.add(sourceUrl)
            }
        }
        return result
    }

    fun parseEpisodeLink(jsonString: String) : EpisodeLink {
        val root = json.parseToJsonElement(jsonString) as? JsonObject
        val links = root?.get("links") as? JsonArray
        return json.decodeFromString(json.encodeToString(links?.get(0)))
    }

    fun parsePopularAnimeResult(jsonString: String): List<PopularAnimeResult> {
        var results = mutableListOf<PopularAnimeResult>()
        val root = json.parseToJsonElement(jsonString) as? JsonObject
        val data = root?.get("data") as? JsonObject
        val queryPopular = data?.get("queryPopular") as? JsonObject
        val recommendations = queryPopular?.get("recommendations") as? JsonArray
        recommendations?.forEach {
            val recommendation = it as? JsonObject
            val anyCard = recommendation?.get("anyCard") as? JsonObject
            var result = json.decodeFromString<PopularAnimeResult>(json.encodeToString(anyCard))
            if (result.thumbnail?.startsWith("https") == false) {
                result = PopularAnimeResult(
                    id = result.id,
                    name = result.name,
                    englishName = result.englishName,
                    thumbnail = "https://wp.youtube-anime.com/aln.youtube-anime.com/${result.thumbnail}"
                )
            }
            results.add(result)
        }
        return results
    }
}