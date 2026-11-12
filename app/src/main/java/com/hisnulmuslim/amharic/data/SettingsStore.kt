package com.hisnulmuslim.amharic.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class AppSettings(
    val themeMode: String = "system",
    val arabicSize: Float = 26f,
    val amharicSize: Float = 16f,
    val showArabic: Boolean = true,
    val showTranslation: Boolean = true,
    val favorites: Set<Int> = emptySet(),
    val counters: Map<Int, Int> = emptyMap(),
    val tasbihCount: Int = 0,
) {
    companion object {
        const val THEME_SYSTEM = "system"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
    }
}

class SettingsStore(context: Context) {

    private val store = context.applicationContext.settingsDataStore

    val settings: Flow<AppSettings> = store.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            AppSettings(
                themeMode = prefs[Keys.THEME] ?: AppSettings.THEME_SYSTEM,
                arabicSize = prefs[Keys.ARABIC_SIZE] ?: 26f,
                amharicSize = prefs[Keys.AMHARIC_SIZE] ?: 16f,
                showArabic = prefs[Keys.SHOW_ARABIC] ?: true,
                showTranslation = prefs[Keys.SHOW_TRANSLATION] ?: true,
                favorites = prefs[Keys.FAVORITES]?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet(),
                counters = decodeCounters(prefs[Keys.COUNTERS]),
                tasbihCount = prefs[Keys.TASBIH] ?: 0,
            )
        }

    suspend fun setThemeMode(mode: String) = store.edit { it[Keys.THEME] = mode }

    suspend fun setArabicSize(size: Float) = store.edit { it[Keys.ARABIC_SIZE] = size }

    suspend fun setAmharicSize(size: Float) = store.edit { it[Keys.AMHARIC_SIZE] = size }

    suspend fun setShowArabic(show: Boolean) = store.edit { it[Keys.SHOW_ARABIC] = show }

    suspend fun setShowTranslation(show: Boolean) = store.edit { it[Keys.SHOW_TRANSLATION] = show }

    suspend fun toggleFavorite(duaId: Int) = store.edit { prefs ->
        val favorites = prefs[Keys.FAVORITES]?.toMutableSet() ?: mutableSetOf()
        if (!favorites.add(duaId.toString())) favorites.remove(duaId.toString())
        prefs[Keys.FAVORITES] = favorites
    }

    suspend fun clearFavorites() = store.edit { it.remove(Keys.FAVORITES) }

    /** Increments the tap counter of a dua, capped at [target]. */
    suspend fun incrementCounter(duaId: Int, target: Int) = store.edit { prefs ->
        val counters = decodeCounters(prefs[Keys.COUNTERS]).toMutableMap()
        val next = ((counters[duaId] ?: 0) + 1).coerceAtMost(target)
        counters[duaId] = next
        prefs[Keys.COUNTERS] = encodeCounters(counters)
    }

    suspend fun resetCounter(duaId: Int) = store.edit { prefs ->
        val counters = decodeCounters(prefs[Keys.COUNTERS]).toMutableMap()
        counters.remove(duaId)
        prefs[Keys.COUNTERS] = encodeCounters(counters)
    }

    suspend fun resetAllCounters() = store.edit { it.remove(Keys.COUNTERS) }

    suspend fun setTasbihCount(value: Int) = store.edit { it[Keys.TASBIH] = value.coerceAtLeast(0) }

    private fun decodeCounters(raw: String?): Map<Int, Int> {
        if (raw.isNullOrBlank()) return emptyMap()
        val result = LinkedHashMap<Int, Int>()
        raw.split(',').forEach { entry ->
            val parts = entry.split(':')
            if (parts.size == 2) {
                val id = parts[0].toIntOrNull()
                val count = parts[1].toIntOrNull()
                if (id != null && count != null) result[id] = count
            }
        }
        return result
    }

    private fun encodeCounters(counters: Map<Int, Int>): String =
        counters.entries.joinToString(",") { "${it.key}:${it.value}" }

    private object Keys {
        val THEME = stringPreferencesKey("theme_mode")
        val ARABIC_SIZE = floatPreferencesKey("arabic_size")
        val AMHARIC_SIZE = floatPreferencesKey("amharic_size")
        val SHOW_ARABIC = booleanPreferencesKey("show_arabic")
        val SHOW_TRANSLATION = booleanPreferencesKey("show_translation")
        val FAVORITES = stringSetPreferencesKey("favorites")
        val COUNTERS = stringPreferencesKey("counters")
        val TASBIH = intPreferencesKey("tasbih_count")
    }
}
