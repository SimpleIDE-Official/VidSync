package com.itsvks.vidsync.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.itsvks.vidsync.ui.theme.VidSyncTheme

@Composable
fun ProgressDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    progress: (() -> Float)? = null,
    message: String = ""
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )
    ) {
        Card(
            modifier = modifier,
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                if (message.isNotBlank() && message.isNotEmpty()) {
                    if (progress != null) {
                        Text(text = "$message (${progress() * 100f}%)")
                    } else {
                        Text(text = message)
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                }

                progress?.let {
                    LinearProgressIndicator(
                        progress = it,
                        drawStopIndicator = {}
                    )
                } ?: run {
                    LinearProgressIndicator()
                }
            }
        }
    }
}

@Preview(wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE)
@Composable
fun ProgressDialogPreview() {
    VidSyncTheme {
        var show by remember { mutableStateOf(true) }
        val progress by animateFloatAsState(
            targetValue = 0.76f,
            visibilityThreshold = 0.1f,
            label = "progress",
        )

        ProgressDialog(
            onDismissRequest = { show = !show },
            progress = { progress },
            message = "Downloading..."
        )
    }
}
