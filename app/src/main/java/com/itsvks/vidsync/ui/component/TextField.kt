package com.itsvks.vidsync.ui.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import com.itsvks.vidsync.ui.theme.VidSyncTheme

@Composable
fun VidSyncTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        placeholder = {
            Text(text = placeholder)
        },
        isError = isError
    )
}

@Preview(wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE)
@Composable
fun VidSyncTextFieldPreview() {
    VidSyncTheme {
        var value by remember { mutableStateOf("") }

        VidSyncTextField(
            text = value,
            onTextChange = {
                value = it
            },
            placeholder = "Placeholder"
        )
    }
}
