package com.example.data.parser

import com.example.data.model.MediaFormat
import com.example.data.model.Platform
import com.example.data.model.QualityOption
import com.example.data.model.VideoMetadata

object VideoUrlParser {

    data class SampleUrl(
        val platform: Platform,
        val title: String,
        val url: String,
        val description: String
    )

    val sampleUrls = listOf(
        SampleUrl(
            platform = Platform.YOUTUBE,
            title = "Nature 4K Wildlife Documentary",
            url = "https://www.youtube.com/watch?v=LXb3EKWsInQ",
            description = "YouTube 4K 60fps & 1080p with HDR colors"
        ),
        SampleUrl(
            platform = Platform.TIKTOK,
            title = "Urban Street Dance Reel",
            url = "https://www.tiktok.com/@dancer_crews/video/73918239019284",
            description = "TikTok HD Watermark-Free & Audio MP3"
        ),
        SampleUrl(
            platform = Platform.INSTAGRAM,
            title = "Cinematic Travel Drone Reel",
            url = "https://www.instagram.com/reel/C8kP92xV5nm/",
            description = "Instagram Reels 1080p High Bitrate"
        ),
        SampleUrl(
            platform = Platform.FACEBOOK,
            title = "Speed Car Build Highlights",
            url = "https://www.facebook.com/watch/?v=982374921029",
            description = "Facebook Watch 1080p & 720p HD"
        ),
        SampleUrl(
            platform = Platform.TWITTER,
            title = "SpaceX Falcon Heavy Launch 4K",
            url = "https://x.com/SpaceX/status/179283749201948271",
            description = "Twitter / X HD Video Clip"
        )
    )

    fun parseUrl(rawUrl: String): VideoMetadata {
        val trimmed = rawUrl.trim()
        val platform = Platform.fromUrl(trimmed)

        return when (platform) {
            Platform.YOUTUBE -> generateYouTubeMetadata(trimmed)
            Platform.TIKTOK -> generateTikTokMetadata(trimmed)
            Platform.INSTAGRAM -> generateInstagramMetadata(trimmed)
            Platform.FACEBOOK -> generateFacebookMetadata(trimmed)
            Platform.TWITTER -> generateTwitterMetadata(trimmed)
            Platform.DIRECT -> generateDirectMetadata(trimmed)
        }
    }

    private fun generateYouTubeMetadata(url: String): VideoMetadata {
        val isShort = url.contains("shorts")
        val title = if (isShort) "Epic Kinetic Typography Short" else "Stunning 4K Nature & Wildlife Cinematic HDR"
        val author = "@CinematicCreators"
        val duration = if (isShort) 45 else 328
        val views = if (isShort) "4.2M views" else "1.8M views"

        val qualityOptions = listOf(
            QualityOption(
                id = "yt_4k",
                label = "4K Ultra HD (2160p)",
                resolution = "3840 x 2160",
                format = MediaFormat.MP4,
                estimatedSizeBytes = 345L * 1024 * 1024,
                fps = 60,
                bitrate = "45 Mbps",
                isRecommended = true
            ),
            QualityOption(
                id = "yt_1080p",
                label = "Full HD (1080p 60fps)",
                resolution = "1920 x 1080",
                format = MediaFormat.MP4,
                estimatedSizeBytes = 112L * 1024 * 1024,
                fps = 60,
                bitrate = "12 Mbps",
                isRecommended = false
            ),
            QualityOption(
                id = "yt_720p",
                label = "HD (720p)",
                resolution = "1280 x 720",
                format = MediaFormat.MP4,
                estimatedSizeBytes = 54L * 1024 * 1024,
                fps = 30,
                bitrate = "5 Mbps"
            ),
            QualityOption(
                id = "yt_480p",
                label = "Standard (480p)",
                resolution = "854 x 480",
                format = MediaFormat.MP4,
                estimatedSizeBytes = 28L * 1024 * 1024,
                fps = 30,
                bitrate = "2.5 Mbps"
            ),
            QualityOption(
                id = "yt_mp3",
                label = "Audio Only (MP3 320kbps)",
                resolution = "Studio Quality Audio",
                format = MediaFormat.MP3,
                estimatedSizeBytes = 9L * 1024 * 1024,
                fps = 0,
                bitrate = "320 kbps"
            ),
            QualityOption(
                id = "yt_m4a",
                label = "Audio Only (M4A 256kbps)",
                resolution = "AAC High Quality",
                format = MediaFormat.M4A,
                estimatedSizeBytes = 7L * 1024 * 1024,
                fps = 0,
                bitrate = "256 kbps"
            )
        )

        return VideoMetadata(
            title = title,
            author = author,
            platform = Platform.YOUTUBE,
            originalUrl = url,
            thumbnailUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&q=80",
            durationSeconds = duration,
            viewCount = views,
            qualityOptions = qualityOptions,
            defaultDownloadUrl = url
        )
    }

