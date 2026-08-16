package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class DownloadStatus {
    QUEUED,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED
}

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val originalUrl: String,
    val title: String,
    val author: String,
    val platform: String, // from Platform.name
    val thumbnailUrl: String,
    val resolutionLabel: String, // e.g. "4K (2160p)"
    val format: String, // "MP4", "MP3"
    val fps: Int = 60,
    val totalBytes: Long,
    val downloadedBytes: Long = 0L,
    val speedBytesPerSec: Long = 0L,
    val status: String = DownloadStatus.DOWNLOADING.name,
    val localFilePath: String = "",
    val durationSeconds: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val errorMessage: String? = null
) {
    val progress: Float
        get() = if (totalBytes > 0) (downloadedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f) else 0f

    val progressPercent: Int
        get() = (progress * 100).toInt()

    val url: String get() = originalUrl
    val timestamp: Long get() = completedAt ?: createdAt
    val filePath: String get() = localFilePath

    fun getFormattedDate(): String {
        val date = java.util.Date(timestamp)
        val format = java.text.SimpleDateFormat("MMM dd, yyyy • HH:mm", java.util.Locale.getDefault())
        return format.format(date)
    }

    fun getFormattedSpeed(): String {
        val mbps = speedBytesPerSec / (1024.0 * 1024.0)
        return if (mbps >= 1.0) {
            String.format("%.1f MB/s", mbps)
        } else {
            val kbps = speedBytesPerSec / 1024.0
            String.format("%.0f KB/s", kbps)
        }
    }

    fun getFormattedDownloaded(): String {
        val dlMb = downloadedBytes / (1024.0 * 1024.0)
        val totalMb = totalBytes / (1024.0 * 1024.0)
        return String.format("%.1f / %.1f MB", dlMb, totalMb)
    }

    fun getFormattedDuration(): String {
        val minutes = durationSeconds / 60
        val seconds = durationSeconds % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    fun getEta(): String {
        if (speedBytesPerSec <= 0 || downloadedBytes >= totalBytes) return "0s"
        val remainingBytes = totalBytes - downloadedBytes
        val remainingSeconds = remainingBytes / speedBytesPerSec
        return if (remainingSeconds > 60) {
            "${remainingSeconds / 60}m ${remainingSeconds % 60}s"
        } else {
            "${remainingSeconds}s"
        }
    }
}
