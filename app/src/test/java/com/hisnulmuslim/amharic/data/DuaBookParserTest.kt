package com.hisnulmuslim.amharic.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.File

class DuaBookParserTest {

    private val json = """
        {
          "schemaVersion": 1,
          "categories": [{"id": "a", "title": "ምድብ", "description": "", "icon": "Home"}],
          "chapters": [
            {"id": 2, "title": "ሁለተኛ", "categoryId": "a", "order": 2,
             "duas": [{"id": 20, "arabic": "ا", "amharic": "ሁለት", "repeat": 3}]},
            {"id": 1, "title": "አንደኛ", "categoryId": "a", "order": 1,
             "duas": [{"id": 10, "arabic": "", "amharic": "አንድ"}]}
          ]
        }
    """.trimIndent()

    @Test
    fun sortsChaptersByOrder() {
        val book = DuaBookParser.parse(json)
        assertEquals(listOf(1, 2), book.chapters.map { it.id })
    }

    @Test
    fun carriesChapterContextIntoDuas() {
        val book = DuaBookParser.parse(json)
        val dua = book.allDuas.first { it.id == 20 }
        assertEquals(2, dua.chapterId)
        assertEquals("ሁለተኛ", dua.chapterTitle)
        assertEquals("a", dua.categoryId)
        assertEquals(3, dua.repeat)
    }

    @Test
    fun defaultsRepeatToOne() {
        val book = DuaBookParser.parse(json)
        assertEquals(1, book.allDuas.first { it.id == 10 }.repeat)
    }

    @Test
    fun rejectsBrokenJson() {
        try {
            DuaBookParser.parse("{ this is not json")
            fail("expected BookFormatException")
        } catch (expected: BookFormatException) {
            assertTrue(expected.message.orEmpty().isNotEmpty())
        }
    }

    @Test
    fun rejectsEmptyDocument() {
        try {
            DuaBookParser.parse("   ")
            fail("expected BookFormatException")
        } catch (expected: BookFormatException) {
            assertTrue(expected.message.orEmpty().isNotEmpty())
        }
    }

    /** Guards the bundled content: the app ships a real book, not a stub. */
    @Test
    fun parsesTheShippedAsset() {
        val asset = File("src/main/assets/data/hisnul_muslim_am.json")
        assertTrue("asset missing at ${asset.absolutePath}", asset.exists())

        val book = DuaBookParser.parse(asset.readText())

        assertTrue("expected at least 130 chapters", book.chapters.size >= 130)
        assertTrue("expected at least 260 duas", book.allDuas.size >= 260)
        assertTrue("expected categories", book.categories.isNotEmpty())
        assertTrue("every dua needs Amharic", book.allDuas.all { it.amharic.isNotBlank() })
        assertEquals(
            "dua ids must be unique",
            book.allDuas.size,
            book.allDuas.map { it.id }.toSet().size,
        )
    }
}
