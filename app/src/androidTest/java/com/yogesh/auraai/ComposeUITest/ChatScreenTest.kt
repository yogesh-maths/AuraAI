package com.yogesh.auraai.ComposeUITest

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yogesh.auraai.presentation.chat.AuraTypingIndicator
import com.yogesh.auraai.presentation.home.AuraLogo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class ChatScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun greeting_isDisplayed() {

        composeTestRule.setContent {
            Text("How can I help you today?")
        }

        composeTestRule
            .onNodeWithText("How can I help you today?")
            .assertExists()
    }

    @Test
    fun auraLogo_renders() {

        composeTestRule.setContent {
            AuraLogo()
        }
    }
    @Test
    fun auraTypingIndicator_displaysTypingDots() {

        composeTestRule.setContent {
            AuraTypingIndicator()
        }

        composeTestRule
            .onAllNodesWithText("●")
            .assertCountEquals(3)
    }
}