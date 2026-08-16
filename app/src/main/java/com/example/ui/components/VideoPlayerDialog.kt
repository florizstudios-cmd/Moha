package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.DownloadEntity
import com.example.data.model.Platform
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerDialog(
    item: DownloadEntity,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(false) }
    var isLooping by remember { mutableStateOf(true) }
    var selectedSpeed by remember { mutableStateOf("1.0x") }
    var playbackProgress by remember { mutableFloatStateOf(0f) }
    var showControls by remember { mutableStateOf(true) }

    val totalDurationSec = if (item.durationSeconds > 0) item.durationSeconds else 60

    // Playback ticker simulation
    LaunchedEffect(isPlaying, selectedSpeed) {
        val speedMultiplier = when (selectedSpeed) {
            "0.5x" -> 0.5f
            "1.25x" -> 1.25f
            "1.5x" -> 1.5f
            "2.0x" -> 2.0f
            else -> 1.0f
        }
        while (isPlaying) {
            delay(100)
            val step = (0.1f * speedMultiplier) / totalDurationSec
            val next = playbackProgress + step
            if (next >= 1f) {
                if (isLooping) {
                    playbackProgress = 0f
                } else {
                    playbackProgress = 1f
                    isPlaying = false
                }
            } else {
                playbackProgress = next
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f))
                .padding(16.dp),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val platform = try {
                            Platform.valueOf(item.platform)
                        } catch (e: Exception) {
                            Platform.DIRECT
                        }
                        PlatformBadge(platform = platform)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.title,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row {
                        IconButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Check out this downloaded video: ${item.title}\n${item.originalUrl}")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Video")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier.testTag("share_video_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_player_button")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Video viewport
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showControls = !showControls },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Poster / Media Content
                        AsyncImage(
                            model = item.thumbnailUrl,
                            contentDescription = item.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Vignette overlay
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.4f),
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.8f)
                                        )
                                    )
                                )
                        )

                        // Center Play/Pause button
                        if (showControls || !isPlaying) {
                            Row(
                                modifier = Modifier.align(Alignment.Center),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        playbackProgress = (playbackProgress - (5f / totalDurationSec)).coerceAtLeast(0f)
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                ) {
                                    Icon(Icons.Default.FastRewind, contentDescription = "Rewind 5s", tint = Color.White)
                                }

                                IconButton(
                                    onClick = { isPlaying = !isPlaying },
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                                        .testTag("player_play_pause_button")
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "Pause" else "Play",
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        playbackProgress = (playbackProgress + (5f / totalDurationSec)).coerceAtMost(1f)
                                    },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                ) {
                                    Icon(Icons.Default.FastForward, contentDescription = "Forward 5s", tint = Color.White)
                                }
                            }
                        }

                        // Resolution Tag in video corner
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                        ) {
                            ResolutionBadge(resolution = item.resolutionLabel)
                        }

                        // Bottom progress in player
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Slider(
                                value = playbackProgress,
                                onValueChange = {
                                    playbackProgress = it
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val currentSec = (playbackProgress * totalDurationSec).toInt()
                                val currentMin = currentSec / 60
                                val curSecRem = currentSec % 60
                                val totalMin = totalDurationSec / 60
                                val totalSecRem = totalDurationSec % 60

                                Text(
                                    text = String.format("%02d:%02d / %02d:%02d", currentMin, curSecRem, totalMin, totalSecRem),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { isMuted = !isMuted },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                            contentDescription = "Mute",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { isLooping = !isLooping },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Repeat,
                                            contentDescription = "Loop",
                                            tint = if (isLooping) MaterialTheme.colorScheme.primary else Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Controls & Details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Playback Speed",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("0.5x", "1.0x", "1.25x", "1.5x", "2.0x").forEach { speed ->
                                val selected = selectedSpeed == speed
                                FilterChip(
                                    selected = selected,
                                    onClick = { selectedSpeed = speed },
                                    label = { Text(speed, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFF1E293B),
                                        labelColor = Color(0xFF94A3B8)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Media Specifications",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SpecItem(label = "Resolution", value = item.resolutionLabel)
                            SpecItem(label = "Format", value = item.format)
                            SpecItem(label = "Size", value = item.getFormattedDownloaded())
                            SpecItem(label = "FPS", value = "${item.fps} fps")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecItem(label: String, value: String) {
    Column {
        Text(text = label, color = Color(0xFF64748B), fontSize = 11.sp)
        Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
