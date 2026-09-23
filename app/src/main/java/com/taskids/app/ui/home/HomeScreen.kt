package com.taskids.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskids.app.domain.model.Task
import com.taskids.app.domain.model.TaskStatus
import com.taskids.app.ui.components.FocusSurface
import com.taskids.app.ui.components.ProfilePill
import com.taskids.app.ui.components.StarPill
import com.taskids.app.ui.parental.ParentPinDialog
import com.taskids.app.ui.theme.Blue500
import com.taskids.app.ui.theme.Green400
import com.taskids.app.ui.theme.Navy800
import com.taskids.app.ui.theme.Navy900
import com.taskids.app.ui.theme.Navy950
import com.taskids.app.ui.theme.TaskPalette
import com.taskids.app.ui.theme.White
import com.taskids.app.ui.theme.Yellow500
import com.taskids.app.viewmodel.MainViewModel

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val children by viewModel.children.collectAsState()
    val currentChild by viewModel.currentChild.collectAsState()
    val currentChildId by viewModel.currentChildId.collectAsState()
    val tasks by viewModel.todayTasks.collectAsState()
    val timer by viewModel.timerState.collectAsState()
    var showPin by remember { mutableStateOf(false) }

    val completed = tasks.count { it.status == TaskStatus.COMPLETED }
    val progress = if (tasks.isEmpty()) 0f else completed.toFloat() / tasks.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Navy800, Navy900, Navy950)
                )
            )
    ) {
        DecorativeBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 38.dp, vertical = 22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("★", color = Yellow500, fontSize = 38.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.width(8.dp))
                    Text("TASK", color = White, fontSize = 29.sp, fontWeight = FontWeight.Black)
                    Text("IDS", color = Yellow500, fontSize = 29.sp, fontWeight = FontWeight.Black)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    children.forEach { child ->
                        ProfilePill(
                            avatar = child.avatar,
                            name = child.name,
                            selected = child.id == currentChildId,
                            onClick = { viewModel.selectChild(child.id) }
                        )
                    }
                    StarPill(currentChild?.totalStars ?: 0)
                    FocusSurface(
                        onClick = { showPin = true },
                        shape = RoundedCornerShape(999.dp),
                        background = Color.White.copy(alpha = .12f),
                        focusColor = Yellow500,
                        padding = 11.dp
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Área dos pais", tint = White)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Olá, ${currentChild?.name ?: "Kids"}!",
                        color = White,
                        fontSize = 39.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Vamos conquistar mais estrelas hoje?",
                        color = White.copy(alpha = .82f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .width(245.dp)
                        .height(92.dp)
                        .background(Color(0xFF174FC5).copy(alpha = .72f), RoundedCornerShape(34.dp))
                        .border(1.dp, Color.White.copy(alpha = .14f), RoundedCornerShape(34.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(currentChild?.avatar ?: "🧒", fontSize = 62.sp)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("Nível ${currentChild?.level ?: 1}", color = Yellow500, fontWeight = FontWeight.Black, fontSize = 17.sp)
                            Text("Cada missão conta!", color = White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("Suas tarefas de hoje", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            if (tasks.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        "Nenhuma missão programada para hoje.",
                        color = White.copy(alpha = .75f),
                        fontSize = 20.sp
                    )
                }
            } else {
                LazyRow(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            color = TaskPalette[kotlin.math.abs(task.id) % TaskPalette.size],
                            activeTimer = timer.taskId == task.id,
                            onClick = {
                                if (timer.taskId == task.id) {
                                    viewModel.navigateTo(com.taskids.app.viewmodel.Screen.Timer(task.id))
                                } else {
                                    viewModel.startTask(task)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            ProgressFooter(
                completed = completed,
                total = tasks.size,
                progress = progress,
                childName = currentChild?.name ?: "Kids"
            )
        }
    }

    if (showPin) {
        ParentPinDialog(
            viewModel = viewModel,
            onDismiss = { showPin = false },
            onVerified = { showPin = false }
        )
    }
}

@Composable
private fun DecorativeBackground() {
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .size(360.dp)
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 80.dp)
                .background(Color(0xFF2563EB).copy(alpha = .18f), CircleShape)
        )
        Text(
            "✦",
            color = Yellow500.copy(alpha = .7f),
            fontSize = 32.sp,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 92.dp)
        )
        Text(
            "★",
            color = Yellow500.copy(alpha = .35f),
            fontSize = 26.sp,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 36.dp)
        )
    }
}

@Composable
private fun TaskCard(task: Task, color: Color, activeTimer: Boolean, onClick: () -> Unit) {
    val completed = task.status == TaskStatus.COMPLETED
    FocusSurface(
        onClick = { if (!completed) onClick() },
        modifier = Modifier.width(235.dp).height(272.dp),
        shape = RoundedCornerShape(30.dp),
        background = if (completed) color.copy(alpha = .55f) else color,
        focusColor = White,
        padding = 16.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .background(Color.White.copy(alpha = .22f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(task.icon, fontSize = 47.sp)
                }
                if (completed) {
                    Box(
                        Modifier.size(30.dp).background(White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Green400,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Column {
                Text(
                    task.title,
                    color = White,
                    fontWeight = FontWeight.Black,
                    fontSize = 21.sp,
                    maxLines = 2
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    if (activeTimer) "⏸ Missão em andamento" else "◷ ${task.durationMinutes} min",
                    color = White.copy(alpha = .88f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
            Row(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = .14f), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "⭐ +${task.rewardPoints}",
                    color = White,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ProgressFooter(completed: Int, total: Int, progress: Float, childName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .background(White, RoundedCornerShape(26.dp))
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Box(
            Modifier.size(46.dp).background(Yellow500, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                completed.toString(),
                color = White,
                fontWeight = FontWeight.Black,
                fontSize = 19.sp
            )
        }
        Column(modifier = Modifier.weight(.9f)) {
            Text(
                "Muito bem, $childName!",
                color = Color(0xFF172033),
                fontWeight = FontWeight.Black,
                fontSize = 17.sp
            )
            Text(
                "$completed de $total tarefas concluídas",
                color = Color(0xFF667085),
                fontSize = 12.sp
            )
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.weight(1.4f).height(15.dp).clip(CircleShape),
            color = Green400,
            trackColor = Color(0xFFE4EAF2)
        )
        Text(
            "${(progress * 100).toInt()}%",
            color = Blue500,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            textAlign = TextAlign.End
        )
    }
}
