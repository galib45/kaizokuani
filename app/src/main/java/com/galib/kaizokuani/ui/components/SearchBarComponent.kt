package com.galib.kaizokuani.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarComponent(onSearch: (String) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isExpanded) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        SearchBar(
            modifier = Modifier.align(Alignment.Center),
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                dividerColor = MaterialTheme.colorScheme.outlineVariant
            ),
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
                            if (isExpanded)
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                            else
                                Icon(Icons.Outlined.Search, contentDescription = "")
                        }
                    },
                    trailingIcon = {
                        if (isExpanded) IconButton(onClick = { query = "" }) {
                            Icon(Icons.Outlined.Clear, contentDescription = "")
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.primary, // Use primary for cursor
                        focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant, // Use onSurfaceVariant
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        focusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            },
            expanded = isExpanded,
            onExpandedChange = { isExpanded = it }
        ) {

            SearchHistoryComponent(
                onSearchItemClick = { item ->
                    isExpanded = false
                    onSearch(item)
                }
            )
        }
    }
}