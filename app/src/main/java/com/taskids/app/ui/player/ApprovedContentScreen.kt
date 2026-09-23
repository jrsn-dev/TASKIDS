package com.taskids.app.ui.player

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.taskids.app.domain.model.ApprovedContent
import com.taskids.app.ui.components.FocusSurface
import com.taskids.app.ui.parental.ParentPinDialog
import com.taskids.app.ui.theme.Blue500
import com.taskids.app.ui.theme.Cloud100
import com.taskids.app.ui.theme.Cloud50
import com.taskids.app.ui.theme.Ink600
import com.taskids.app.ui.theme.Ink900
import com.taskids.app.ui.theme.Red500
import com.taskids.app.ui.theme.White
import com.taskids.app.ui.theme.Yellow500
import com.taskids.app.viewmodel.MainViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ApprovedContentScreen(viewModel: MainViewModel) {
    val content by viewModel.approvedContent.collectAsState()
    val remaining by viewModel.screenTimeRemainingSeconds.collectAsState()
    var selectedId by remember { mutableStateOf<Int?>(null) }
    var showPin by remember { mutableStateOf(false) }

    val selected = content.firstOrNull { it.id == selectedId } ?: content.firstOrNull()
    val minutes = remaining / 60
    val seconds = remaining % 60

    Row(Modifier.fillMaxSize().background(Cloud50)) {
        Column(
            modifier = Modifier.width(270.dp).fillMaxHeight().background(White).padding(18.dp)
        ) {
            Text("Tempo de diversão", color = Ink900, fontWeight = FontWeight.Black, fontSize = 21.sp)
            Spacer(Modifier.height(5.dp))
            Text("Somente conteúdos aprovados", color = Ink600, fontSize = 11.sp)
            Spacer(Modifier.height(14.dp))
            Box(
                Modifier.fillMaxWidth()
                    .background(Color(0xFFFFF2C8), RoundedCornerShape(15.dp))
                    .padding(13.dp)
            ) {
                Column {
                    Text("Tempo restante", color = Ink600, fontSize = 10.sp)
                    Text(
                        "%02d:%02d".format(minutes, seconds),
                        color = Ink900,
                        fontWeight = FontWeight.Black,
                        fontSize = 25.sp
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Escolha um vídeo", color = Ink900, fontWeight = FontWeight.Black, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                items(content.filter { it.isEnabled }, key = { it.id }) { item ->
                    FocusSurface(
                        onClick = { selectedId = item.id },
                        modifier = Modifier.fillMaxWidth(),
                        background = if (selected?.id == item.id) {
                            Color(0xFFE5EFFF)
                        } else {
                            Cloud100
                        },
                        focusColor = Blue500,
                        shape = RoundedCornerShape(13.dp),
                        padding = 10.dp
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("▶️", fontSize = 20.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                item.title,
                                color = Ink900,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            FocusSurface(
                onClick = { showPin = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                background = Red500,
                focusColor = Yellow500,
                shape = RoundedCornerShape(13.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Fechar com PIN", color = White, fontWeight = FontWeight.Black)
                }
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(18.dp)) {
            if (selected == null) {
                Column(
                    Modifier.fillMaxSize().background(White, RoundedCornerShape(24.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🛡️", fontSize = 58.sp)
                    Text(
                        "Nenhum conteúdo aprovado",
                        color = Ink900,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp
                    )
                    Text(
                        "Um responsável precisa adicionar vídeos específicos na Área dos Pais.",
                        color = Ink600,
                        fontSize = 12.sp
                    )
                }
            } else {
                key(selected.id) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize().background(
                            Color.Black,
                            RoundedCornerShape(24.dp)
                        ),
                        factory = { context ->
                            WebView(context).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.mediaPlaybackRequiresUserGesture = true
                                settings.setSupportMultipleWindows(false)
                                webViewClient = SafeYoutubeClient()
                                loadApprovedVideo(selected)
                            }
                        },
                        update = { view ->
                            if (view.tag != selected.youtubeVideoId) {
                                view.loadApprovedVideo(selected)
                            }
                        }
                    )
                }
            }
        }
    }

    if (showPin) {
        ParentPinDialog(
            viewModel = viewModel,
            title = "Responsável: encerrar tempo de tela?",
            onDismiss = { showPin = false },
            onVerified = {
                showPin = false
                viewModel.closeApprovedPlayer()
            }
        )
    }
}

private fun WebView.loadApprovedVideo(content: ApprovedContent) {
    tag = content.youtubeVideoId
    val safeId = content.youtubeVideoId.replace(Regex("[^A-Za-z0-9_-]"), "")
    val html = """
        <!doctype html>
        <html style="width:100%;height:100%"><head><meta name="viewport" content="width=device-width,initial-scale=1"/></head>
        <body style="margin:0;background:#000;overflow:hidden;width:100%;height:100%">
          <iframe width="100%" height="100%"
            src="https://www.youtube-nocookie.com/embed/$safeId?rel=0&modestbranding=1"
            title="Conteúdo aprovado"
            frameborder="0"
            allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture"
            allowfullscreen></iframe>
        </body></html>
    """.trimIndent()
    loadDataWithBaseURL(
        "https://www.youtube-nocookie.com",
        html,
        "text/html",
        "UTF-8",
        null
    )
}

private class SafeYoutubeClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        if (request?.isForMainFrame != true) return false
        val host = request.url.host.orEmpty().lowercase()
        return host !in setOf("www.youtube-nocookie.com", "youtube-nocookie.com")
    }

    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        val host = runCatching {
            Uri.parse(url).host.orEmpty().lowercase()
        }.getOrDefault("")
        return host !in setOf("www.youtube-nocookie.com", "youtube-nocookie.com")
    }
}
