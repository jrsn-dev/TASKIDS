package com.example.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Task
import com.example.model.TaskStatus
import com.example.ui.design.TaskIdsColors

@Composable
fun AdventureMap(
    tasks: List<Task>,
    modifier: Modifier = Modifier,
    onMissionClick: (Task) -> Unit
) {
    val visible = tasks.filter { it.isActive }.take(6)
    val nextMissionId = visible.firstOrNull { it.status != TaskStatus.COMPLETED }?.id

    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.07f), RoundedCornerShape(28.dp))
            .padding(18.dp)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            if (visible.size <= 1) return@Canvas
            val left = size.width * 0.09f
            val right = size.width * 0.91f
            val step = (right - left) / (visible.size - 1)
            val yBase = size.height * 0.48f
            val path = Path()
            visible.indices.forEach { index ->
                val x = left + index * step
                val y = yBase + if (index % 2 == 0) -size.height * 0.11f else size.height * 0.11f
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(
                path,
                color = Color.White.copy(alpha = 0.18f),
                style = Stroke(width = 10f, cap = StrokeCap.Round)
            )
            visible.forEachIndexed { index, task ->
                if (task.status == TaskStatus.COMPLETED && index < visible.lastIndex) {
                    val x1 = left + index * step
                    val y1 = yBase + if (index % 2 == 0) -size.height * 0.11f else size.height * 0.11f
                    val x2 = left + (index + 1) * step
                    val y2 = yBase + if ((index + 1) % 2 == 0) -size.height * 0.11f else size.height * 0.11f
                    drawLine(
                        TaskIdsColors.Yellow,
                        Offset(x1, y1),
                        Offset(x2, y2),
                        strokeWidth = 10f,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            visible.forEachIndexed { index, task ->
                val completed = task.status == TaskStatus.COMPLETED
                val active = task.id == nextMissionId
                val locked = !completed && !active && visible
                    .take(index)
                    .any { it.status != TaskStatus.COMPLETED }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(116.dp)
                        .offset(y = if (index % 2 == 0) (-28).dp else 28.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (active) 88.dp else 76.dp)
                            .background(
                                when {
                                    completed -> TaskIdsColors.Green
                                    active -> TaskIdsColors.Yellow
                                    else -> Color(0xFF60708D)
                                },
                                CircleShape
                            )
                            .clickable(enabled = !locked && !completed) { onMissionClick(task) },
                        contentAlignment = Alignment.Center
                    ) {
                        GameIcon(
                            key = task.iconKey,
                            modifier = Modifier.size(if (active) 52.dp else 44.dp),
                            tint = if (active) TaskIdsColors.Ink else Color.White
                        )
                        if (completed) {
                            Box(
                                Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(25.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✓", color = TaskIdsColors.Green, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = when {
                            completed -> "Concluída"
                            active -> task.title
                            locked -> "Bloqueada"
                            else -> task.title
                        },
                        color = if (active) TaskIdsColors.Yellow else Color.White,
                        fontWeight = if (active) FontWeight.Black else FontWeight.Bold,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                    if (active) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "+${task.rewardXp} XP",
                            color = Color.White.copy(alpha = 0.72f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
