package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PrimaryTvButton
import com.example.ui.design.TaskIdsColors
import com.example.ui.game.RewardVectorIcon
import com.example.ui.game.ChildMobileScreenTime
import com.example.ui.game.GameWorldBackground
import com.example.game.GameThemeKey
import com.example.viewmodel.MainViewModel

@Composable
fun ScreenTimeScreen(viewModel: MainViewModel) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        if (maxWidth < 700.dp) ChildMobileScreenTime(viewModel) else ScreenTimeLandscape(viewModel)
    }
}

@Composable
private fun ScreenTimeLandscape(viewModel: MainViewModel) {
    val remaining by viewModel.screenTimeRemainingSeconds.collectAsState()
    val child by viewModel.currentChild.collectAsState()
    val minutes = remaining / 60
    val seconds = remaining % 60

    Box(Modifier.fillMaxSize()) {
    GameWorldBackground(GameThemeKey.from(child?.gameTheme ?: "SKY"), Modifier.fillMaxSize())
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(26.dp)
            .background(Color.White, RoundedCornerShape(34.dp))
            .padding(34.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(TaskIdsColors.SoftBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    RewardVectorIcon(
                        type = "SCREEN_TIME",
                        modifier = Modifier.size(28.dp),
                        tint = TaskIdsColors.Blue
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "Tempo de tela aprovado",
                        color = TaskIdsColors.Ink,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "Área controlada pelos responsáveis",
                        color = TaskIdsColors.Muted,
                        fontSize = 13.sp
                    )
                }
                Spacer(Modifier.weight(1f))
                PrimaryTvButton(
                    text = "Encerrar",
                    background = TaskIdsColors.Pink,
                    onClick = { viewModel.stopScreenTime() }
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                "%02d:%02d".format(minutes, seconds),
                color = TaskIdsColors.Ink,
                fontSize = 100.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                "Tempo restante",
                color = TaskIdsColors.Muted,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(22.dp))

            LinearProgressIndicator(
                progress = { if (remaining > 0) 1f else 0f },
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(14.dp),
                color = TaskIdsColors.Green,
                trackColor = Color(0xFFE6ECF5)
            )

            Spacer(Modifier.height(26.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.70f)
                    .background(TaskIdsColors.SoftBg, RoundedCornerShape(24.dp))
                    .padding(22.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Aproveite este momento! Quando o tempo terminar, você poderá continuar suas missões.",
                    color = TaskIdsColors.Ink,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }

            Spacer(Modifier.weight(1f))
        }
    }
    }
}
