package com.galib.kaizokuani.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.galib.kaizokuani.icons.ExploreIcon
import com.galib.kaizokuani.icons.PersonIcon

@Composable
fun BottomNavigationComponent(onClick: (Int) -> Unit) {
    var selected by rememberSaveable { mutableStateOf(0) }
    val items = listOf<BottomNavigationItem>(
        BottomNavigationItem("Explore", "Search Anime", ExploreIcon),
        BottomNavigationItem("Profile", "History and Settings", PersonIcon)
    )
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selected == index,
                onClick = {
                    selected = index
                    onClick(index)
                },
                icon = { Icon(item.icon, contentDescription = item.description) },
                label = { Text(text = item.label) }
            )
        }
    }
}

data class BottomNavigationItem(
    val label: String,
    val description: String,
    val icon: ImageVector
)