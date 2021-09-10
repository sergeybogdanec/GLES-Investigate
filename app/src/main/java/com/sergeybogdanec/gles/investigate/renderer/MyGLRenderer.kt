package com.sergeybogdanec.gles.investigate.renderer

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES
import android.opengl.GLES20
import android.opengl.GLES31
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.sergeybogdanec.gles.investigate.model.PreviewFrame
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer(
    private val context: Context,
    private val onTextureCreated: (SurfaceTexture) -> Unit,
    private val onRenderRequire: () -> Unit
) : GLSurfaceView.Renderer {

    private var _previewFrame: PreviewFrame? = null

    private var surfaceTexture: SurfaceTexture? = null
    private var isSurfaceUpdated: Boolean = false

    private var aspectRatio: Float = 1f
    private var surfaceWidth: Int = 0
    private var surfaceHeight: Int = 0

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig?) {
        GLES31.glEnable(GLES31.GL_CULL_FACE)
        GLES31.glEnable(GLES31.GL_DEPTH_TEST)
    }

    private fun initGl() {
        release()

        GLES31.glClearColor(0f, 0f, 0f, 1f)
        GLES31.glEnable(GLES31.GL_CULL_FACE)
        GLES31.glEnable(GLES31.GL_DEPTH_TEST)

        val textures = IntArray(1)
        GLES31.glGenTextures(1, textures, 0)
        val textureId = textures[0]

        val buffers = IntArray(1)
        GLES31.glGenFramebuffers(1, buffers, 0)

        val bufferId = buffers[0]
        GLES31.glBindFramebuffer(GLES31.GL_FRAMEBUFFER, bufferId)
        GLES31.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId)
        GLES31.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES31.GL_LINEAR)
        GLES31.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES31.GL_LINEAR)
        GLES31.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES31.glTexParameteri(GL_TEXTURE_EXTERNAL_OES , GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        GLES31.glTexImage2D(GLES31.GL_TEXTURE_2D, 0, GLES31.GL_RGBA, surfaceWidth, surfaceHeight, 0, GLES31.GL_RGBA, GLES31.GL_UNSIGNED_BYTE, null)
        GLES31.glFramebufferTexture2D(GLES31.GL_FRAMEBUFFER, GLES31.GL_COLOR_ATTACHMENT0, GLES31.GL_TEXTURE_2D, textureId, 0)

        GLES31.glBindFramebuffer(GLES31.GL_FRAMEBUFFER, 0)
        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, 0)

        _previewFrame = PreviewFrame(context.assets, textureId, bufferId).apply {
            setup()
        }

        surfaceTexture = SurfaceTexture(textureId)
            .also(onTextureCreated)
            .apply {
                setOnFrameAvailableListener {
                    Log.d("Sergey", "onFrameAvailable")
                    isSurfaceUpdated = true
                    onRenderRequire()
                }
            }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES31.glViewport(0, 0, width, height)

        aspectRatio = width.toFloat() / height

        surfaceWidth = width
        surfaceHeight = height

        initGl()
    }

    override fun onDrawFrame(unused: GL10) {
        synchronized(this) {
            if (isSurfaceUpdated) {
                isSurfaceUpdated = false
                surfaceTexture?.updateTexImage()
            }
        }

        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT)

        _previewFrame?.draw(surfaceWidth, surfaceHeight)
    }

    private fun release() {
        surfaceTexture?.release()
        surfaceTexture = null
        _previewFrame?.release()
        _previewFrame = null
    }
}
