package com.itsvks.vidsync

import android.app.Application
import android.util.Log
import com.yausername.aria2c.Aria2c
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VidSync : Application() {

    companion object {
        private const val TAG = "VidSync"
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    YoutubeDL.getInstance().init(applicationContext)
                    FFmpeg.getInstance().init(applicationContext)
                    Aria2c.getInstance().init(applicationContext)
                } catch (e: YoutubeDLException) {
                    Log.e(TAG, "failed to initialize youtubedl-android", e)
                }
            }
        }
    }
}
