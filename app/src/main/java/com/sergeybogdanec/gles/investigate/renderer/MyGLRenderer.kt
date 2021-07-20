package com.sergeybogdanec.gles.investigate.renderer

import android.content.Context
import android.graphics.Color
import android.opengl.GLES31
import android.opengl.GLSurfaceView
import android.util.Log
import java.io.InputStreamReader
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var programObject: Int = 0

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig?) {
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


}