    private fun generateTikTokMetadata(url: String): VideoMetadata {
        return VideoMetadata(
            title = "Viral Shuffle Choreography & Flow 🎵",
            author = "@dancevibes_daily",
            platform = Platform.TIKTOK,
            originalUrl = url,
            thumbnailUrl = "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?w=800&q=80",
            durationSeconds = 34,
            viewCount = "8.9M views",
            qualityOptions = listOf(
                QualityOption(
                    id = "tt_hd_nowm",
                    label = "1080p HD (No Watermark)",
                    resolution = "1080 x 1920",
                    format = MediaFormat.MP4,
                    estimatedSizeBytes = 24L * 1024 * 1024,
                    fps = 60,
                    bitrate = "10 Mbps",
                    isWatermarkFree = true,
                    isRecommended = true
                ),
                QualityOption(
                    id = "tt_720p",
                    label = "720p HD Standard",
                    resolution = "720 x 1280",
                    format = MediaFormat.MP4,
                    estimatedSizeBytes = 14L * 1024 * 1024,
                    fps = 30,
                    bitrate = "4.5 Mbps",
                    isWatermarkFree = true
                ),
                QualityOption(
                    id = "tt_audio",
                    label = "Original Audio (MP3)",
                    resolution = "Trending TikTok Sound",
                    format = MediaFormat.MP3,
                    estimatedSizeBytes = 2L * 1024 * 1024,
                    fps = 0,
                    bitrate = "320 kbps"
                )
            ),
            defaultDownloadUrl = url
        )
    }

    private fun generateInstagramMetadata(url: String): VideoMetadata {
        return VideoMetadata(
            title = "Aesthetic Sunset by the Amalfi Coastline 🇮🇹",
            author = "@wanderlust.visuals",
            platform = Platform.INSTAGRAM,
            originalUrl = url,
            thumbnailUrl = "https://images.unsplash.com/photo-1533105079780-92b9be482077?w=800&q=80",
            durationSeconds = 58,
            viewCount = "2.3M likes",
            qualityOptions = listOf(
                QualityOption(
                    id = "ig_1080p",
                    label = "Full HD (1080p Reel)",
                    resolution = "1080 x 1920",
                    format = MediaFormat.MP4,
                    estimatedSizeBytes = 36L * 1024 * 1024,
                    fps = 60,
                    bitrate = "12 Mbps",
                    isRecommended = true
                ),
                QualityOption(
                    id = "ig_720p",
                    label = "HD (720p)",
                    resolution = "720 x 1280",
                    format = MediaFormat.MP4,
                    estimatedSizeBytes = 18L * 1024 * 1024,
                    fps = 30,
                    bitrate = "5 Mbps"
                ),
                QualityOption(
                    id = "ig_mp3",
                    label = "Reel Audio Track (MP3)",
                    resolution = "Audio Stream",
                    format = MediaFormat.MP3,
                    estimatedSizeBytes = 3L * 1024 * 1024,
                    fps = 0,
                    bitrate = "320 kbps"
                )
            ),
            defaultDownloadUrl = url
        )
    }

