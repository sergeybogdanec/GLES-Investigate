package com.sergeybogdanec.gles.investigate.view

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sergeybogdanec.gles.investigate.renderer.MyGLRenderer

class MyGLSurfaceView(context: Context): GLSurfaceView(context) {

    private val _currentTexture = MutableLiveData<SurfaceTexture>()
    val currentTexture: LiveData<SurfaceTexture> = _currentTexture

    private val renderer = MyGLRenderer(context) {
        _currentTexture.value?.release()
        _currentTexture.value = it
    }

    init {
        setEGLContextClientVersion(3)
        setRenderer(renderer)
        preserveEGLContextOnPause = true
        setZOrderOnTop(false)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        holder.setFormat(PixelFormat.RGBA_8888)
    }
}
