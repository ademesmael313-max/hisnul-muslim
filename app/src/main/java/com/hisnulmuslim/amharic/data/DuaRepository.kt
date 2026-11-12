package com.hisnulmuslim.amharic.data

import android.content.Context

/**
 * Loads the bundled, fully offline book from `assets`.
 *
 * The asset is parsed once and cached; the whole book takes a few dozen
 * milliseconds, so it is safe to call from a background coroutine. Any problem
 * with the asset surfaces as a [BookFormatException] so the UI can show a
 * friendly message instead of crashing.
 */
class DuaRepository(private val context: Context) {

    @Volatile
    private var cached: DuaBook? = null

    @Throws(BookFormatException::class)
    fun load(): DuaBook {
        cached?.let { return it }
        return synchronized(this) {
            cached ?: runCatching { DuaBookParser.parse(readAsset()) }
                .getOrElse { throw BookFormatException("cannot read $ASSET_NAME", it) }
                .also { cached = it }
        }
    }

    private fun readAsset(): String =
        context.assets.open(ASSET_NAME).bufferedReader(Charsets.UTF_8).use { it.readText() }

    private companion object {
        const val ASSET_NAME = "data/hisnul_muslim_am.json"
    }
}
