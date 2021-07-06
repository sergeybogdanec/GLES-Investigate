package com.sergeybogdanec.gles.investigate.view

import android.content.Context
import android.opengl.GLSurfaceView
import com.sergeybogdanec.gles.investigate.renderer.MyGLRenderer

class MyGLSurfaceView(context: Context): GLSurfaceView(context) {

    private val renderer = MyGLRenderer(context)

    init {
        setEGLContextClientVersion(3)
        setRenderer(renderer)
    }

}
