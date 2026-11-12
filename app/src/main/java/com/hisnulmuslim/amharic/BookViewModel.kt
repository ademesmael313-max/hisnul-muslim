package com.hisnulmuslim.amharic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hisnulmuslim.amharic.data.AppSettings
import com.hisnulmuslim.amharic.data.DuaBook
import com.hisnulmuslim.amharic.data.DuaRepository
import com.hisnulmuslim.amharic.data.SettingsStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

/** What the UI needs to know about the bundled book. */
sealed interface BookUiState {
    data object Loading : BookUiState
    data class Ready(val book: DuaBook) : BookUiState
    data class Failed(val reason: String) : BookUiState
}

class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DuaRepository(application)
    private val settingsStore = SettingsStore(application)

    private val _bookState = MutableStateFlow<BookUiState>(BookUiState.Loading)
    val bookState: StateFlow<BookUiState> = _bookState.asStateFlow()

    val settings: StateFlow<AppSettings> = settingsStore.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings())

    init {
        loadBook()
    }

    /** (Re)loads and parses the book off the main thread. */
    fun loadBook() {
        if (_bookState.value is BookUiState.Ready) return
        _bookState.value = BookUiState.Loading
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { runCatching { repository.load() } }
            _bookState.value = result.fold(
                onSuccess = { BookUiState.Ready(it) },
                onFailure = { BookUiState.Failed(it.message ?: "unknown error") },
            )
        }
    }

    /** Deterministic "dua of the day": the same dua for the whole day, changes at midnight. */
    val duaOfTheDayId: Int
        get() {
            val book = (_bookState.value as? BookUiState.Ready)?.book ?: return 0
            val duas = book.allDuas
            if (duas.isEmpty()) return 0
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            return duas[dayOfYear % duas.size].id
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
