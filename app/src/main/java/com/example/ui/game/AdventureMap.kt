package com.example.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Task
import com.example.model.TaskStatus
import com.example.ui.design.TaskIdsColors

@Composable
fun AdventureMap(tasks: List<Task>, modifier: Modifier = Modifier, onMissionClick: (Task) -> Unit) {
    val visible = tasks.filter { it.isActive }
    val nextId = visible.firstOrNull { it.status != TaskStatus.COMPLETED }?.id
    Column(modifier.background(Color.White, RoundedCornerShape(28.dp)).padding(14.dp)) {
        Text("Escolha sua próxima aventura", color = TaskIdsColors.Ink,
            fontSize = 18.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(12.dp))
        if (visible.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Novas missões aparecerão aqui!", color = TaskIdsColors.Muted)
            }
        } else {
            LazyRow(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                itemsIndexed(visible, key = { _, task -> task.id }) { index, task ->
                    val done = task.status == TaskStatus.COMPLETED
                    val locked = !done && task.id != nextId && visible.take(index).any { it.status != TaskStatus.COMPLETED }
                    Column(
                        Modifier.width(164.dp).fillMaxHeight().background(
                            when {
                                done -> Color(0xFFEEFFF0)
                                locked -> Color(0xFFF2F4FA)
                                else -> Color(0xFFE9F5FF)
                            }, RoundedCornerShape(22.dp))
                            .clickable(enabled = !done && !locked) { onMissionClick(task) }
                            .padding(13.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        MissionCardArt(task.iconKey)
                        Spacer(Modifier.height(8.dp))
                        Text(task.title, color = TaskIdsColors.Ink, fontSize = 15.sp,
                            fontWeight = FontWeight.Black, textAlign = TextAlign.Center, maxLines = 2)
                        Spacer(Modifier.height(4.dp))
                        Text(when { done -> "Concluída ✓"; locked -> "Em breve"; else -> "${task.durationMinutes} min  •  ★ ${task.rewardStars}" },
                            color = if (done) TaskIdsColors.Green else TaskIdsColors.Muted,
                            fontSize = 11.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
private fun MissionCardArt(iconKey: String) {
    Box(Modifier.size(76.dp).background(Color.White, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center) {
        GameIcon(iconKey, Modifier.size(52.dp), tint = TaskIdsColors.Blue)
    }
}
