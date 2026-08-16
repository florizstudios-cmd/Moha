package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class Platform(
    val displayName: String,
    val brandColor: Color,
    val iconName: String,
    val domains: List<String>
) {
    YOUTUBE(
        displayName = "YouTube",
        brandColor = Color(0xFFFF0000),
        iconName = "youtube",
        domains = listOf("youtube.com", "youtu.be", "m.youtube.com")
    ),
    TIKTOK(
        displayName = "TikTok",
        brandColor = Color(0xFF00F2FE),
        iconName = "tiktok",
        domains = listOf("tiktok.com", "vt.tiktok.com", "vm.tiktok.com")
    ),
    INSTAGRAM(
        displayName = "Instagram",
        brandColor = Color(0xFFE1306C),
        iconName = "instagram",
        domains = listOf("instagram.com", "instagr.am")
    ),
    FACEBOOK(
        displayName = "Facebook",
        brandColor = Color(0xFF1877F2),
        iconName = "facebook",
        domains = listOf("facebook.com", "fb.watch", "fb.com", "m.facebook.com")
    ),
    TWITTER(
        displayName = "X (Twitter)",
        brandColor = Color(0xFF1DA1F2),
        iconName = "twitter",
        domains = listOf("twitter.com", "x.com")
    ),
    DIRECT(
        displayName = "Direct Link",
        brandColor = Color(0xFF6366F1),
        iconName = "link",
        domains = emptyList()
    );

    companion object {
        fun fromUrl(url: String): Platform {
            val lower = url.lowercase().trim()
            return entries.firstOrNull { platform ->
                platform.domains.any { domain -> lower.contains(domain) }
            } ?: DIRECT
        }
    }
}

enum class MediaFormat(val extension: String, val isAudioOnly: Boolean) {
    MP4("mp4", false),
    WEBM("webm", false),
    MP3("mp3", true),
    M4A("m4a", true)
}

data class QualityOption(
    val id: String,
    val label: String, // e.g. "4K (2160p)", "1080p FHD"
    val resolution: String, // "3840x2160"
    val format: MediaFormat,
    val estimatedSizeBytes: Long,
    val fps: Int = 60,
    val bitrate: String = "45 Mbps",
    val isWatermarkFree: Boolean = true,
    val isRecommended: Boolean = false
) {
    fun getFormattedSize(): String {
        val mb = estimatedSizeBytes / (1024.0 * 1024.0)
        return if (mb >= 1024) {
            String.format("%.1f GB", mb / 1024.0)
        } else {
            String.format("%.1f MB", mb)
        }
    }
}

data class VideoMetadata(
    val title: String,
    val author: String,
    val platform: Platform,
    val originalUrl: String,
    val thumbnailUrl: String,
    val durationSeconds: Int,
    val viewCount: String,
    val qualityOptions: List<QualityOption>,
    val defaultDownloadUrl: String
) {
    fun getFormattedDuration(): String {
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}
