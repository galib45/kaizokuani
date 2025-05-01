package com.galib.kaizokuani.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "appdata")

object AppDataManager {
    // Define the DataStore extension property
    private lateinit var dataStore: DataStore<Preferences>

    // Define the key for search history
    private val APP_DATA_KEY = stringPreferencesKey("app_data")

    private val _appData = MutableStateFlow(AppData())
    val appData = _appData.asStateFlow()

    fun init(context: Context) {
        dataStore = context.dataStore
    }

    suspend fun loadFromDataStore() {
        val jsonFlow = dataStore.data.map { data ->
            data[APP_DATA_KEY] ?: "{}"
        }

        jsonFlow.collect { jsonString ->
            _appData.value = JsonParser.json.decodeFromString<AppData>(jsonString)
        }
    }

    suspend fun saveToDataStore() {
        dataStore.edit { data ->
            data[APP_DATA_KEY] = JsonParser.json.encodeToString(_appData.value)
        }
    }

    fun toggleShowEnglishName() {
        _appData.value.showEnglishName = _appData.value.showEnglishName.not()
    }

    fun removeHistory(item: String) {
        _appData.value.searchHistory.remove(item)
    }

    fun clearHistory() {
        _appData.value.searchHistory.clear()
    }

    fun addProgressEntry(id: String, episodeNo: String, translationType: String, contentPosition: Long) {
        _appData.update { currentData ->
            val animeProgressData = currentData.animeProgressData
            var animeProgress = animeProgressData.get(id) ?: AnimeProgress()
            if (translationType == "dub") {
                animeProgress.playedEpisodesDub.add(episodeNo)
                animeProgress.lastPlayedDub = LastPlayedEpisode(episodeNo, contentPosition)
            } else {
                animeProgress.playedEpisodesSub.add(episodeNo)
                animeProgress.lastPlayedSub = LastPlayedEpisode(episodeNo, contentPosition)
            }
            animeProgressData.put(id, animeProgress)
            currentData.copy(animeProgressData = animeProgressData)
        }
    }
}

//object AnimeProgressData {
//    var data: HashMap<String, AnimeProgress> = hashMapOf()
//
//    fun addEntry(id: String, episodeNo: String, translationType: String, currentPosition: Long) {
//        var animeProgress = data.get(id) ?: AnimeProgress()
//        if (translationType == "dub") {
//            animeProgress.playedEpisodesDub.add(episodeNo)
//            animeProgress.lastPlayedDub = LastPlayedEpisode(episodeNo, currentPosition)
//        } else {
//            animeProgress.playedEpisodesSub.add(episodeNo)
//            animeProgress.lastPlayedSub = LastPlayedEpisode(episodeNo, currentPosition)
//        }
//        data.put(id, animeProgress)
//    }
//
//    suspend fun loadFromAppData(context: Context) {
//        AppDataManager.getAnimeProgress(context).collect { value ->
//            data = value
//        }
//    }
//
//    suspend fun saveToAppData(context: Context) {
//        AppDataManager.updateAnimeProgress(context, data)
//    }
//
//    fun getEntry(id: String) : AnimeProgress {
//        return data.get(id) ?: AnimeProgress()
//    }
//}