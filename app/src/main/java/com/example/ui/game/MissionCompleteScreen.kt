package com.example.ui.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameEngine
import com.example.game.GameThemeKey
import com.example.ui.components.PrimaryTvButton
import com.example.ui.design.TaskIdsColors
import com.example.ui.navigation.AppScreen
import com.example.viewmodel.MainViewModel

@Composable
fun MissionCompleteScreen(viewModel: MainViewModel) {
    val child by viewModel.currentChild.collectAsState()
    val completion by viewModel.lastMissionCompletion.collectAsState()

    val current = child ?: return
    val result = completion
    val level = GameEngine.levelFor(current.totalXp)
    val theme = GameThemeKey.from(current.gameTheme)

    val burst = remember { Animatable(0f) }
    LaunchedEffect(result) {
        burst.snapTo(0f)
        burst.animateTo(1f, tween(900))
    }

    Box(Modifier.fillMaxSize()) {
        GameWorldBackground(theme, Modifier.fillMaxSize())

        Canvas(Modifier.fillMaxSize()) {
            val center = Offset(size.width * 0.5f, size.height * 0.36f)
            val particles = 24
            repeat(particles) { i ->
                val angle = (i.toFloat() / particles) * 6.28318f
                val distance = minOf(size.width, size.height) * 0.28f * burst.value
                val x = center.x + kotlin.math.cos(angle.toDouble()).toFloat() * distance
                val y = center.y + kotlin.math.sin(angle.toDouble()).toFloat() * distance
                val color = when (i % 4) {
                    0 -> TaskIdsColors.Yellow
                    1 -> TaskIdsColors.Green
                    2 -> TaskIdsColors.Blue
                    else -> TaskIdsColors.Purple
                }
                drawCircle(color.copy(alpha = 1f - burst.value * 0.3f), 5f + (i % 3) * 2f, Offset(x, y))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
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
                Spacer(Modifier.weight(1f))

                MascotStar(Modifier.size(70.dp))

                GameAvatar(
                    child = current,
                    celebrate = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    "MISSÃO CONCLUÍDA",
                    color = TaskIdsColors.Yellow,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    result?.taskTitle ?: "Muito bem!",
                    color = TaskIdsColors.Ink,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.weight(1f))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.White, RoundedCornerShape(32.dp))
                    .padding(30.dp)
            ) {
                Text(
                    "Boa, ${current.name}!",
                    color = TaskIdsColors.Ink,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "Seu progresso foi salvo. Continue a jornada para aumentar o combo.",
                    color = TaskIdsColors.Muted,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(22.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    RewardMetric("ESTRELAS", "+${result?.stars ?: 0}", TaskIdsColors.Yellow, Modifier.weight(1f))
                    RewardMetric("XP", "+${result?.xp ?: 0}", TaskIdsColors.Blue, Modifier.weight(1f))
                    RewardMetric("COMBO", "x${result?.combo ?: current.currentCombo}", TaskIdsColors.Orange, Modifier.weight(1f))
                }

                if ((result?.comboBonusStars ?: 0) > 0 || (result?.perfectDayStars ?: 0) > 0) {
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if ((result?.comboBonusStars ?: 0) > 0) {
                            BonusChip("BÔNUS DE COMBO", "+${result?.comboBonusStars} estrelas")
                        }
                        if ((result?.perfectDayStars ?: 0) > 0) {
                            BonusChip(
                                "DIA PERFEITO",
                                "+${result?.perfectDayStars} estrelas • +${result?.perfectDayXp} XP"
                            )
                        }
                    }
                }

                Spacer(Modifier.height(22.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TaskIdsColors.SoftBg, RoundedCornerShape(24.dp))
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Nível ${level.level}",
                            color = TaskIdsColors.Ink,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(level.title, color = TaskIdsColors.Muted, fontSize = 13.sp)
                        Spacer(Modifier.weight(1f))
                        Text(
                            "${level.currentXp}/500 XP",
                            color = TaskIdsColors.Blue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { level.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        color = TaskIdsColors.Blue,
                        trackColor = Color(0xFFDDE5F1)
                    )
                }

                Spacer(Modifier.weight(1f))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PrimaryTvButton(
                        text = "CONTINUAR JORNADA",
                        background = TaskIdsColors.Green,
                        modifier = Modifier.weight(1.3f),
                        onClick = { viewModel.navigateTo(AppScreen.Home) }
                    )
                    PrimaryTvButton(
                        text = "RECOMPENSAS",
                        background = TaskIdsColors.Orange,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(AppScreen.Rewards) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RewardMetric(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Text(label, color = TaskIdsColors.Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = color, fontSize = 28.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun BonusChip(label: String, value: String) {
    Column(
        modifier = Modifier
            .background(TaskIdsColors.Ink, RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(label, color = TaskIdsColors.Yellow, fontSize = 9.sp, fontWeight = FontWeight.Black)
        Text(value, color = TaskIdsColors.Ink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
