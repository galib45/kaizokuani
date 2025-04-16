package com.galib.kaizokuani.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.galib.kaizokuani.History
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarComponent(onSearch: (String) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var searchHistory by remember { mutableStateOf<Set<String>>(emptySet()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Collect search history using LaunchedEffect
    LaunchedEffect(Unit) {
        History.readHistory(context).collect { searchHistory = it }
    }

    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(if (isExpanded) 0.dp else 16.dp),
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = { query = it },
                onSearch = {
                    isExpanded = false
                    onSearch(query)
                },
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it },
                placeholder = { Text(text = "Search Anime") },
                leadingIcon = {
                    IconButton(
                        onClick = { isExpanded = !isExpanded }
                    ) {
                        if(isExpanded)
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                        else
                            Icon(Icons.Outlined.Search, contentDescription = "")
                    }
                },
                trailingIcon = {
                    if (isExpanded) IconButton(onClick = { query = "" }) {
                        Icon(Icons.Outlined.Clear, contentDescription = "")
                    }
                }
            )
        },
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {

        SearchHistoryComponent(
            searchHistory,
            onSearchItemClick = { item ->
                isExpanded = false
                onSearch(item)
            },
            onClearHistoryClick = {
                scope.launch {
                    History.clearHistory(context)
                }
            }
        )
    }
}