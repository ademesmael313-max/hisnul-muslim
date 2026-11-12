package com.hisnulmuslim.amharic.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DuaBookSearchTest {

    private fun dua(id: Int, arabic: String, amharic: String) =
        Dua(
            id = id,
            arabic = arabic,
            amharic = amharic,
            repeat = 1,
            chapterId = 1,
            chapterTitle = "የጠዋት ዚክር",
            categoryId = "c",
        )

    private val book = DuaBook(
        categories = emptyList(),
        chapters = listOf(
            Chapter(
                id = 1,
                title = "የጠዋት ዚክር",
                categoryId = "c",
                order = 1,
                duas = listOf(
                    dua(1, "بِسْمِ اللَّهِ", "በአላህ ስም"),
                    dua(2, "الْحَمْدُ لِلَّهِ", "ምስጋና ለአላህ"),
                    dua(3, "أَعُوذُ بِاللَّهِ", "በአላህ እጠበቃለሁ"),
                ),
            )
        ),
    )

    @Test
    fun findsAmharicText() {
        assertEquals(listOf(1), book.search("በአላህ ስም").map { it.id })
    }

    @Test
    fun findsArabicWithoutDiacritics() {
        assertEquals(listOf(2), book.search("الحمد لله").map { it.id })
    }

    @Test
    fun recognisesAlefVariants() {
        assertEquals(listOf(3), book.search("اعوذ").map { it.id })
    }

    @Test
    fun matchesChapterTitles() {
        assertEquals(3, book.search("የጠዋት").size)
    }

    @Test
    fun ignoresSurroundingWhitespace() {
        assertEquals(listOf(2), book.search("  الحمد  ").map { it.id })
    }

    @Test
    fun blankQueryReturnsNothing() {
        assertTrue(book.search("").isEmpty())
        assertTrue(book.search("   ").isEmpty())
    }

    @Test
    fun unknownQueryReturnsNothing() {
        assertTrue(book.search("የለም ቃል").isEmpty())
    }
}
