package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MetricCard
import com.example.ui.components.PrimaryTvButton
import com.example.ui.design.TaskIdsColors
import com.example.ui.navigation.AppScreen
import com.example.viewmodel.MainViewModel
import java.util.concurrent.TimeUnit

@Composable
fun ReportsScreen(viewModel: MainViewModel) {
    val child by viewModel.currentChild.collectAsState()
    val executions by viewModel.executions.collectAsState()

    val completed = executions.size
    val totalStars = executions.sumOf { it.earnedStars }
    val avgMinutes = if (executions.isEmpty()) 0
    else executions.map { it.actualDurationSeconds }.average().div(60.0).toInt()

    val today = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())
    val dailyCounts = (6 downTo 0).map { offset ->
        val day = today - offset
        executions.count { TimeUnit.MILLISECONDS.toDays(it.completedAt) == day }
    }
    val maxCount = (dailyCounts.maxOrNull() ?: 1).coerceAtLeast(1)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(TaskIdsColors.SoftBg, RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PrimaryTvButton(
                    text = "← Área dos Pais",
                    background = TaskIdsColors.Ink,
                    onClick = { viewModel.navigateTo(AppScreen.Parent) }
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        "Relatórios de ${child?.name ?: "perfil"}",
                        color = TaskIdsColors.Ink,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "Acompanhe consistência, esforço e evolução sem transformar o app em competição.",
                        color = TaskIdsColors.Muted,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                MetricCard("✅", "Tarefas concluídas", completed.toString(), Modifier.weight(1f), TaskIdsColors.Green)
                MetricCard("⭐", "Estrelas conquistadas", totalStars.toString(), Modifier.weight(1f), TaskIdsColors.Yellow)
                MetricCard("⏱", "Tempo médio", "${avgMinutes} min", Modifier.weight(1f), TaskIdsColors.Blue)
                MetricCard("🔥", "Sequência atual", "${child?.currentStreak ?: 0} dias", Modifier.weight(1f), TaskIdsColors.Orange)
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Text(
                        "Últimos 7 dias",
                        color = TaskIdsColors.Ink,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "Quantidade de tarefas finalizadas por dia",
                        color = TaskIdsColors.Muted,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        dailyCounts.forEachIndexed { index, count ->
                            Column(
                                modifier = Modifier.fillMaxHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(count.toString(), color = TaskIdsColors.Muted, fontSize = 11.sp)
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(34.dp)
                                        .fillMaxHeight((count.toFloat() / maxCount.toFloat()).coerceAtLeast(0.08f))
                                        .background(TaskIdsColors.Blue, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    listOf("S","T","Q","Q","S","S","D")[index],
                                    color = TaskIdsColors.Ink,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1.15f)
                        .fillMaxHeight()
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Text(
                        "Atividades recentes",
                        color = TaskIdsColors.Ink,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.height(12.dp))

                    if (executions.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "As primeiras conclusões aparecerão aqui.",
                                color = TaskIdsColors.Muted
                            )
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(executions.take(12), key = { it.id }) { execution ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(TaskIdsColors.SoftBg, RoundedCornerShape(16.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("✅", fontSize = 20.sp)
                                    Spacer(Modifier.width(10.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            execution.taskTitle,
                                            color = TaskIdsColors.Ink,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "${execution.actualDurationSeconds / 60} min realizados",
                                            color = TaskIdsColors.Muted,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Text(
                                        "⭐ +${execution.earnedStars}",
                                        color = TaskIdsColors.Ink,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
