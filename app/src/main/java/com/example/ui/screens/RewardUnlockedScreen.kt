package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun RewardUnlockedScreen(viewModel: MainViewModel) {
    val child by viewModel.currentChild.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val earnedToday = tasks.filter { it.status.name == "COMPLETED" }.sumOf { it.rewardStars }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(26.dp)
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF2D1E78), Color(0xFF3D2B9B), TaskIdsColors.Navy800)
                ),
                RoundedCornerShape(34.dp)
            )
            .padding(36.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(0.9f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🎉 ⭐ 🎊", fontSize = 52.sp)
            Spacer(Modifier.height(14.dp))
            Text(
                child?.avatarEmoji ?: "🧒",
                fontSize = 110.sp
            )
            Text("🙌", fontSize = 54.sp)
        }

        Column(
            modifier = Modifier.weight(1.25f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "Parabéns, ${child?.name ?: ""}!",
                color = TaskIdsColors.Yellow,
                fontSize = 46.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                "Você concluiu as tarefas de hoje!",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Seu esforço virou estrelas. Agora você pode escolher como usar sua recompensa.",
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 16.sp
            )

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Text(
                        "⭐ +${earnedToday} hoje",
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Text(
                        "🔥 ${child?.currentStreak ?: 0} dias",
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(Modifier.height(26.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(26.dp))
                    .padding(22.dp)
            ) {
                Column {
                    Text(
                        "Escolha sua recompensa",
                        color = TaskIdsColors.Ink,
                        fontWeight = FontWeight.Black,
                        fontSize = 19.sp
                    )
                    Text(
                        "As recompensas são definidas e controladas pelos responsáveis.",
                        color = TaskIdsColors.Muted,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PrimaryTvButton(
                            text = "🎁 Ver recompensas",
                            background = TaskIdsColors.Orange,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.navigateTo(AppScreen.Rewards) }
                        )
                        PrimaryTvButton(
                            text = "↻ Recomeçar tarefas",
                            background = TaskIdsColors.Blue,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.restartAllTasks() }
                        )
                    }
                }
            }
        }
    }
}
