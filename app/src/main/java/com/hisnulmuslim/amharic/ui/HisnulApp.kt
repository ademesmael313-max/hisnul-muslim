package com.hisnulmuslim.amharic.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hisnulmuslim.amharic.BookViewModel
import com.hisnulmuslim.amharic.data.AppSettings
import com.hisnulmuslim.amharic.ui.screens.AboutScreen
import com.hisnulmuslim.amharic.ui.screens.ChapterScreen
import com.hisnulmuslim.amharic.ui.screens.FavoritesScreen
import com.hisnulmuslim.amharic.ui.screens.HomeScreen
import com.hisnulmuslim.amharic.ui.screens.SettingsScreen
import com.hisnulmuslim.amharic.ui.screens.TasbihScreen
import com.hisnulmuslim.amharic.ui.theme.HisnulMuslimTheme

private object Routes {
    const val HOME = "home"
    const val CHAPTER = "chapter/{chapterId}"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val ABOUT = "about"
    const val TASBIH = "tasbih"

    fun chapter(id: Int) = "chapter/$id"
}

@Composable
fun HisnulApp(
    appVersion: String,
    viewModel: BookViewModel = viewModel(),
) {
    val settings by viewModel.settings.collectAsState()
    val darkTheme = when (settings.themeMode) {
        AppSettings.THEME_LIGHT -> false
        AppSettings.THEME_DARK -> true
        else -> isSystemInDarkTheme()
    }

    HisnulMuslimTheme(darkTheme = darkTheme) {
        val navController = rememberNavController()
        val book = viewModel.book

        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    book = book,
                    settings = settings,
                    duaOfTheDay = book.allDuas.firstOrNull { it.id == viewModel.duaOfTheDayId },
                    onOpenChapter = { navController.navigate(Routes.chapter(it)) },
                    onToggleFavorite = viewModel::toggleFavorite,
                    onCount = { viewModel.incrementCounter(it.id, it.repeat) },
                    onResetCount = viewModel::resetCounter,
                    onOpenFavorites = { navController.navigate(Routes.FAVORITES) },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                    onOpenTasbih = { navController.navigate(Routes.TASBIH) },
                )
            }

            composable(
                route = Routes.CHAPTER,
                arguments = listOf(navArgument("chapterId") { type = NavType.IntType }),
            ) { entry ->
                val chapterId = entry.arguments?.getInt("chapterId") ?: 0
                val chapter = book.chapter(chapterId)
                if (chapter == null) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "ምዕራፉ አልተገኘም",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                } else {
                    ChapterScreen(
                        book = book,
                        chapter = chapter,
                        settings = settings,
                        onBack = { navController.popBackStack() },
                        onOpenChapter = { navController.navigate(Routes.chapter(it)) },
                        onToggleFavorite = viewModel::toggleFavorite,
                        onCount = { viewModel.incrementCounter(it.id, it.repeat) },
                        onResetCount = viewModel::resetCounter,
                    )
                }
            }

            composable(Routes.FAVORITES) {
                FavoritesScreen(
                    book = book,
                    settings = settings,
                    onBack = { navController.popBackStack() },
                    onToggleFavorite = viewModel::toggleFavorite,
                    onCount = { viewModel.incrementCounter(it.id, it.repeat) },
                    onResetCount = viewModel::resetCounter,
                )
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    settings = settings,
                    versionName = appVersion,
                    onBack = { navController.popBackStack() },
                    onThemeMode = viewModel::setThemeMode,
                    onArabicSize = viewModel::setArabicSize,
                    onAmharicSize = viewModel::setAmharicSize,
                    onShowArabic = viewModel::setShowArabic,
                    onShowTranslation = viewModel::setShowTranslation,
                    onResetCounters = viewModel::resetAllCounters,
                    onClearFavorites = viewModel::clearFavorites,
                    onOpenAbout = { navController.navigate(Routes.ABOUT) },
                )
            }

            composable(Routes.ABOUT) {
                AboutScreen(
                    appVersion = appVersion,
                    onBack = { navController.popBackStack() },
                )
            }

            composable(Routes.TASBIH) {
                TasbihScreen(
                    count = settings.tasbihCount,
                    onCount = { target ->
                        val next = (settings.tasbihCount + 1).coerceAtMost(target)
                        viewModel.setTasbihCount(next)
                    },
                    onReset = { viewModel.setTasbihCount(0) },
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
