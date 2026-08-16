package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.DownloadEntity
import com.example.data.model.Platform
import com.example.data.model.QualityOption
import com.example.data.model.VideoMetadata
import com.example.data.parser.VideoUrlParser
import com.example.data.repository.DownloadRepository
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.MohaThemeStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DownloaderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DownloadRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DownloadRepository(application, database.downloadDao())
    }

    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _themeStyle = MutableStateFlow(MohaThemeStyle.SIGNATURE)
    val themeStyle: StateFlow<MohaThemeStyle> = _themeStyle.asStateFlow()

    private val _urlInput = MutableStateFlow("")
    val urlInput: StateFlow<String> = _urlInput.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _extractedMetadata = MutableStateFlow<VideoMetadata?>(null)
    val extractedMetadata: StateFlow<VideoMetadata?> = _extractedMetadata.asStateFlow()

    private val _selectedQuality = MutableStateFlow<QualityOption?>(null)
    val selectedQuality: StateFlow<QualityOption?> = _selectedQuality.asStateFlow()

    private val _noWatermark = MutableStateFlow(true)
    val noWatermark: StateFlow<Boolean> = _noWatermark.asStateFlow()

    private val _autoStripMetadata = MutableStateFlow(true)
    val autoStripMetadata: StateFlow<Boolean> = _autoStripMetadata.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Downloader, 1: Queue, 2: Library
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedPlatformFilter = MutableStateFlow<Platform?>(null)
    val selectedPlatformFilter: StateFlow<Platform?> = _selectedPlatformFilter.asStateFlow()

    private val _currentlyPlaying = MutableStateFlow<DownloadEntity?>(null)
    val currentlyPlaying: StateFlow<DownloadEntity?> = _currentlyPlaying.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    val allDownloads: StateFlow<List<DownloadEntity>> = repository.allDownloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeDownloads: StateFlow<List<DownloadEntity>> = repository.activeDownloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedDownloads: StateFlow<List<DownloadEntity>> = repository.completedDownloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentDownloads: StateFlow<List<DownloadEntity>> = repository.recentDownloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredCompletedDownloads: StateFlow<List<DownloadEntity>> = combine(
        repository.completedDownloads,
        _searchQuery,
        _selectedPlatformFilter
    ) { downloads, query, platformFilter ->
        downloads.filter { item ->
            val matchesQuery = query.isBlank() || item.title.contains(query, ignoreCase = true) || item.author.contains(query, ignoreCase = true)
            val matchesPlatform = platformFilter == null || item.platform == platformFilter.name
            matchesQuery && matchesPlatform
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onUrlChanged(newUrl: String) {
        _urlInput.value = newUrl
        if (newUrl.isBlank()) {
            _extractedMetadata.value = null
            _selectedQuality.value = null
        }
    }

    fun pasteAndAnalyze(text: String, isFallbackSample: Boolean = false) {
        val trimmed = text.trim()
        _urlInput.value = trimmed
        if (trimmed.isNotBlank()) {
            if (isFallbackSample) {
                _snackbarMessage.value = "Clipboard was empty: loaded sample video URL"
            } else {
                _snackbarMessage.value = "Pasted link from clipboard"
            }
            analyzeUrl(trimmed)
        } else {
            _snackbarMessage.value = "Clipboard is empty. Copy a video link first!"
        }
    }

    fun showClipboardMessage(message: String) {
        _snackbarMessage.value = message
    }

    fun clearInput() {
        _urlInput.value = ""
        _extractedMetadata.value = null
        _selectedQuality.value = null
    }

    fun analyzeUrl(urlToAnalyze: String? = null) {
        val targetUrl = urlToAnalyze ?: _urlInput.value
        if (targetUrl.isBlank()) {
            _snackbarMessage.value = "Please enter or paste a valid video URL"
            return
        }

        viewModelScope.launch {
            _isAnalyzing.value = true
            _extractedMetadata.value = null
            _selectedQuality.value = null

            // Realistic parser latency simulation
            delay(650)

            try {
                val metadata = VideoUrlParser.parseUrl(targetUrl)
                _extractedMetadata.value = metadata
                // Select default recommended quality
                val defaultOption = metadata.qualityOptions.firstOrNull { it.isRecommended }
                    ?: metadata.qualityOptions.firstOrNull()
                _selectedQuality.value = defaultOption
                _snackbarMessage.value = "Parsed video: ${metadata.platform.displayName}"
            } catch (e: Exception) {
                _snackbarMessage.value = "Failed to parse link: ${e.localizedMessage}"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun selectQuality(quality: QualityOption) {
        _selectedQuality.value = quality
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
        _snackbarMessage.value = when (mode) {
            AppThemeMode.SYSTEM -> "Theme: System Default"
            AppThemeMode.LIGHT -> "Theme: Light Mode"
            AppThemeMode.DARK -> "Theme: Dark Mode"
        }
    }

    fun toggleThemeMode() {
        val nextMode = when (_themeMode.value) {
            AppThemeMode.SYSTEM -> AppThemeMode.DARK
            AppThemeMode.DARK -> AppThemeMode.LIGHT
            AppThemeMode.LIGHT -> AppThemeMode.SYSTEM
        }
        setThemeMode(nextMode)
    }

    fun setThemeStyle(style: MohaThemeStyle) {
        _themeStyle.value = style
        _snackbarMessage.value = "Theme Palette: ${style.displayName}"
    }

    fun toggleThemeStyle() {
        val nextStyle = when (_themeStyle.value) {
            MohaThemeStyle.SIGNATURE -> MohaThemeStyle.SUNSET_GOLD
            MohaThemeStyle.SUNSET_GOLD -> MohaThemeStyle.CYBER_CYAN
            MohaThemeStyle.CYBER_CYAN -> MohaThemeStyle.SIGNATURE
        }
        setThemeStyle(nextStyle)
    }

    fun toggleNoWatermark() {
        _noWatermark.value = !_noWatermark.value
    }

    fun toggleAutoStripMetadata() {
        _autoStripMetadata.value = !_autoStripMetadata.value
        _snackbarMessage.value = if (_autoStripMetadata.value) {
            "Auto Metadata Remover: Enabled (EXIF, GPS & tags cleaned)"
        } else {
            "Auto Metadata Remover: Disabled"
        }
    }

    fun setTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setPlatformFilter(platform: Platform?) {
        _selectedPlatformFilter.value = platform
    }

    fun quickDownload(urlToDownload: String? = null) {
        val targetUrl = (urlToDownload ?: _urlInput.value).trim()
        if (targetUrl.isBlank()) {
            _snackbarMessage.value = "Please enter or paste a valid video URL"
            return
        }

        viewModelScope.launch {
            val currentMeta = _extractedMetadata.value
            val (meta, quality) = if (currentMeta != null && currentMeta.originalUrl == targetUrl) {
                val chosenQuality = _selectedQuality.value
                    ?: currentMeta.qualityOptions.firstOrNull { it.isRecommended }
                    ?: currentMeta.qualityOptions.firstOrNull()
                Pair(currentMeta, chosenQuality)
            } else {
                _isAnalyzing.value = true
                try {
                    val parsed = VideoUrlParser.parseUrl(targetUrl)
                    _extractedMetadata.value = parsed
                    val defaultOption = parsed.qualityOptions.firstOrNull { it.isRecommended }
                        ?: parsed.qualityOptions.firstOrNull()
                    _selectedQuality.value = defaultOption
                    Pair(parsed, defaultOption)
                } catch (e: Exception) {
                    _snackbarMessage.value = "Failed to extract link: ${e.localizedMessage}"
                    Pair(null, null)
                } finally {
                    _isAnalyzing.value = false
                }
            }

            if (meta != null && quality != null) {
                repository.startDownload(meta, quality)
                _snackbarMessage.value = "Download started: ${quality.label}"
                _selectedTab.value = 1 // Switch to active queue tab
            }
        }
    }

    fun startDownload(overrideQuality: QualityOption? = null) {
        val meta = _extractedMetadata.value
        val quality = overrideQuality ?: _selectedQuality.value
        if (meta == null || quality == null) {
            _snackbarMessage.value = "Please enter a valid link first"
            return
        }

        viewModelScope.launch {
            repository.startDownload(meta, quality)
            _snackbarMessage.value = "Download started: ${quality.label}"
            _selectedTab.value = 1 // Switch to active queue tab
        }
    }

    fun pauseDownload(id: String) {
        viewModelScope.launch {
            repository.pauseDownload(id)
        }
    }

    fun resumeDownload(id: String) {
        viewModelScope.launch {
            repository.resumeDownload(id)
        }
    }

    fun cancelDownload(id: String) {
        viewModelScope.launch {
            repository.cancelDownload(id)
            _snackbarMessage.value = "Download canceled"
        }
    }

    fun deleteDownload(id: String) {
        viewModelScope.launch {
            repository.deleteDownload(id)
            _snackbarMessage.value = "Item removed"
        }
    }

    fun clearAllLibrary() {
        viewModelScope.launch {
            repository.clearAll()
            _snackbarMessage.value = "Library cleared"
        }
    }

    fun openPlayer(item: DownloadEntity) {
        _currentlyPlaying.value = item
    }

    fun closePlayer() {
        _currentlyPlaying.value = null
    }

    fun dismissSnackbar() {
        _snackbarMessage.value = null
    }
}
