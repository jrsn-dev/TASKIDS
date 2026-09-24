package com.example.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.game.GameThemeKey
import com.example.ui.components.PrimaryTvButton
import com.example.ui.design.TaskIdsColors
import com.example.ui.navigation.AppScreen
import com.example.viewmodel.MainViewModel

@Composable
fun GameMissionScreen(viewModel: MainViewModel, taskId: Int) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        if (maxWidth < 700.dp) ChildMobileMission(viewModel, taskId) else GameMissionLandscape(viewModel, taskId)
    }
}

@Composable
private fun GameMissionLandscape(
    viewModel: MainViewModel,
    taskId: Int
) {
    val tasks by viewModel.tasks.collectAsState()
    val timer by viewModel.timerState.collectAsState()
    val child by viewModel.currentChild.collectAsState()

    val current = child ?: return
    val task = tasks.firstOrNull { it.id == taskId } ?: return
    val theme = GameThemeKey.from(current.gameTheme)

    val minutes = timer.remainingSeconds / 60
    val seconds = timer.remainingSeconds % 60

    Box(Modifier.fillMaxSize()) {
        GameWorldBackground(theme, Modifier.fillMaxSize())

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .background(Color.White.copy(alpha = 0.94f), RoundedCornerShape(30.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "MISSÃO EM ANDAMENTO",
                    color = TaskIdsColors.Muted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .background(TaskIdsColors.Yellow, RoundedCornerShape(26.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    GameIcon(
                        key = task.iconKey,
                        modifier = Modifier.size(58.dp),
                        tint = TaskIdsColors.Ink
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    task.title,
                    color = TaskIdsColors.Ink,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Text(
                    task.description,
                    color = TaskIdsColors.Muted,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.weight(1f))

                GameAvatar(
                    child = current,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                )

                Spacer(Modifier.weight(1f))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GameStatPill("ESTRELAS", "+${task.rewardStars}", accent = TaskIdsColors.Yellow)
                    GameStatPill("XP", "+${task.rewardXp}", accent = TaskIdsColors.Blue)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.White, RoundedCornerShape(32.dp))
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Mantenha o foco",
                        color = TaskIdsColors.Ink,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        "COMBO x${current.currentCombo}",
                        color = TaskIdsColors.Orange,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(Modifier.weight(1f))

                Text(
                    "%02d:%02d".format(minutes, seconds),
                    color = TaskIdsColors.Ink,
                    fontSize = 108.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { timer.progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth(0.78f)
                        .height(16.dp),
                    color = when {
                        timer.progress > 0.5f -> TaskIdsColors.Blue
                        timer.progress > 0.25f -> TaskIdsColors.Yellow
                        else -> TaskIdsColors.Pink
                    },
                    trackColor = Color(0xFFE6ECF5)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    if (timer.isRunning) "Missão ativa" else "Missão pausada",
                    color = TaskIdsColors.Muted,
                    fontSize = 13.sp
                )

                Spacer(Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PrimaryTvButton(
                        text = if (timer.isRunning) "PAUSAR" else "RETOMAR",
                        background = TaskIdsColors.Purple,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (timer.isRunning) viewModel.pauseTimer() else viewModel.resumeTimer()
                        }
                    )
                    PrimaryTvButton(
                        text = "+5 MIN",
                        background = TaskIdsColors.Blue,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.extendTimer(5) }
                    )
                    PrimaryTvButton(
                        text = "CONCLUIR",
                        background = TaskIdsColors.Green,
                        modifier = Modifier.weight(1.15f),
                        onClick = { viewModel.markTaskAsCompleted(task) }
                    )
                }

                Spacer(Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .background(TaskIdsColors.SoftBg, RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Text(
                        "Voltar ao mapa pausa o fluxo visual, mas o timer continua persistido.",
                        color = TaskIdsColors.Muted,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
