package com.taskids.app.ui.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskids.app.ui.components.FocusSurface
import com.taskids.app.ui.theme.Blue500
import com.taskids.app.ui.theme.Cloud100
import com.taskids.app.ui.theme.Green500
import com.taskids.app.ui.theme.Ink600
import com.taskids.app.ui.theme.Ink900
import com.taskids.app.ui.theme.Purple500
import com.taskids.app.ui.theme.Sky400
import com.taskids.app.ui.theme.White
import com.taskids.app.ui.theme.Yellow500
import com.taskids.app.viewmodel.MainViewModel

@Composable
fun TimerScreen(viewModel: MainViewModel, taskId: Int) {
    val tasks by viewModel.tasks.collectAsState()
    val child by viewModel.currentChild.collectAsState()
    val timer by viewModel.timerState.collectAsState()
    val task = tasks.firstOrNull { it.id == taskId }

    if (task == null) {
        Box(Modifier.fillMaxSize().background(Ink900), contentAlignment = Alignment.Center) {
            Text("Carregando missão...", color = White)
        }
        return
    }

    val minutes = timer.remainingSeconds / 60
    val seconds = timer.remainingSeconds % 60
    val counter = "%02d:%02d".format(minutes, seconds)
    val completedSeconds = (timer.totalSeconds - timer.remainingSeconds).coerceAtLeast(0)
    val completedMinutes = completedSeconds / 60

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF1CB8F1), Color(0xFF4C8DF8))
                )
            )
            .padding(28.dp),
        horizontalArrangement = Arrangement.spacedBy(28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(.82f).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            FocusSurface(
                onClick = viewModel::leaveTimer,
                shape = RoundedCornerShape(999.dp),
                background = Color.White.copy(alpha = .18f),
                padding = 10.dp
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = White)
                    Spacer(Modifier.width(7.dp))
                    Text("Voltar", color = White, fontWeight = FontWeight.Bold)
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(310.dp)
                        .background(Color.White.copy(alpha = .15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(child?.avatar ?: "🧒", fontSize = 88.sp)
                        Text(task.icon, fontSize = 86.sp)
                    }
                }
                Text(
                    "○  ○  ○",
                    color = White.copy(alpha = .65f),
                    fontSize = 35.sp,
                    modifier = Modifier.align(Alignment.TopEnd).padding(top = 50.dp)
                )
            }

            Text(
                text = "Você consegue, ${child?.name ?: "Kids"}!",
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
        }

        Column(
            modifier = Modifier
                .weight(1.18f)
                .fillMaxSize()
                .background(White, RoundedCornerShape(38.dp))
                .padding(horizontal = 40.dp, vertical = 30.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(58.dp).background(Cloud100, RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(task.icon, fontSize = 34.sp)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            task.title,
                            color = Ink900,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            "Mantenha o foco! Você consegue!",
                            color = Ink600,
                            fontSize = 13.sp
                        )
                    }
                }
                Box(
                    Modifier
                        .background(Color(0xFFFFF4C2), CircleShape)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        "⭐ +${task.rewardPoints}",
                        color = Ink900,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    counter,
                    color = Ink900,
                    fontSize = 94.sp,
                    lineHeight = 96.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { 1f - timer.progress },
                    modifier = Modifier.fillMaxWidth().height(18.dp).clip(CircleShape),
                    color = Blue500,
                    trackColor = Color(0xFFE1E9F5)
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "$completedMinutes minutos concluídos",
                        color = Ink600,
                        fontSize = 12.sp
                    )
                    Text(
                        "${timer.totalSeconds / 60} minutos",
                        color = Ink600,
                        fontSize = 12.sp
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                FocusSurface(
                    onClick = {
                        if (timer.isRunning) viewModel.pauseTimer() else viewModel.resumeTimer()
                    },
                    modifier = Modifier.weight(1f).height(92.dp),
                    background = Color(0xFFEDE3FF),
                    focusColor = Purple500,
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            if (timer.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            null,
                            tint = Color(0xFF6D38D8),
                            modifier = Modifier.size(30.dp)
                        )
                        Text(
                            if (timer.isRunning) "Pausar" else "Retomar",
                            color = Color(0xFF3D2379),
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                FocusSurface(
                    onClick = { viewModel.extendTimer(5) },
                    modifier = Modifier.weight(1f).height(92.dp),
                    background = Color(0xFFDDEEFF),
                    focusColor = Sky400,
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("+5", color = Blue500, fontSize = 26.sp, fontWeight = FontWeight.Black)
                        Text("+5 min", color = Blue500, fontWeight = FontWeight.Black)
                    }
                }
                FocusSurface(
                    onClick = viewModel::completeCurrentTask,
                    modifier = Modifier.weight(1.18f).height(92.dp),
                    background = Green500,
                    focusColor = Yellow500,
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            null,
                            tint = White,
                            modifier = Modifier.size(34.dp)
                        )
                        Text("Concluído", color = White, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
