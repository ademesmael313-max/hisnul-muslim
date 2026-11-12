package com.hisnulmuslim.amharic

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End-to-end smoke test: launches the real app and checks that the bundled book
 * renders, that search works and that navigation + bookmarks work.
 */
@RunWith(AndroidJUnit4::class)
class AppSmokeTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeScreenRendersBook() {
        rule.onNodeWithText("ሂስኑል ሙስሊም").assertIsDisplayed()
        rule.onNodeWithText("የዕለት ተዕለት ዱዓዎችና ዚክሮች").assertIsDisplayed()
        rule.onNodeWithText("የዕለቱ ዱዓ").assertIsDisplayed()
    }

    @Test
    fun searchFindsDuas() {
        rule.onNode(hasSetTextAction()).performTextInput("አላህ")
        rule.waitUntil(timeoutMillis = 40_000) {
            rule.onAllNodesWithText("የፍለጋ ውጤቶች").fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText("የፍለጋ ውጤቶች").assertIsDisplayed()
    }

    @Test
    fun openChapterFromDuaOfTheDay() {
        rule.onNodeWithText("የዕለቱ ዱዓ").performClick()
        // The chapter screen always shows a back arrow in its app bar.
        rule.waitUntil(timeoutMillis = 40_000) {
            rule.onAllNodesWithContentDescription("ተመለስ").fetchSemanticsNodes().isNotEmpty()
        }
        rule.onAllNodesWithContentDescription("ተመለስ")[0].assertIsDisplayed()
        // …and at least one dua card with its favourite button.
        rule.onAllNodesWithContentDescription("ወደ ተወዳጆች ጨምር")[0].assertIsDisplayed()
    }

    @Test
    fun bookmarkADua() {
        rule.onNodeWithText("የዕለቱ ዱዓ").performClick()
        rule.waitUntil(timeoutMillis = 40_000) {
            rule.onAllNodesWithContentDescription("ወደ ተወዳጆች ጨምር").fetchSemanticsNodes().isNotEmpty()
        }
        rule.onAllNodesWithContentDescription("ወደ ተወዳጆች ጨምር")[0].performClick()
        rule.waitUntil(timeoutMillis = 40_000) {
            rule.onAllNodesWithContentDescription("ከተወዳጆች አስወግድ").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun tasbihScreenOpens() {
        // The extended FAB label lives in the unmerged tree.
        rule.onNodeWithText("ተስቢሕ ቆጣሪ", useUnmergedTree = true).performClick()
        rule.waitUntil(timeoutMillis = 40_000) {
            rule.onAllNodesWithText("ሱብሐናላህ").fetchSemanticsNodes().isNotEmpty()
        }
        rule.onAllNodesWithText("ሱብሐናላህ")[0].assertIsDisplayed()
    }
}
