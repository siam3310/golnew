package com.angeldevtech.gol.ui.screens.player

import android.content.Context
import android.net.Uri
import android.view.SurfaceView
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.ProgressiveMediaSource

@OptIn(UnstableApi::class)
class FastMedia3Player(private val context: Context) {

    // 1. Ultra-fast buffering and hardware decoder initialization
    val exoPlayer: ExoPlayer = ExoPlayer.Builder(
        context,
        DefaultRenderersFactory(context).apply {
            setExtensionRendererMode(EXTENSION_RENDERER_MODE_OFF)
            setEnableDecoderFallback(true)
        }
    ).apply {
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs = */ 15_000,
                /* maxBufferMs = */ 50_000,
                /* bufferForPlaybackMs = */ 2_500, // Starts immediately with only 2.5s buffer
                /* bufferForPlaybackAfterRebufferMs = */ 5_000
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()
        setLoadControl(loadControl)
    }.build().apply {
        playWhenReady = true
    }

    private val contentTypeAttempts = mutableMapOf<Int, Boolean>()

    // 2. Anti-blocking network headers and multi-protocol fallback
    fun playStream(url: String, forcedType: Int? = null) {
        val uri = Uri.parse(url)
        val userAgent = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36"

        val defaultHeaders = mutableMapOf(
            "User-Agent" to userAgent,
            "Accept" to "*/*",
            "Accept-Language" to "en-US,en;q=0.9",
            "Connection" to "keep-alive"
        )
        uri.host?.let { host ->
            val scheme = uri.scheme ?: "https"
            defaultHeaders["Origin"] = "$scheme://$host"
            defaultHeaders["Referer"] = "$scheme://$host/"
        }

        val httpDataSourceFactory = DefaultHttpDataSource.Factory().apply {
            setUserAgent(userAgent)
            setConnectTimeoutMs(8_000)
            setReadTimeoutMs(10_000)
            setKeepPostFor302Redirects(true)
            setAllowCrossProtocolRedirects(true)
            setDefaultRequestProperties(defaultHeaders)
        }
        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)

        val isMkv = url.contains(".mkv", ignoreCase = true) || url.contains("matroska", ignoreCase = true)
        val isMp4 = url.contains(".mp4", ignoreCase = true)
        val isHls = url.contains(".m3u8", ignoreCase = true)

        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .apply {
                if (isMkv) {
                    setMimeType(androidx.media3.common.MimeTypes.VIDEO_MATROSKA)
                } else if (isMp4) {
                    setMimeType(androidx.media3.common.MimeTypes.VIDEO_MP4)
                } else if (isHls) {
                    setMimeType(androidx.media3.common.MimeTypes.APPLICATION_M3U8)
                }
            }
            .build()

        val targetType = forcedType ?: when {
            isHls -> C.CONTENT_TYPE_HLS
            isMkv || isMp4 -> C.CONTENT_TYPE_OTHER
            else -> Util.inferContentType(uri)
        }

        val mediaSource = when (targetType) {
            C.CONTENT_TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
            C.CONTENT_TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
            C.CONTENT_TYPE_RTSP -> RtspMediaSource.Factory().createMediaSource(mediaItem)
            C.CONTENT_TYPE_OTHER -> ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
            else -> DefaultMediaSourceFactory(dataSourceFactory).createMediaSource(mediaItem)
        }

        contentTypeAttempts[targetType] = true
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    // 3. Live window auto-recovery and container error handler
    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                    exoPlayer.seekToDefaultPosition()
                    exoPlayer.prepare()
                } else if (error.errorCode == PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED) {
                    val uri = exoPlayer.currentMediaItem?.localConfiguration?.uri ?: return
                    if (contentTypeAttempts[C.CONTENT_TYPE_HLS] != true) {
                        playStream(uri.toString(), C.CONTENT_TYPE_HLS)
                    } else if (contentTypeAttempts[C.CONTENT_TYPE_DASH] != true) {
                        playStream(uri.toString(), C.CONTENT_TYPE_DASH)
                    } else if (contentTypeAttempts[C.CONTENT_TYPE_OTHER] != true) {
                        playStream(uri.toString(), C.CONTENT_TYPE_OTHER)
                    }
                }
            }
        })
    }

    fun attachSurface(surfaceView: SurfaceView) {
        exoPlayer.setVideoSurfaceView(surfaceView)
    }

    fun setMuted(muted: Boolean) {
        exoPlayer.volume = if (muted) 0f else 1f
    }

    fun isMuted(): Boolean = exoPlayer.volume == 0f

    fun toggleMute(): Boolean {
        val newMuted = exoPlayer.volume > 0f
        setMuted(newMuted)
        return newMuted
    }

    fun getCurrentPosition(): Long = exoPlayer.currentPosition

    fun getDuration(): Long {
        val dur = exoPlayer.duration
        return if (dur > 0 && dur != androidx.media3.common.C.TIME_UNSET) dur else 0L
    }

    fun seekTo(positionMs: Long) {
        val duration = getDuration()
        val target = if (duration > 0) positionMs.coerceIn(0L, duration) else positionMs.coerceAtLeast(0L)
        exoPlayer.seekTo(target)
    }

    fun seekBy(deltaMs: Long) {
        seekTo(exoPlayer.currentPosition + deltaMs)
    }

    fun release() {
        exoPlayer.release()
    }
}
