package com.ehan.app4

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ehan.app4.ui.theme.App4Theme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
// SDK 35 runs on JDK 17+; SDK 36 would require JDK 21.
@Config(sdk = [35], qualifiers = "w360dp-h640dp-xxhdpi")
class MainScreenScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun Header() {
        composeRule.setContent {
            HeaderPreview()
        }
        // Saved to app/src/test/screenshots/<package>.<class>.<method>.png
        composeRule.onRoot().captureRoboImage()
    }

    @Test
    fun HeaderOtherTab() {
        composeRule.setContent {
            HeaderPreviewOtherTab()
        }
        // Saved to app/src/test/screenshots/<package>.<class>.<method>.png
        composeRule.onRoot().captureRoboImage()
    }

    @Test
    fun DisplayPanel() {
        composeRule.setContent {
            DisplayPanelPreview()
        }
        // Saved to app/src/test/screenshots/<package>.<class>.<method>.png
        composeRule.onRoot().captureRoboImage()
    }

    @Test
    fun MainScreen() {
        composeRule.setContent {
            MainScreenPreview()
        }
        // Saved to app/src/test/screenshots/<package>.<class>.<method>.png
        composeRule.onRoot().captureRoboImage()
    }

    @Test
    fun MainScreenOtherTab() {
        composeRule.setContent {
            MainScreenPreviewOtherTab()
        }
        // Saved to app/src/test/screenshots/<package>.<class>.<method>.png
        composeRule.onRoot().captureRoboImage()
    }
}
