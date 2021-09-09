package com.sergeybogdanec.gles.investigate.renderer

import android.content.Context
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.opengl.GLES31
import android.opengl.GLSurfaceView
import android.util.Log
import java.io.InputStreamReader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer(
    private val context: Context,
    private val onTextureCreated: (SurfaceTexture) -> Unit
) : GLSurfaceView.Renderer {

    private var programObject: Int = 0

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig?) {
        GLES31.glClearColor(0f, 0f, 0f, 1f)
        GLES31.glEnable(GLES31.GL_CULL_FACE)
        GLES31.glEnable(GLES31.GL_DEPTH_TEST)

        val textures = IntArray(1)
        GLES31.glGenTextures(1, textures, 0)

        val surfaceTexture = SurfaceTexture(textures.first())
        onTextureCreated(surfaceTexture)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES31.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT)
        GLES31.glClear(GLES31.GL_DEPTH_BUFFER_BIT or GLES31.GL_COLOR_BUFFER_BIT)

        GLES31.glDisable(GLES31.GL_DITHER)
        GLES31.glDisable(GLES31.GL_DEPTH_TEST)
        GLES31.glEnable(GLES31.GL_BLEND)

        GLES31.glUseProgram(programObject)

        GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, 3)
        GLES31.glDisableVertexAttribArray(0)
    }
}
