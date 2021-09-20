package com.sergeybogdanec.gles.investigate.view

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sergeybogdanec.gles.investigate.renderer.MyGLRenderer

class MyGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): GLSurfaceView(context, attrs) {

    private val _currentTexture = MutableLiveData<SurfaceTexture>()
    val currentTexture: LiveData<SurfaceTexture> = _currentTexture

    private val renderer = MyGLRenderer(
        context = context,
        onTextureCreated = {
            Log.d("Sergey", "surface created")
            _currentTexture.value?.release()
            _currentTexture.postValue(it)
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
}
