package com.galib.kaizokuani

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object AppData {
    // Define the DataStore extension property
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    // Define the key for search history
    private val SEARCH_HISTORY = stringSetPreferencesKey("search_history")
    private val SHOW_ENGLISH_NAME = booleanPreferencesKey("show_english_name")

    suspend fun setShowEnglishName(context: Context, value: Boolean) {
        context.dataStore.edit { appData ->
            appData[SHOW_ENGLISH_NAME] = value
        }
    }

    fun getShowEnglishName(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { appData ->
            appData[SHOW_ENGLISH_NAME] ?: false
        }
    }

    // Function to add an item to the search history
    suspend fun addHistory(context: Context, item: String) {
        context.dataStore.edit { appData ->
            // Get the current search history or initialize an empty mutable set
            val currentHistory = appData[SEARCH_HISTORY]?.toMutableSet() ?: mutableSetOf()
            currentHistory.add(item) // Add the new item
            appData[SEARCH_HISTORY] = currentHistory // Save the updated set back
        }
    }

    // Function to retrieve the search history as a Flow
    fun readHistory(context: Context): Flow<Set<String>> {
        return context.dataStore.data.map { appData ->
            appData[SEARCH_HISTORY] ?: emptySet()
        }
    }

    suspend fun removeHistoryItem(context: Context, item: String) {
        context.dataStore.edit { appData ->
            // Get the current search history or initialize an empty mutable set
            val currentHistory = appData[SEARCH_HISTORY]?.toMutableSet() ?: mutableSetOf()
            currentHistory.remove(item) // Add the new item
            appData[SEARCH_HISTORY] = currentHistory // Save the updated set back
        }
    }

    // Optional: Function to clear the search history
    suspend fun clearHistory(context: Context) {
        context.dataStore.edit { appData ->
            appData[SEARCH_HISTORY] = emptySet()
        }
    }

}