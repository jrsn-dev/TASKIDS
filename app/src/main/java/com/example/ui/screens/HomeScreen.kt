package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Task
import com.example.model.TaskStatus
import com.example.ui.components.ProfilePill
import com.example.ui.components.StarPill
import com.example.ui.components.taskIdsFocus
import com.example.ui.design.TaskIdsColors
import com.example.ui.dialogs.ParentPinDialog
import com.example.ui.navigation.AppScreen
import com.example.viewmodel.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val children by viewModel.children.collectAsState()
    val child by viewModel.currentChild.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    var showPin by remember { mutableStateOf(false) }

    val completed = tasks.count { it.status == TaskStatus.COMPLETED }
    val progress = if (tasks.isEmpty()) 0f else completed.toFloat() / tasks.size.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp, vertical = 22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⭐", fontSize = 34.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "TASK",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "IDS",
                    color = TaskIdsColors.Yellow,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(Modifier.weight(1f))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                children.forEach { profile ->
                    ProfilePill(
                        child = profile,
                        selected = profile.id == child?.id,
                        onClick = { viewModel.selectChild(profile.id) }
                    )
                }
            }

            Spacer(Modifier.width(14.dp))
            StarPill(stars = child?.totalStars ?: 0)
            Spacer(Modifier.width(10.dp))

            var settingsFocused by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .onFocusChanged { settingsFocused = it.isFocused }
                    .taskIdsFocus(settingsFocused, RoundedCornerShape(17.dp), TaskIdsColors.Yellow)
                    .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(17.dp))
                    .clickable { showPin = true },
                contentAlignment = Alignment.Center
            ) {
                Text("⚙", fontSize = 24.sp)
            }
        }

        Spacer(Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Olá, ${child?.name ?: "explorador(a)"}!",
                    color = Color.White,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "Vamos conquistar mais estrelas hoje?",
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 17.sp
                )
            }

            Row(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(24.dp))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SmallStat("🔥", "${child?.currentStreak ?: 0} dias", "sequência")
                SmallStat("🏆", "Nível ${((child?.totalStars ?: 0) / 100) + 1}", "jornada")
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            "Suas tarefas de hoje",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(Modifier.height(12.dp))

        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Nenhuma tarefa para hoje. A Área dos Pais pode criar uma nova missão.",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 16.sp
                )
            }
        } else {
            LazyRow(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskCardV2(
                        task = task,
                        onClick = { viewModel.selectTask(task) }
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(28.dp))
                .padding(horizontal = 22.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(TaskIdsColors.Yellow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("⭐", fontSize = 22.sp)
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.width(220.dp)) {
                Text(
                    if (completed == tasks.size && tasks.isNotEmpty()) "Tudo pronto!" else "Progresso do dia",
                    color = TaskIdsColors.Ink,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "${completed} de ${tasks.size} tarefas concluídas",
                    color = TaskIdsColors.Muted,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp),
                    color = TaskIdsColors.Green,
                    trackColor = Color(0xFFE4EAF2)
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    "${(progress * 100).toInt()}% concluído",
                    color = TaskIdsColors.Muted,
                    fontSize = 11.sp
                )
            }

            Spacer(Modifier.width(18.dp))

            Box(
                modifier = Modifier
                    .background(TaskIdsColors.SoftBlue, RoundedCornerShape(18.dp))
                    .clickable { viewModel.navigateTo(AppScreen.Rewards) }
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Text(
                    "🎁 Recompensas",
                    color = TaskIdsColors.Blue,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }

    if (showPin) {
        ParentPinDialog(
            viewModel = viewModel,
            onDismiss = { showPin = false },
            onVerified = {
                showPin = false
                viewModel.navigateTo(AppScreen.Parent)
            }
        )
    }
}

@Composable
private fun SmallStat(icon: String, value: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 21.sp)
        Spacer(Modifier.width(7.dp))
        Column {
            Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
            Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
        }
    }
}

@Composable
private fun TaskCardV2(task: Task, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val base = when (task.orderIndex % 4) {
        1 -> TaskIdsColors.CardGreen
        2 -> TaskIdsColors.CardBlue
        3 -> TaskIdsColors.CardYellow
        else -> TaskIdsColors.CardPurple
    }

    val completed = task.status == TaskStatus.COMPLETED

    Column(
        modifier = Modifier
            .width(230.dp)
            .height(270.dp)
            .onFocusChanged { focused = it.isFocused }
            .taskIdsFocus(focused, RoundedCornerShape(28.dp), Color.White)
            .background(
                Brush.verticalGradient(
                    listOf(base.copy(alpha = if (completed) 0.68f else 1f), base)
                ),
                RoundedCornerShape(28.dp)
            )
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(28.dp))
            .clickable(enabled = !completed, onClick = onClick)
            .padding(17.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(
                        if (completed) Color.White else Color.White.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(if (completed) "✓" else "›", color = if (completed) TaskIdsColors.Green else Color.White)
            }
        }

        Spacer(Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .size(78.dp)
                .background(Color.White.copy(alpha = 0.20f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(task.icon, fontSize = 42.sp)
        }

        Spacer(Modifier.weight(1f))

        Text(
            task.title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("⏱ ${task.durationMinutes} min", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
            Spacer(Modifier.weight(1f))
            Text(
                "⭐ +${task.rewardStars}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
        }

        if (!task.scheduledTime.isNullOrBlank()) {
            Spacer(Modifier.height(7.dp))
            Text(
                "Hoje • ${task.scheduledTime}",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 11.sp
            )
        }
    }
}
