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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PrimaryTvButton
import com.example.ui.design.TaskIdsColors
import com.example.ui.navigation.AppScreen
import com.example.viewmodel.MainViewModel

@Composable
fun TimerScreenV2(viewModel: MainViewModel, taskId: Int) {
    val tasks by viewModel.tasks.collectAsState()
    val timer by viewModel.timerState.collectAsState()
    val child by viewModel.currentChild.collectAsState()

    val task = tasks.firstOrNull { it.id == taskId }

    if (task == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tarefa não encontrada", color = Color.White)
        }
        return
    }

    val minutes = timer.remainingSeconds / 60
    val seconds = timer.remainingSeconds % 60
    val formatted = "%02d:%02d".format(minutes, seconds)
    val elapsed = (timer.totalSeconds - timer.remainingSeconds).coerceAtLeast(0) / 60

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(0.88f)
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(
                        listOf(TaskIdsColors.Sky, TaskIdsColors.Blue)
                    ),
                    RoundedCornerShape(34.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                PrimaryTvButton(
                    text = "← Voltar",
                    background = Color.White.copy(alpha = 0.18f),
                    onClick = { viewModel.navigateTo(AppScreen.Home) }
                )
            }

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(190.dp)
                    .background(Color.White.copy(alpha = 0.18f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(task.icon, fontSize = 96.sp)
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "Você consegue, ${child?.name ?: "campeão(ã)"}!",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Text(
                task.description,
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.weight(1f))
        }

        Column(
            modifier = Modifier
                .weight(1.25f)
                .fillMaxHeight()
                .background(Color.White, RoundedCornerShape(34.dp))
                .padding(34.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(TaskIdsColors.SoftBlue, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(task.icon, fontSize = 30.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        task.title,
                        color = TaskIdsColors.Ink,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "Mantenha o foco! Você consegue.",
                        color = TaskIdsColors.Muted,
                        fontSize = 13.sp
                    )
                }
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .background(TaskIdsColors.SoftBlue, RoundedCornerShape(20.dp))
                        .padding(horizontal = 15.dp, vertical = 9.dp)
                ) {
                    Text(
                        "⭐ +${task.rewardStars}",
                        color = TaskIdsColors.Ink,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Text(
                formatted,
                modifier = Modifier.fillMaxWidth(),
                color = TaskIdsColors.Ink,
                fontSize = 94.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { timer.progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp),
                color = when {
                    timer.progress > 0.5f -> TaskIdsColors.Blue
                    timer.progress > 0.25f -> TaskIdsColors.Yellow
                    else -> TaskIdsColors.Pink
                },
                trackColor = Color(0xFFE4EAF2)
            )

            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "${elapsed} minutos concluídos",
                    color = TaskIdsColors.Muted,
                    fontSize = 12.sp
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "${task.durationMinutes} minutos",
                    color = TaskIdsColors.Muted,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PrimaryTvButton(
                    text = if (timer.isRunning) "⏸ Pausar" else "▶ Retomar",
                    background = TaskIdsColors.Purple,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (timer.isRunning) viewModel.pauseTimer() else viewModel.resumeTimer()
                    }
                )
                PrimaryTvButton(
                    text = "＋ 5 min",
                    background = TaskIdsColors.Blue,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.extendTimer(5) }
                )
                PrimaryTvButton(
                    text = "✓ Concluído",
                    background = TaskIdsColors.Green,
                    modifier = Modifier.weight(1.15f),
                    onClick = { viewModel.markTaskAsCompleted(task) }
                )
            }
        }
    }
}
