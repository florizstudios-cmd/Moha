package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Platform
import com.example.ui.theme.FacebookBlue
import com.example.ui.theme.HighResGold
import com.example.ui.theme.InstagramGradientEnd
import com.example.ui.theme.TikTokPink
import com.example.ui.theme.TwitterBlue
import com.example.ui.theme.YouTubeRed

@Composable
fun PlatformBadge(
    platform: Platform,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val (dotColor, label) = when (platform) {
        Platform.YOUTUBE -> Pair(YouTubeRed, "YouTube")
        Platform.TIKTOK -> Pair(TikTokPink, "TikTok")
        Platform.INSTAGRAM -> Pair(InstagramGradientEnd, "Instagram")
        Platform.FACEBOOK -> Pair(FacebookBlue, "Facebook")
        Platform.TWITTER -> Pair(TwitterBlue, "Twitter")
        Platform.DIRECT -> Pair(MaterialTheme.colorScheme.primary, "Direct")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            if (showLabel) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ResolutionBadge(
    resolution: String,
    modifier: Modifier = Modifier,
    is4K: Boolean = resolution.contains("4K", ignoreCase = true),
    isAudio: Boolean = resolution.contains("Audio", ignoreCase = true) || resolution.contains("MP3", ignoreCase = true)
) {
    val (bgColor, textColor) = when {
        is4K -> Pair(
            HighResGold.copy(alpha = 0.2f),
            HighResGold
        )
        isAudio -> Pair(
            Color(0xFF8B5CF6).copy(alpha = 0.18f),
            Color(0xFF8B5CF6)
        )
        else -> Pair(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 7.dp, vertical = 2.5.dp)
    ) {
        Text(
            text = (resolution.split(" ").firstOrNull() ?: resolution).uppercase(),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

