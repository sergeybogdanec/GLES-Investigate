package com.sergeybogdanec.gles.investigate.model

import android.content.res.AssetManager
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLES31
import android.opengl.Matrix
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PreviewFrame(
    private val assets: AssetManager,
    private val textureId: Int,
    private val frameBufferId: Int
) {

    companion object {
        private const val VERTICES_STRIDE = 5 * Float.SIZE_BYTES
        private const val VERTICES_XYZ_SIZE = 3
        private const val VERTICES_UV_SIZE = 2
        private const val VERTICES_XYZ_OFFSET = 0
        private const val VERTICES_UV_OFFSET = VERTICES_XYZ_SIZE * Float.SIZE_BYTES
    }

    private val tMatrix = FloatArray(16)
    private val vpMatrix = FloatArray(16)

    private val aPositionHandle: Int
        get() = getHandle("aPosition")

    private val aTextureCoordHandle: Int
        get() = getHandle("aTextureCoord")

    private val sTextureHandle: Int
        get() = getHandle("sTexture")

    private val uMVPMatrixHandle: Int
        get() = getHandle("uMVPMatrix")

    private val uTMatrixHandle: Int
        get() = getHandle("uTMatrix")

    private var vertexBuffer: Int = 0
    private val vertices = arrayOf(
        // X, Y, Z, U, V
        -1f, 1f, 0f, 0f, 1f,
        1f, 1f, 0f, 1f, 1f,
        -1f, -1f, 0f, 0f, 0f,
        1f, -1f, 0f, 1f, 0f
    ).toFloatArray()

    private val verticesBuffer
        get() = ByteBuffer
            .allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertices).position(0)
            }

    private var _shaderProgram: ShaderProgram? = null
    private val shaderProgram: ShaderProgram
        get() = _shaderProgram ?: throw IllegalStateException("PreviewFrame must be set up before using!")

    private val handleCache = hashMapOf<String, Int>()
    private fun getHandle(name: String): Int {
        handleCache[name]?.let { value ->
            return value
        }

        val program = shaderProgram.programId
        val location = GLES31.glGetUniformLocation(program, name)
            .takeIf { it != -1 }
            ?: GLES31.glGetAttribLocation(program, name)

        if (location == -1)
            throw IllegalStateException("Cannot get attrib/uniform location!")

        handleCache[name] = location

        return location
    }

    fun setup() {
        _shaderProgram = ShaderProgram(
            vertexShaderAssetName = "preview_vertex_shader.glsl",
            fragmentShaderAssetName = "preview_fragment_shader.glsl",
            assets = assets
        )

        val buffers = IntArray(1) { 0 }
        GLES31.glGenBuffers(1, buffers, 0)
        vertexBuffer = buffers[0]

        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, vertexBuffer)
        GLES31.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices.size * Float.SIZE_BYTES, verticesBuffer, GLES20.GL_STATIC_DRAW)
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0)
    }

    fun draw(width: Int, height: Int) {
        initViewPort(width, height)

        val ratio = width.toFloat() / height

        Matrix.setIdentityM(tMatrix, 0)
        Matrix.rotateM(tMatrix, 0, 0f, 0f, 0f, 1f)
        Matrix.scaleM(tMatrix, 0, ratio, 1f, 1f)
        Matrix.scaleM(tMatrix, 0, 1f, 1f, 1f)

        GLES31.glUseProgram(shaderProgram.programId)

        GLES31.glBindFramebuffer(GLES31.GL_FRAMEBUFFER, frameBufferId)

        GLES31.glUniform1i(sTextureHandle, 0)
        GLES31.glActiveTexture(GLES31.GL_TEXTURE0)
        GLES31.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)

        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, vertexBuffer)

        GLES31.glEnableVertexAttribArray(aPositionHandle)
        GLES31.glVertexAttribPointer(aPositionHandle, VERTICES_XYZ_SIZE, GLES31.GL_FLOAT, false, VERTICES_STRIDE, VERTICES_XYZ_OFFSET)

        GLES31.glEnableVertexAttribArray(aTextureCoordHandle)
        GLES31.glVertexAttribPointer(aTextureCoordHandle, VERTICES_UV_SIZE, GLES31.GL_FLOAT, false, VERTICES_STRIDE, VERTICES_UV_OFFSET)

        GLES20.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, vpMatrix, 0)
        GLES20.glUniformMatrix4fv(uTMatrixHandle, 1, false, tMatrix, 0)

        GLES31.glDrawArrays(GLES31.GL_TRIANGLE_STRIP, 0, 4)

        Log.d("Sergey", "Draw")

        GLES31.glDisableVertexAttribArray(aPositionHandle)
        GLES31.glDisableVertexAttribArray(aTextureCoordHandle)
        GLES31.glBindBuffer(GLES31.GL_ARRAY_BUFFER, 0)
        GLES31.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        GLES31.glUseProgram(0)
        //GLES31.glFinish()
    }

    private fun initViewPort(width: Int, height: Int) {
        GLES31.glViewport(0, 0, width, height)

        val vMatrix = FloatArray(16)
        val pMatrix = FloatArray(16)

        val near = 1f
        val far = 100f
        val eyeZ = 1f
        Matrix.setLookAtM(vMatrix, 0,
            0.0f, 0.0f, eyeZ,
            0.0f, 0.0f, 0.0f,
            0.0f, 0.1f, 0.0f)

        val ratio = width.toFloat() / height.toFloat()
        Matrix.frustumM(pMatrix, 0, -ratio, ratio, -1.0f, 1.0f, near, far)
        Matrix.multiplyMM(vpMatrix, 0, pMatrix, 0, vMatrix, 0)
    }

    fun release() {
        handleCache.clear()
        _shaderProgram?.release()
        _shaderProgram = null
        GLES31.glDeleteBuffers(1, IntArray(vertexBuffer), 0)
        vertexBuffer = 0
    }
}
