package com.sergeybogdanec.gles.investigate.renderer

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.*
import android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES
import android.util.Log
import android.util.Size
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

    private var surfaceWidth: Int = 0
    private var surfaceHeight: Int = 0

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig?) {
        GLES31.glEnable(GLES31.GL_CULL_FACE)
        GLES31.glEnable(GLES31.GL_DEPTH_TEST)
    }

    private var width = 0
    private var height = 0
    private var rotation = 0

    fun setVideoParams(videoSize: Size, videoRotation: Int) {
        width = videoSize.width
        height = videoSize.height
        rotation = videoRotation
    }

    private fun initGl() {
        release()

        GLES31.glClearColor(0f, 0f, 0f, 1f)
        GLES31.glEnable(GLES31.GL_CULL_FACE)
        GLES31.glEnable(GLES31.GL_DEPTH_TEST)

        val textures = IntArray(1)
        GLES31.glGenTextures(1, textures, 0)
        val textureId = textures[0]

        GLES31.glBindTexture(GL_TEXTURE_EXTERNAL_OES, textureId)
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES31.GL_LINEAR)
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES31.GL_NEAREST)
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        GLES31.glBindTexture(GLES31.GL_TEXTURE_2D, 0)

        surfaceTexture = SurfaceTexture(textureId)
            .also { surfaceTex ->
                _previewFrame = PreviewFrame(context.assets, textureId, surfaceTex).apply {
                    setup()
                }
            }
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

        surfaceWidth = width
        surfaceHeight = height

        initGl()
    }

    override fun onDrawFrame(unused: GL10) {
        synchronized(this) {
            if (isSurfaceUpdated) {
                isSurfaceUpdated = false
                Log.d("Sergey", "update image")
                surfaceTexture?.updateTexImage()
            }
        }

        GLES31.glClearColor(0f, 0f, 0f, 1f)
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT or GLES30.GL_COLOR_BUFFER_BIT)

        _previewFrame?.draw(surfaceWidth, surfaceHeight, width, height, rotation)
    }

    private fun release() {
        surfaceTexture?.release()
        surfaceTexture = null
        _previewFrame?.release()
        _previewFrame = null
    }
}
