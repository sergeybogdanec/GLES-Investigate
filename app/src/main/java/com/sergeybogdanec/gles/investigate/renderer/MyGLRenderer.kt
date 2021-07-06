package com.sergeybogdanec.gles.investigate.renderer

import android.content.Context
import android.graphics.Color
import android.opengl.GLES31
import android.opengl.GLSurfaceView
import android.util.Log
import com.sergeybogdanec.gles.investigate.shapes.MyGLTriangle
import java.io.InputStreamReader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var triangle: MyGLTriangle

    private var programObject: Int = 0

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig?) {
        triangle = MyGLTriangle(
            vertices = floatArrayOf(
                0f, .62f, 0f,
                -.5f, -.31f, 0f,
                .5f, -.31f, 0f
            ),
            color = Color . valueOf (Color.MAGENTA)
        )

        val vertexShader = loadShader(GLES31.GL_VERTEX_SHADER, "vertex_shader.glsl")
        val fragmentShader = loadShader(GLES31.GL_FRAGMENT_SHADER, "fragment_shader.glsl")
        val program = GLES31.glCreateProgram()

        if (program == 0) return

        GLES31.glAttachShader(program, vertexShader)
        GLES31.glAttachShader(program, fragmentShader)

        GLES31.glLinkProgram(program)

        val linked = IntArray(1)

        GLES31.glGetProgramiv(program, GLES31.GL_LINK_STATUS, linked, 0)

        if (linked[0] == 0) {
            Log.e("Sergey", GLES31.glGetProgramInfoLog(program))
            GLES31.glDeleteProgram(program)
            return
        }

        programObject = program
        GLES31.glClearColor(0f, 0f, 0f, 1f)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES31.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES31.glClear(GLES31.GL_COLOR_BUFFER_BIT)

        GLES31.glUseProgram(programObject)

        GLES31.glVertexAttribPointer(0, 3, GLES31.GL_FLOAT, false, 0, triangle.vertexBuffer)
        GLES31.glEnableVertexAttribArray(0)

        GLES31.glGetUniformLocation(programObject, "vColor").also { variableId ->
            GLES31.glUniform4fv(variableId, 1, triangle.color, 0)
        }

        GLES31.glDrawArrays(GLES31.GL_TRIANGLES, 0, 3)
        GLES31.glDisableVertexAttribArray(0)
    }

    private fun loadShader(type: Int, shaderName: String): Int {
        return GLES31.glCreateShader(type).also { shader ->
            val shaderCode = InputStreamReader(context.assets.open(shaderName))
                .readLines()
                .joinToString("\n")
            GLES31.glShaderSource(shader, shaderCode)
            GLES31.glCompileShader(shader)
        }
    }
}
