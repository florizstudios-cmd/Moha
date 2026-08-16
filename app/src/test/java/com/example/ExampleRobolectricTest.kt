package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Platform
import com.example.data.parser.VideoUrlParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Video Downloader", appName)
  }

  @Test
  fun `test platform url parsing for youtube, tiktok, instagram, facebook, twitter`() {
    val yt = VideoUrlParser.parseUrl("https://www.youtube.com/watch?v=LXb3EKWsInQ")
    assertEquals(Platform.YOUTUBE, yt.platform)
    assertTrue(yt.qualityOptions.any { it.label.contains("4K") })

    val tt = VideoUrlParser.parseUrl("https://www.tiktok.com/@dance/video/123456789")
    assertEquals(Platform.TIKTOK, tt.platform)
    assertTrue(tt.qualityOptions.any { it.isWatermarkFree })

    val ig = VideoUrlParser.parseUrl("https://www.instagram.com/reel/C8kP92xV5nm/")
    assertEquals(Platform.INSTAGRAM, ig.platform)

    val fb = VideoUrlParser.parseUrl("https://www.facebook.com/watch/?v=982374921029")
    assertEquals(Platform.FACEBOOK, fb.platform)

    val tw = VideoUrlParser.parseUrl("https://x.com/SpaceX/status/179283749201948271")
    assertEquals(Platform.TWITTER, tw.platform)
  }
}
