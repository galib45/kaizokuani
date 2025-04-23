package com.galib.kaizokuani.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galib.kaizokuani.data.AnimeApi
import com.galib.kaizokuani.data.JsonParser
import com.galib.kaizokuani.data.PopularAnimeResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val dateRanges = listOf<Int>(
    0, 1, 7, 30
)

class ExploreScreenViewModel : ViewModel() {
    private val _popularAnimeResults =
        MutableStateFlow<Array<List<PopularAnimeResult>?>>(arrayOf(null, null, null, null))
    val popularAnimeResults = _popularAnimeResults.asStateFlow()

    init {
        dateRanges.forEachIndexed { index, dateRange ->
            viewModelScope.launch {
                AnimeApi.getPopularAnime(dateRange) { response ->
                    val parsedResult = JsonParser.parsePopularAnimeResult(response)

                    val updatedResults = _popularAnimeResults.value.copyOf().apply {
                        this[index] = parsedResult
                    }

                    _popularAnimeResults.value = updatedResults
                }
            }
        }
    }

    fun getPopularAnimeResult(index: Int) : List<PopularAnimeResult>? {
        return _popularAnimeResults.value[index]
    }
}