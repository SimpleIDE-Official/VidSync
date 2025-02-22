package com.itsvks.vidsync.util

import android.text.SpannableString
import android.text.style.URLSpan
import android.text.util.Linkify
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString

@Composable
fun String.linkify() = buildAnnotatedString {
    append(this@linkify)

    val spannable = SpannableString(this@linkify)
    Linkify.addLinks(spannable, Linkify.WEB_URLS)

    val uriHandler = LocalUriHandler.current

    val spans = spannable.getSpans(0, spannable.length, URLSpan::class.java)
    for (span in spans) {
        val start = spannable.getSpanStart(span)
        val end = spannable.getSpanEnd(span)

        addLink(
            LinkAnnotation.Url(
                url = span.url,
                linkInteractionListener = {
                    val url = (it as LinkAnnotation.Url).url
                    uriHandler.openUri(url)
                }
            ),
            start = start,
            end = end
        )
    }
}

fun AnnotatedString.urlAt(position: Int, onFound: (String) -> Unit) = getStringAnnotations(
    tag = "URL",
    start = position,
    end = position
).firstOrNull()?.item?.let { onFound(it) }
