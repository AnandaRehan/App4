package com.ehan.app4

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.ehan.app4.ui.theme.App4Theme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers =
        //RobolectricDeviceQualifiers.Pixel8
        "+land", sdk = [34])
class GreetingScreenshotTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun greetingScreenshot() {
    composeTestRule.setContent { App4Theme { GreetingPreview() } }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
  @Test
  fun greeting2Screenshot() {
    composeTestRule.setContent { App4Theme { Greeting("Robolectric") } }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting2.png")
  }
}
