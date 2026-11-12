package com.hisnulmuslim.amharic

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

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

        // Save a real screenshot of the home screen (used by CI, ignored on failure).
        runCatching {
            val bitmap = rule.onRoot().captureToImage().asAndroidBitmap()
            val dir = InstrumentationRegistry.getInstrumentation()
                .targetContext.getExternalFilesDir(null)
            File(dir, "screenshot-home.png").outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
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
