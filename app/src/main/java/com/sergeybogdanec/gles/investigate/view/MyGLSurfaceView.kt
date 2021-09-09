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

    private val renderer = MyGLRenderer(
        context = context,
        onTextureCreated = {
            _currentTexture.value?.release()
            _currentTexture.postValue(it)
        },
        onRenderRequire = {
            requestRender()
        }
    )

    init {
        setEGLContextClientVersion(3)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
        preserveEGLContextOnPause = true
        setZOrderOnTop(false)
        holder.setFormat(PixelFormat.RGBA_8888)
    }
}
