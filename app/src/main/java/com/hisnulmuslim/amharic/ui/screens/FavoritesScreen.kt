package com.hisnulmuslim.amharic.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hisnulmuslim.amharic.R
import com.hisnulmuslim.amharic.data.AppSettings
import com.hisnulmuslim.amharic.data.Dua
import com.hisnulmuslim.amharic.data.DuaBook
import com.hisnulmuslim.amharic.ui.components.DuaCard
import com.hisnulmuslim.amharic.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    book: DuaBook,
    settings: AppSettings,
    onBack: () -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onCount: (Dua) -> Unit,
    onResetCount: (Int) -> Unit,
) {
    val favorites = book.allDuas.filter { settings.favorites.contains(it.id) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.favorites),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 12.dp,
                bottom = innerPadding.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (favorites.isEmpty()) {
                item(key = "empty") {
                    EmptyState(
                        title = stringResource(R.string.favorites_empty),
                        hint = stringResource(R.string.favorites_empty_hint),
                    )
                }
            } else {
                items(items = favorites, key = { dua: Dua -> dua.id }) { dua ->
                    DuaCard(
                        dua = dua,
                        settings = settings,
                        isFavorite = true,
                        count = settings.counters[dua.id] ?: 0,
                        onToggleFavorite = { onToggleFavorite(dua.id) },
                        onCount = { onCount(dua) },
                        onResetCount = { onResetCount(dua.id) },
                        showChapter = true,
                    )
                }
            }
        }
    }
}
