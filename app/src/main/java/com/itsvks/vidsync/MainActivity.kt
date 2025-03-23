package com.itsvks.vidsync

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.itsvks.vidsync.ui.component.ProgressDialog
import com.itsvks.vidsync.ui.component.VidSyncButton
import com.itsvks.vidsync.ui.component.VidSyncTextField
import com.itsvks.vidsync.ui.theme.VidSyncTheme
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : ComponentActivity() {

    @Suppress("LocalVariableName")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VidSyncTheme {
                var showUpdatingDialog by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    showUpdatingDialog = true

                    withContext(Dispatchers.IO) {
                        runCatching {
                            YoutubeDL.getInstance().updateYoutubeDL(applicationContext, YoutubeDL.UpdateChannel.STABLE)
                        }.onFailure {
                            showUpdatingDialog = false

                            Log.e("aaaa", it.message, it)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@MainActivity, it.message.toString(), Toast.LENGTH_LONG).show()
                            }
                        }.onSuccess {
                            showUpdatingDialog = false

                            Log.i("aaaa", "YoutubeDL updated")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@MainActivity, "updated", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                if (showUpdatingDialog) {
                    ProgressDialog(
                        onDismissRequest = { showUpdatingDialog = false },
                        message = "Checking for updates..."
                    )
                }

                val scope = rememberCoroutineScope()
                val uriHandler = LocalUriHandler.current

                var url by remember { mutableStateOf("") }
                var progress by remember { mutableFloatStateOf(0f) }
                var etaInSeconds by remember { mutableLongStateOf(1L) }
                var showDownloadingDialog by remember { mutableStateOf(false) }

                var playableLink: String? by remember { mutableStateOf(null) }

                LaunchedEffect(url) {
                    playableLink = null

                    withContext(Dispatchers.IO) {
                        if (URLUtil.isValidUrl(url)) {
                            val request = YoutubeDLRequest(url).apply {
                                addOption("-f", "best")
                            }

                            runCatching {
                                val streamInfo = YoutubeDL.getInstance().getInfo(request = request)
                                playableLink = streamInfo.url
                            }.onFailure {
                                Log.e("aaaa", it.message, it)
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@MainActivity, it.message.toString(), Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (URLUtil.isValidUrl(url)) {
                            Column {
                                if (!playableLink.isNullOrEmpty()) {
                                    Text(text = buildAnnotatedString {
                                        append("Video Link: ")
                                        withLink(
                                            LinkAnnotation.Url(
                                                url = playableLink!!,
                                                styles = TextLinkStyles(
                                                    style = SpanStyle(
                                                        color = MaterialTheme.colorScheme.primary,
                                                        textDecoration = TextDecoration.Underline
                                                    ),
                                                    pressedStyle = SpanStyle(
                                                        color = MaterialTheme.colorScheme.secondary,
                                                        textDecoration = TextDecoration.Underline
                                                    ),
                                                ),
                                                linkInteractionListener = {
                                                    val uri = (it as LinkAnnotation.Url).url
                                                    uriHandler.openUri(uri)
                                                }
                                            )
                                        ) { append(playableLink!!) }
                                    })
                                } else {
                                    Text(text = "Loading info...")
                                }

                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }

                        VidSyncTextField(
                            text = url,
                            onTextChange = {
                                url = it
                            },
                            placeholder = "Enter video link",
                            isError = !URLUtil.isValidUrl(url)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        VidSyncButton(
                            text = "Download",
                            enabled = URLUtil.isValidUrl(url)
                        ) {
                            showDownloadingDialog = true

                            val downloadLocation = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "VidSync")
                            val request = YoutubeDLRequest(url).apply {
                                addOption("-o", "${downloadLocation.absolutePath}/%(title)s.%(ext)s")
                            }

                            scope.launch(Dispatchers.IO) {
                                runCatching {
                                    YoutubeDL.getInstance().execute(request) { _progress, _etaInSeconds, _ ->
                                        progress = _progress
                                        etaInSeconds = _etaInSeconds
                                    }
                                }.onFailure {
                                    Log.e("aaaa", it.message, it)
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(this@MainActivity, it.message.toString(), Toast.LENGTH_LONG).show()
                                    }
                                }.onSuccess {
                                    showDownloadingDialog = false
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(this@MainActivity, "Download Complete", Toast.LENGTH_SHORT).show()
                                    }
                                    sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(downloadLocation)))
                                }
                            }
                        }
                    }

                    if (showDownloadingDialog) {
                        val message = if (etaInSeconds <= 0L) {
                            "Starting..."
                        } else {
                            "Downloading..."
                        }

                        ProgressDialog(
                            onDismissRequest = {
                                showDownloadingDialog = false
                            },
                            progress = { progress / 100f },
                            message = "$message (ETA ${etaInSeconds.coerceAtLeast(0L)}s)"
                        )
                    }
                }
            }
        }
    }
}
