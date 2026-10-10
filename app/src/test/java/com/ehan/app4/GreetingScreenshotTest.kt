package com.example.template

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.template.ui.theme.TemplateTheme
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
    composeTestRule.setContent { TemplateTheme { GreetingPreview() } }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
  @Test
  fun greeting2Screenshot() {
    composeTestRule.setContent { TemplateTheme { Greeting("Robolectric") } }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting2.png")
  }
}
