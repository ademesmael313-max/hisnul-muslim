package com.hisnulmuslim.amharic.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hisnulmuslim.amharic.R
import com.hisnulmuslim.amharic.ui.components.contentSidePadding
import com.hisnulmuslim.amharic.data.AppSettings

/** Destructive actions that ask for confirmation first. */
private enum class DestructiveAction(val titleRes: Int, val bodyRes: Int) {
    RESET_COUNTERS(R.string.confirm_reset_counters_title, R.string.confirm_reset_counters_body),
    CLEAR_FAVORITES(R.string.confirm_clear_favorites_title, R.string.confirm_clear_favorites_body),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    versionName: String,
    onBack: () -> Unit,
    onThemeMode: (String) -> Unit,
    onArabicSize: (Float) -> Unit,
    onAmharicSize: (Float) -> Unit,
    onShowArabic: (Boolean) -> Unit,
    onShowTranslation: (Boolean) -> Unit,
    onResetCounters: () -> Unit,
    onClearFavorites: () -> Unit,
    onOpenAbout: () -> Unit,
) {
    var pendingAction by remember { mutableStateOf<DestructiveAction?>(null) }
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
                        text = stringResource(R.string.settings),
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = contentSidePadding(), vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SettingsCard(title = stringResource(R.string.settings_appearance)) {
                Text(
                    text = stringResource(R.string.theme),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = settings.themeMode == AppSettings.THEME_SYSTEM,
                        onClick = { onThemeMode(AppSettings.THEME_SYSTEM) },
                        label = { Text(stringResource(R.string.theme_system)) },
                    )
                    FilterChip(
                        selected = settings.themeMode == AppSettings.THEME_LIGHT,
                        onClick = { onThemeMode(AppSettings.THEME_LIGHT) },
                        label = { Text(stringResource(R.string.theme_light)) },
                    )
                    FilterChip(
                        selected = settings.themeMode == AppSettings.THEME_DARK,
                        onClick = { onThemeMode(AppSettings.THEME_DARK) },
                        label = { Text(stringResource(R.string.theme_dark)) },
                    )
                }
            }

            SettingsCard(title = stringResource(R.string.settings_reading)) {
                var arabicSize by remember(settings.arabicSize) {
                    mutableFloatStateOf(settings.arabicSize)
                }
                var amharicSize by remember(settings.amharicSize) {
                    mutableFloatStateOf(settings.amharicSize)
                }

                Text(
                    text = "${stringResource(R.string.arabic_size)} · ${arabicSize.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Slider(
                    value = arabicSize,
                    onValueChange = { arabicSize = it },
                    onValueChangeFinished = { onArabicSize(arabicSize) },
                    valueRange = 18f..42f,
                    steps = 23,
                )
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَنِ الرَّحِيمِ",
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = arabicSize.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "${stringResource(R.string.amharic_size)} · ${amharicSize.toInt()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Slider(
                    value = amharicSize,
                    onValueChange = { amharicSize = it },
                    onValueChangeFinished = { onAmharicSize(amharicSize) },
                    valueRange = 13f..26f,
                    steps = 12,
                )
                Text(
                    text = stringResource(R.string.size_preview),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = amharicSize.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(Modifier.height(12.dp))

                SwitchRow(
                    title = stringResource(R.string.show_arabic),
                    checked = settings.showArabic,
                    onCheckedChange = onShowArabic,
                )
                SwitchRow(
                    title = stringResource(R.string.show_translation),
                    checked = settings.showTranslation,
                    onCheckedChange = onShowTranslation,
                )
            }

            SettingsCard(title = stringResource(R.string.settings_data)) {
                OutlinedButton(
                    onClick = { pendingAction = DestructiveAction.RESET_COUNTERS },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.reset_all_counters))
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { pendingAction = DestructiveAction.CLEAR_FAVORITES },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.clear_favorites))
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenAbout),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.about_app),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f),
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Text(
                text = stringResource(R.string.offline_note),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
            )
            Text(
                text = stringResource(R.string.version_label, versionName),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
    }

    pendingAction?.let { action ->
        AlertDialog(
            onDismissRequest = { pendingAction = null },
            title = { Text(stringResource(action.titleRes)) },
            text = { Text(stringResource(action.bodyRes)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        when (action) {
                            DestructiveAction.RESET_COUNTERS -> onResetCounters()
                            DestructiveAction.CLEAR_FAVORITES -> onClearFavorites()
                        }
                        pendingAction = null
                    },
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingAction = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun SwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
