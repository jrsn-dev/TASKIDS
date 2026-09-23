package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.model.Child
import com.example.ui.game.GameAvatar
import com.example.ui.theme.MyApplicationTheme
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
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun procedural_avatar_screenshot() {
    val child = Child(
      id = 1,
      name = "Alex",
      avatarSkinTone = 2,
      avatarHairStyle = 0,
      avatarHairColor = 0,
      avatarOutfitColor = 0,
      gameTheme = "SKY"
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        Box(
          modifier = Modifier
            .background(Color(0xFF102A68))
            .padding(24.dp)
        ) {
          GameAvatar(
            child = child,
            modifier = Modifier.size(260.dp)
          )
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(
      filePath = "src/test/screenshots/procedural_avatar.png"
    )
  }
}
