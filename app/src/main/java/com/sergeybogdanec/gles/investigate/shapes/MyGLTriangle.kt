package com.sergeybogdanec.gles.investigate.shapes

import android.graphics.Color
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class MyGLTriangle(
    private val vertices: FloatArray,
    color: Color
) {

    val color = floatArrayOf(color.red(), color.green(), color.blue(), color.alpha())

    val vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(vertices.size * Float.SIZE_BYTES).run {
        order(ByteOrder.nativeOrder())
        asFloatBuffer().apply {
            put(vertices)
            position(0)
        }
    }

}
