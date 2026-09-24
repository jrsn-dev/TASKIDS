package com.example.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.PrimaryTvButton
import com.example.ui.design.TaskIdsColors
import com.example.viewmodel.MainViewModel

@Composable
fun GameMissionScreen(
    viewModel: MainViewModel,
    taskId: Int
) {
    val tasks by viewModel.tasks.collectAsState()
    val timer by viewModel.timerState.collectAsState()
    val child by viewModel.currentChild.collectAsState()

    val current = child ?: return
    val task = tasks.firstOrNull { it.id == taskId } ?: return
    val minutes = timer.remainingSeconds / 60
    val seconds = timer.remainingSeconds % 60

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF8FBFF), Color(0xFFF3F6FF), Color(0xFFF9FAFF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.kids_task_logo_1780663813391),
                    contentDescription = "TASKIDS",
                    modifier = Modifier
                        .width(108.dp)
                        .height(38.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(Modifier.weight(1f))

                Text(
                    "MISSÃO EM ANDAMENTO",
                    color = TaskIdsColors.Purple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1.15f)
                        .fillMaxHeight()
                        .shadow(12.dp, RoundedCornerShape(32.dp))
                        .background(Color.White, RoundedCornerShape(32.dp))
                        .padding(26.dp)
                ) {
                    Text(
                        task.title,
                        color = TaskIdsColors.Ink,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Black
                    )

                    if (task.description.isNotBlank()) {
                        Spacer(Modifier.height(5.dp))
                        Text(
                            task.description,
                            color = TaskIdsColors.Muted,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    Text(
                        "%02d:%02d".format(minutes, seconds),
                        color = TaskIdsColors.Ink,
                        fontSize = 92.sp,
                        lineHeight = 92.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { timer.progress.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp),
                        color = when {
                            timer.progress > 0.5f -> TaskIdsColors.Blue
                            timer.progress > 0.25f -> TaskIdsColors.Yellow
                            else -> TaskIdsColors.Pink
                        },
                        trackColor = Color(0xFFE7ECF5)
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        if (timer.isRunning) "Continue assim, você está indo muito bem." else "Missão pausada.",
                        color = TaskIdsColors.Muted,
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                            modifier = Modifier.weight(1.2f),
                            onClick = { viewModel.markTaskAsCompleted(task) }
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .width(330.dp)
                        .fillMaxHeight()
                        .shadow(10.dp, RoundedCornerShape(32.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFEAF5FF), Color(0xFFF2ECFF))
                            ),
                            RoundedCornerShape(32.dp)
                        )
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "${current.name}",
                        color = TaskIdsColors.Ink,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "Combo x${current.currentCombo}",
                        color = TaskIdsColors.Orange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    GameAvatar(
                        child = current,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MissionRewardPill(
                            "${task.rewardStars}",
                            "estrelas",
                            TaskIdsColors.Yellow,
                            Modifier.weight(1f)
                        )
                        MissionRewardPill(
                            "${task.rewardXp}",
                            "XP",
                            TaskIdsColors.Blue,
                            Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MissionRewardPill(
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.88f), RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(9.dp)
                .background(accent, RoundedCornerShape(99.dp))
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(value, color = TaskIdsColors.Ink, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Text(label, color = TaskIdsColors.Muted, fontSize = 9.sp)
        }
    }
}