    private fun generateFacebookMetadata(url: String): VideoMetadata {
        return VideoMetadata(
            title = "Master Craftsman Custom Table Restoration",
            author = "Artisan Workshop Official",
            platform = Platform.FACEBOOK,
            originalUrl = url,
            thumbnailUrl = "https://images.unsplash.com/photo-1540555700478-4be289fbecef?w=800&q=80",
            durationSeconds = 210,
            viewCount = "950K views",
            qualityOptions = listOf(
                QualityOption(
                    id = "fb_1080p",
                    label = "Full HD (1080p)",
                    resolution = "1920 x 1080",
                    format = MediaFormat.MP4,
                    estimatedSizeBytes = 88L * 1024 * 1024,
                    fps = 60,
                    bitrate = "14 Mbps",
                    isRecommended = true
                ),
                QualityOption(
                    id = "fb_720p",
                    label = "HD (720p)",
                    resolution = "1280 x 720",
                    format = MediaFormat.MP4,
                    estimatedSizeBytes = 42L * 1024 * 1024,
                    fps = 30,
                    bitrate = "6 Mbps"
                ),
                QualityOption(
                    id = "fb_480p",
                    label = "SD (480p)",
                    resolution = "854 x 480",
                    format = MediaFormat.MP4,
                    estimatedSizeBytes = 20L * 1024 * 1024,
                    fps = 30,
                    bitrate = "2.8 Mbps"
                ),
                QualityOption(
                    id = "fb_mp3",
                    label = "Audio Only (MP3)",
                    resolution = "High Quality Audio",
                    format = MediaFormat.MP3,
                    estimatedSizeBytes = 6L * 1024 * 1024,
                    fps = 0,
                    bitrate = "320 kbps"
                )
            ),
            defaultDownloadUrl = url
        )
    }

    private fun generateTwitterMetadata(url: String): VideoMetadata {
        return VideoMetadata(
            title = "Starship Super Heavy Booster Static Fire Test 🚀",
            author = "@SpaceTech_News",
            platform = Platform.TWITTER,
            originalUrl = url,
            thumbnailUrl = "https://images.unsplash.com/photo-1517976487588-4663e00b86a7?w=800&q=80",
            durationSeconds = 48,
            viewCount = "3.1M views",
            qualityOptions = listOf(
                QualityOption(
                    id = "tw_1080p",
                    label = "Full HD (1080p)",
                    resolution = "1920 x 1080",
                    format = MediaFormat.MP4,
                    estimatedSizeBytes = 32L * 1024 * 1024,
                    fps = 60,
                    bitrate = "12 Mbps",
                    isRecommended = true
                ),
                QualityOption(
                    id = "tw_720p",
                    label = "HD (720p)",
                    resolution = "1280 x 720",
                    format = MediaFormat.MP4,
                    estimatedSizeBytes = 16L * 1024 * 1024,
                    fps = 30,
                    bitrate = "5 Mbps"
                ),
                QualityOption(
                    id = "tw_mp3",
                    label = "Audio Clip (MP3)",
                    resolution = "Audio Track",
                    format = MediaFormat.MP3,
                    estimatedSizeBytes = 2L * 1024 * 1024,
                    fps = 0,
                    bitrate = "320 kbps"
                )
            ),
            defaultDownloadUrl = url
        )
    }

    private fun generateDirectMetadata(url: String): VideoMetadata {
        val fileName = url.substringAfterLast("/").substringBefore("?").ifBlank { "Media Video Stream" }
        return VideoMetadata(
            title = fileName,
            author = "Direct Media Source",
            platform = Platform.DIRECT,
            originalUrl = url,
            thumbnailUrl = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=800&q=80",
            durationSeconds = 120,
            viewCount = "Direct URL",
            qualityOptions = listOf(
                QualityOption(
                    id = "direct_orig",
                    label = "Source Quality (1080p HD)",
                    resolution = "1920 x 1080",
                    format = MediaFormat.MP4,
                    estimatedSizeBytes = 45L * 1024 * 1024,
                    fps = 60,
                    bitrate = "8 Mbps",
                    isRecommended = true
                ),
                QualityOption(
                    id = "direct_mp3",
                    label = "Extract Audio (MP3)",
                    resolution = "Audio Extraction",
                    format = MediaFormat.MP3,
                    estimatedSizeBytes = 4L * 1024 * 1024,
                    fps = 0,
                    bitrate = "320 kbps"
                )
            ),
            defaultDownloadUrl = url
        )
    }
}
