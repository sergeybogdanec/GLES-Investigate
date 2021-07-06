package com.sergeybogdanec.gles.investigate

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sergeybogdanec.gles.investigate.view.MyGLSurfaceView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val glView = MyGLSurfaceView(this)
        setContentView(glView)
    }
}
