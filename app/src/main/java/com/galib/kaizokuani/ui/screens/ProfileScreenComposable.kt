package com.galib.kaizokuani.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.galib.kaizokuani.data.AppData
import com.galib.ui.theme.AppTypography
import kotlinx.coroutines.launch

@Composable
fun ProfileScreenComposable(navController: NavController) {
    var showEnglishName by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        AppData.getShowEnglishName(context).collect {
            showEnglishName = it
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(text = "Settings", style = AppTypography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant, // Subtle divider color
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Show English Name")
            Switch(
                checked = showEnglishName,
                onCheckedChange = {
                    showEnglishName = showEnglishName.not()
                    scope.launch {
                        AppData.setShowEnglishName(context, showEnglishName)
                    }
                },
            )
        }
    }
}