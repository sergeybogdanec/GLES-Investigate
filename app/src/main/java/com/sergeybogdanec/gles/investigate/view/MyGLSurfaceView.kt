package com.sergeybogdanec.gles.investigate.view

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.SurfaceTexture
import android.net.Uri
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.Surface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.sergeybogdanec.gles.investigate.MetadataUtils
import com.sergeybogdanec.gles.investigate.renderer.MyGLRenderer

class MyGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): GLSurfaceView(context, attrs) {

    private val renderer = MyGLRenderer(
        context = context,
        onTextureCreated = {
            Log.d("Sergey", "surface created")
            post {
                player?.setVideoSurface(Surface(it))
            }
        },
        onRenderRequire = {
            Log.d("Sergey", "request render")
            requestRender()
        }
    )

    init {
        setEGLContextClientVersion(3)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
        preserveEGLContextOnPause = true
        setZOrderOnTop(true)
        holder.setFormat(PixelFormat.RGBA_8888)
    }

    var player: ExoPlayer? = null

    fun selectFile(uri: Uri) {
        val videoSize = MetadataUtils.getVideoSize(context, uri)
        val videoRotation = MetadataUtils.getVideoRotation(context, uri)
        renderer.setVideoParams(videoSize, videoRotation)
        player?.setMediaItem(MediaItem.fromUri(uri))
        player?.prepare()
    }
}
