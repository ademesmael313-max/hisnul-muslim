package com.hisnulmuslim.amharic.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hisnulmuslim.amharic.R
import com.hisnulmuslim.amharic.data.AppSettings
import com.hisnulmuslim.amharic.data.Chapter
import com.hisnulmuslim.amharic.data.Dua
import com.hisnulmuslim.amharic.data.DuaBook
import com.hisnulmuslim.amharic.ui.components.CategoryCard
import com.hisnulmuslim.amharic.ui.components.ChapterRow
import com.hisnulmuslim.amharic.ui.components.DuaCard
import com.hisnulmuslim.amharic.ui.components.EmptyState
import com.hisnulmuslim.amharic.ui.components.SectionHeader
import com.hisnulmuslim.amharic.ui.components.contentSidePadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    book: DuaBook,
    settings: AppSettings,
    duaOfTheDay: Dua?,
    onOpenChapter: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onCount: (Dua) -> Unit,
    onResetCount: (Int) -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTasbih: () -> Unit,
) {
    val sidePadding = contentSidePadding()
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }

    val results = remember(query) { book.search(query) }
    val chapters = remember(selectedCategory) {
        selectedCategory?.let { book.chaptersOf(it) } ?: book.chapters
    }
    val chapterCountByCategory = remember {
        book.categories.associate { category -> category.id to book.chaptersOf(category.id).size }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.home_title),
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = stringResource(R.string.home_subtitle),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenFavorites) {
                        Icon(
                            imageVector = Icons.Filled.BookmarkBorder,
                            contentDescription = stringResource(R.string.favorites),
                        )
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onOpenTasbih,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                icon = { Icon(Icons.Filled.Timer, contentDescription = null) },
                text = { Text(stringResource(R.string.tasbih)) },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                start = sidePadding,
                end = sidePadding,
                top = innerPadding.calculateTopPadding() + 12.dp,
                bottom = innerPadding.calculateBottomPadding() + 96.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "search") {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.search_hint)) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(
                                    Icons.Filled.Clear,
                                    contentDescription = stringResource(R.string.clear_search),
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                )
            }

            if (query.isBlank()) {
                if (duaOfTheDay != null) {
                    item(key = "daily") {
                        DuaOfTheDayCard(
                            dua = duaOfTheDay,
                            isFavorite = settings.favorites.contains(duaOfTheDay.id),
                            onClick = { onOpenChapter(duaOfTheDay.chapterId) },
                            onToggleFavorite = { onToggleFavorite(duaOfTheDay.id) },
                        )
                    }
                }

                item(key = "categories-title") {
                    SectionHeader(
                        title = stringResource(R.string.categories),
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }

                items(
                    items = book.categories.chunked(2),
                    key = { row -> row.joinToString("-") { it.id.toString() } },
                ) { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        row.forEach { category ->
                            CategoryCard(
                                category = category,
                                chapterCount = chapterCountByCategory[category.id] ?: 0,
                                selected = selectedCategory == category.id,
                                onClick = {
                                    selectedCategory =
                                        if (selectedCategory == category.id) null else category.id
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }

                item(key = "chapters-title") {
                    SectionHeader(
                        title = if (selectedCategory == null) {
                            stringResource(R.string.all_chapters)
                        } else {
                            book.category(selectedCategory!!)?.title ?: ""
                        },
                        trailing = stringResource(
                            R.string.chapters_count,
                            chapters.size,
                            chapters.sumOf { it.duas.size },
                        ),
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }

                items(items = chapters, key = { chapter: Chapter -> "chapter-${chapter.id}" }) { chapter ->
                    Column {
                        ChapterRow(
                            chapter = chapter,
                            categoryTitle = book.category(chapter.categoryId)?.title ?: "",
                            onClick = { onOpenChapter(chapter.id) },
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            } else {
                item(key = "results-title") {
                    SectionHeader(
                        title = stringResource(R.string.search_results),
                        trailing = stringResource(R.string.duas_count, results.size),
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
                if (results.isEmpty()) {
                    item(key = "no-results") {
                        EmptyState(
                            title = stringResource(R.string.no_results),
                            hint = stringResource(R.string.no_results_hint),
                        )
                    }
                } else {
                    items(items = results, key = { dua: Dua -> "result-${dua.id}" }) { dua ->
                        DuaCard(
                            dua = dua,
                            settings = settings,
                            isFavorite = settings.favorites.contains(dua.id),
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
}

@Composable
private fun DuaOfTheDayCard(
    dua: Dua,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.dua_of_the_day),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = stringResource(
                            if (isFavorite) R.string.favorite_remove else R.string.favorite_add
                        ),
                        tint = MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = if (isFavorite) 1f else 0.75f
                        ),
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = dua.chapterTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = dua.amharic,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.92f),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "ንበብ →",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}
