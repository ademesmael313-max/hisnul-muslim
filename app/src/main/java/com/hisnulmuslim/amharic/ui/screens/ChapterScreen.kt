package com.hisnulmuslim.amharic.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hisnulmuslim.amharic.R
import com.hisnulmuslim.amharic.data.AppSettings
import com.hisnulmuslim.amharic.data.Chapter
import com.hisnulmuslim.amharic.data.Dua
import com.hisnulmuslim.amharic.data.DuaBook
import com.hisnulmuslim.amharic.ui.components.DuaCard
import com.hisnulmuslim.amharic.ui.components.contentSidePadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterScreen(
    book: DuaBook,
    chapter: Chapter,
    settings: AppSettings,
    onBack: () -> Unit,
    onOpenChapter: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onCount: (Dua) -> Unit,
    onResetCount: (Int) -> Unit,
) {
    val index = book.chapters.indexOfFirst { it.id == chapter.id }
    val previous = book.chapters.getOrNull(index - 1)
    val next = book.chapters.getOrNull(index + 1)
    val sidePadding = contentSidePadding()

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
                    Column {
                        Text(
                            text = chapter.title,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = "${book.category(chapter.categoryId)?.title ?: ""} · " +
                                stringResource(R.string.duas_count, chapter.duas.size),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = { previous?.let { onOpenChapter(it.id) } },
                        enabled = previous != null,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = "‹ " + (previous?.title ?: stringResource(R.string.previous_chapter)),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                    OutlinedButton(
                        onClick = { next?.let { onOpenChapter(it.id) } },
                        enabled = next != null,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = (next?.title ?: stringResource(R.string.next_chapter)) + " ›",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                start = sidePadding,
                end = sidePadding,
                top = innerPadding.calculateTopPadding() + 12.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(items = chapter.duas, key = { dua: Dua -> dua.id }) { dua ->
                DuaCard(
                    dua = dua,
                    settings = settings,
                    isFavorite = settings.favorites.contains(dua.id),
                    count = settings.counters[dua.id] ?: 0,
                    onToggleFavorite = { onToggleFavorite(dua.id) },
                    onCount = { onCount(dua) },
                    onResetCount = { onResetCount(dua.id) },
                )
            }
            item(key = "footer") {
                Column(Modifier.padding(vertical = 12.dp)) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "ሂስኑል ሙስሊም · ${book.chapters.size} ምዕራፍ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
