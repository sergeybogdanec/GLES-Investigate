package com.sergeybogdanec.gles.investigate

import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.sergeybogdanec.gles.investigate.view.MyGLSurfaceView
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private val selectFileLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
    }

    private val player by lazy {
        SimpleExoPlayer.Builder(this).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    private val frameLayout by lazy {
        findViewById<FrameLayout>(R.id.glViewContainer)
    }
    private val toggleButton by lazy {
        findViewById<Button>(R.id.toggleButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val glView = MyGLSurfaceView(this)
        glView.currentTexture.observe(this) { surfaceTexture ->
            player.setVideoSurface(Surface(surfaceTexture))
        }

        frameLayout.addView(glView)

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
