package com.galib.kaizokuani

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object History {
    // Define the DataStore extension property
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    // Define the key for search history
    private val SEARCH_HISTORY = stringSetPreferencesKey("search_history")

    // Function to add an item to the search history
    suspend fun addHistory(context: Context, item: String) {
        context.dataStore.edit { settings ->
            // Get the current search history or initialize an empty mutable set
            val currentHistory = settings[SEARCH_HISTORY]?.toMutableSet() ?: mutableSetOf()
            currentHistory.add(item) // Add the new item
            settings[SEARCH_HISTORY] = currentHistory // Save the updated set back
        }
    }

    // Function to retrieve the search history as a Flow
    fun readHistory(context: Context): Flow<Set<String>> {
        return context.dataStore.data.map { settings ->
            settings[SEARCH_HISTORY] ?: emptySet()
        }
    }

    // Optional: Function to clear the search history
    suspend fun clearHistory(context: Context) {
        context.dataStore.edit { settings ->
            settings[SEARCH_HISTORY] = emptySet()
        }
    }

}