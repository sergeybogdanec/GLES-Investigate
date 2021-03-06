package com.sergeybogdanec.gles.investigate.model

import android.content.res.AssetManager
import android.opengl.GLES31
import android.os.Build.VERSION_CODES.P
import android.util.Log
import java.io.InputStreamReader
import kotlin.IllegalStateException

class ShaderProgram(
    vertexShaderAssetName: String,
    fragmentShaderAssetName: String,
    private val assets: AssetManager,
) {

    private var vertexShaderId: Int = 0
    private var fragmentShaderId: Int = 0

    var programId: Int = 0

    init {
        vertexShaderId = loadShader(GLES31.GL_VERTEX_SHADER, vertexShaderAssetName)
        fragmentShaderId = loadShader(GLES31.GL_FRAGMENT_SHADER, fragmentShaderAssetName)

        val program = GLES31.glCreateProgram()

        if (program == 0) throw IllegalStateException("Can't create program!")

        GLES31.glAttachShader(program, vertexShaderId)
        GLES31.glAttachShader(program, fragmentShaderId)

        GLES31.glLinkProgram(program)

        val linked = IntArray(1)

        GLES31.glGetProgramiv(program, GLES31.GL_LINK_STATUS, linked, 0)

        if (linked[0] == 0) {
            Log.e("Sergey", GLES31.glGetProgramInfoLog(program))
            GLES31.glDeleteProgram(program)
            throw IllegalStateException("Program is not linked!")
        }

        programId = program
    }

    private fun loadShader(type: Int, shaderName: String): Int {
        val shaderId = GLES31.glCreateShader(type)

        if (shaderId != 0) {
            val error = IntArray(1)
            val shaderCode = InputStreamReader(assets.open(shaderName))
                .readLines()
                .joinToString("\n")
            GLES31.glShaderSource(shaderId, shaderCode)
            GLES31.glCompileShader(shaderId)
            GLES31.glGetShaderiv(shaderId, GLES31.GL_COMPILE_STATUS, error, 0)

            if (error[0] == 0) {
                throw IllegalStateException("Cannot load shader! code ${error[0]} log ${GLES31.glGetShaderInfoLog(shaderId)}")
            }
            return shaderId
        }

        throw IllegalStateException("Cannot load shader! log ${GLES31.glGetShaderInfoLog(shaderId)}")
    }

    fun release() {
        GLES31.glDeleteShader(programId)
        GLES31.glDeleteShader(vertexShaderId)
        GLES31.glDeleteShader(fragmentShaderId)
    }
}
