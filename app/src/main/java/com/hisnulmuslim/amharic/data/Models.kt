package com.hisnulmuslim.amharic.data

/** A single supplication, already carrying its chapter/category context. */
data class Dua(
    val id: Int,
    val arabic: String,
    val amharic: String,
    val repeat: Int,
    val chapterId: Int,
    val chapterTitle: String,
    val categoryId: String,
) {
    /** Text used by "copy" and "share". */
    fun shareText(): String = buildString {
        if (arabic.isNotBlank()) {
            append(arabic)
            append("\n\n")
        }
        append(amharic)
        append("\n\n— ")
        append(chapterTitle)
        append(" · ሂስኑል ሙስሊም")
    }
}

data class Chapter(
    val id: Int,
    val title: String,
    val categoryId: String,
    val order: Int,
    val duas: List<Dua>,
)

data class Category(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
)

data class DuaBook(
    val categories: List<Category>,
    val chapters: List<Chapter>,
) {
    val allDuas: List<Dua> = chapters.flatMap { it.duas }

    fun category(id: String): Category? = categories.firstOrNull { it.id == id }

    fun chapter(id: Int): Chapter? = chapters.firstOrNull { it.id == id }

    fun chaptersOf(categoryId: String): List<Chapter> = chapters.filter { it.categoryId == categoryId }

    /**
     * Case-insensitive search over the Amharic translation, the Arabic text and
     * chapter titles. Arabic is matched with diacritics and alef variants
     * stripped so that people can type without harakat.
     */
    fun search(query: String): List<Dua> {
        val needle = query.trim()
        if (needle.isEmpty()) return emptyList()
        val plainNeedle = normalize(needle)
        val arabicNeedle = normalizeArabic(needle)
        return allDuas.filter { dua ->
            normalize(dua.amharic).contains(plainNeedle) ||
                normalize(dua.chapterTitle).contains(plainNeedle) ||
                (arabicNeedle.isNotEmpty() && normalizeArabic(dua.arabic).contains(arabicNeedle))
        }
    }

    private fun normalize(value: String): String = value.lowercase().trim()

    private fun normalizeArabic(value: String): String = value
        .replace(ARABIC_MARKS, "")
        .replace('أ', 'ا')
        .replace('إ', 'ا')
        .replace('آ', 'ا')
        .replace('ى', 'ي')
        .replace('ة', 'ه')
        .replace('\u0640', ' ')
        .replace(ARABIC_SPACES, " ")
        .trim()

    private companion object {
        val ARABIC_MARKS = Regex("[\\u0610-\\u061A\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]")
        val ARABIC_SPACES = Regex("\\s+")
    }
}
