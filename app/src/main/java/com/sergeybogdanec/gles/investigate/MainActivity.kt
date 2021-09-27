package com.sergeybogdanec.gles.investigate

import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.sergeybogdanec.gles.investigate.view.MyGLSurfaceView

class MainActivity : AppCompatActivity() {

    private val selectFileLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        glView.selectFile(uri)
    }

    private val player by lazy {
        SimpleExoPlayer.Builder(this).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    private val glView by lazy {
        findViewById<MyGLSurfaceView>(R.id.glView)
    }
    private val toggleButton by lazy {
        findViewById<Button>(R.id.toggleButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        glView.player = player

        toggleButton.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }

        selectFileLauncher.launch(arrayOf("video/*"))
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }
}
