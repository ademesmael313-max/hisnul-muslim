package com.hisnulmuslim.amharic.data

import android.content.Context
import org.json.JSONObject

/**
 * Loads the bundled, fully offline book from assets. Parsing 267 duas takes a
 * few dozen milliseconds, so it is done once on first access and cached.
 */
class DuaRepository(private val context: Context) {

    @Volatile
    private var cached: DuaBook? = null

    fun load(): DuaBook {
        cached?.let { return it }
        return synchronized(this) {
            cached ?: parse(readAsset()).also { cached = it }
        }
    }

    private fun readAsset(): String =
        context.assets.open(ASSET_NAME).bufferedReader(Charsets.UTF_8).use { it.readText() }

    private fun parse(raw: String): DuaBook {
        val root = JSONObject(raw)

        val categoriesJson = root.getJSONArray("categories")
        val categories = ArrayList<Category>(categoriesJson.length())
        for (i in 0 until categoriesJson.length()) {
            val item = categoriesJson.getJSONObject(i)
            categories += Category(
                id = item.getString("id"),
                title = item.getString("title"),
                description = item.optString("description"),
                icon = item.optString("icon"),
            )
        }

        val chaptersJson = root.getJSONArray("chapters")
        val chapters = ArrayList<Chapter>(chaptersJson.length())
        for (i in 0 until chaptersJson.length()) {
            val chapterJson = chaptersJson.getJSONObject(i)
            val chapterId = chapterJson.getInt("id")
            val chapterTitle = chapterJson.getString("title")
            val categoryId = chapterJson.getString("categoryId")
            val order = chapterJson.optInt("order", chapterId)

            val duasJson = chapterJson.getJSONArray("duas")
            val duas = ArrayList<Dua>(duasJson.length())
            for (j in 0 until duasJson.length()) {
                val duaJson = duasJson.getJSONObject(j)
                duas += Dua(
                    id = duaJson.getInt("id"),
                    arabic = duaJson.optString("arabic"),
                    amharic = duaJson.optString("amharic"),
                    repeat = duaJson.optInt("repeat", 1).coerceAtLeast(1),
                    chapterId = chapterId,
                    chapterTitle = chapterTitle,
                    categoryId = categoryId,
                )
            }

            chapters += Chapter(
                id = chapterId,
                title = chapterTitle,
                categoryId = categoryId,
                order = order,
                duas = duas,
            )
        }

        return DuaBook(
            categories = categories,
            chapters = chapters.sortedBy { it.order },
        )
    }

    private companion object {
        const val ASSET_NAME = "data/hisnul_muslim_am.json"
    }
}
