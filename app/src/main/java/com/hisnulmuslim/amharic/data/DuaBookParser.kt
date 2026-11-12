package com.hisnulmuslim.amharic.data

import org.json.JSONException
import org.json.JSONObject

/** Thrown when the bundled book is missing, empty or not valid dua data. */
class BookFormatException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Parses the JSON asset that ships in `assets/data/hisnul_muslim_am.json`.
 *
 * Deliberately free of Android types so it can be unit-tested on the JVM (see
 * `app/src/test`). The expected schema is:
 *
 * ```
 * { "categories": [{id, title, description, icon}],
 *   "chapters":  [{id, title, categoryId, order, duas: [{id, arabic, amharic, repeat}]}] }
 * ```
 */
object DuaBookParser {

    fun parse(raw: String): DuaBook {
        if (raw.isBlank()) throw BookFormatException("the book asset is empty")

        try {
            val root = JSONObject(raw)

            val categories = root.getJSONArray("categories").let { array ->
                List(array.length()) { index ->
                    val item = array.getJSONObject(index)
                    Category(
                        id = item.getString("id"),
                        title = item.getString("title"),
                        description = item.optString("description"),
                        icon = item.optString("icon"),
                    )
                }
            }

            val chapters = root.getJSONArray("chapters").let { array ->
                List(array.length()) { index ->
                    val chapterJson = array.getJSONObject(index)
                    val chapterId = chapterJson.getInt("id")
                    val chapterTitle = chapterJson.getString("title")
                    val categoryId = chapterJson.getString("categoryId")
                    val duasJson = chapterJson.getJSONArray("duas")

                    val duas = List(duasJson.length()) { duaIndex ->
                        val duaJson = duasJson.getJSONObject(duaIndex)
                        Dua(
                            id = duaJson.getInt("id"),
                            arabic = duaJson.optString("arabic").trim(),
                            amharic = duaJson.optString("amharic").trim(),
                            repeat = duaJson.optInt("repeat", 1).coerceAtLeast(1),
                            chapterId = chapterId,
                            chapterTitle = chapterTitle,
                            categoryId = categoryId,
                        )
                    }

                    Chapter(
                        id = chapterId,
                        title = chapterTitle,
                        categoryId = categoryId,
                        order = chapterJson.optInt("order", chapterId),
                        duas = duas,
                    )
                }
            }

            if (chapters.isEmpty()) throw BookFormatException("the book has no chapters")
            if (chapters.all { it.duas.isEmpty() }) throw BookFormatException("the book has no duas")

            return DuaBook(
                categories = categories,
                chapters = chapters.sortedBy { it.order },
            )
        } catch (e: JSONException) {
            throw BookFormatException("the book asset is not valid JSON", e)
        }
    }
}
