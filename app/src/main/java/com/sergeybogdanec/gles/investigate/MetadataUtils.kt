package com.sergeybogdanec.gles.investigate

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Size

object MetadataUtils {

    fun getVideoSize(context: Context, path: Uri): Size {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, path)
        val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!.toInt()
        val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!.toInt()
        return Size(width, height)
    }

    fun getVideoRotation(context: Context, path: Uri): Int {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, path)
        return retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toInt() ?: 0
    }
}