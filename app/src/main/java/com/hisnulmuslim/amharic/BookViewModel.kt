package com.hisnulmuslim.amharic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hisnulmuslim.amharic.data.AppSettings
import com.hisnulmuslim.amharic.data.DuaBook
import com.hisnulmuslim.amharic.data.DuaRepository
import com.hisnulmuslim.amharic.data.SettingsStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DuaRepository(application)
    private val settingsStore = SettingsStore(application)

    val book: DuaBook = repository.load()

    val settings: StateFlow<AppSettings> = settingsStore.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    /** Deterministic "dua of the day": same dua for the whole day, changes at midnight. */
    val duaOfTheDayId: Int
        get() {
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            val duas = book.allDuas
            return if (duas.isEmpty()) 0 else duas[dayOfYear % duas.size].id
        }

    fun setThemeMode(mode: String) = viewModelScope.launch { settingsStore.setThemeMode(mode) }

    fun setArabicSize(size: Float) = viewModelScope.launch { settingsStore.setArabicSize(size) }

    fun setAmharicSize(size: Float) = viewModelScope.launch { settingsStore.setAmharicSize(size) }

    fun setShowArabic(show: Boolean) = viewModelScope.launch { settingsStore.setShowArabic(show) }

    fun setShowTranslation(show: Boolean) = viewModelScope.launch { settingsStore.setShowTranslation(show) }

    fun toggleFavorite(duaId: Int) = viewModelScope.launch { settingsStore.toggleFavorite(duaId) }

    fun clearFavorites() = viewModelScope.launch { settingsStore.clearFavorites() }

    fun incrementCounter(duaId: Int, target: Int) = viewModelScope.launch {
        settingsStore.incrementCounter(duaId, target)
    }

    fun resetCounter(duaId: Int) = viewModelScope.launch { settingsStore.resetCounter(duaId) }

    fun resetAllCounters() = viewModelScope.launch { settingsStore.resetAllCounters() }

    fun setTasbihCount(value: Int) = viewModelScope.launch { settingsStore.setTasbihCount(value) }
}
