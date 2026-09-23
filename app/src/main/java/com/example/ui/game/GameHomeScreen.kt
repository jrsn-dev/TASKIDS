package com.example.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameEngine
import com.example.game.GameThemeKey
import com.example.model.Child
import com.example.model.TaskStatus
import com.example.ui.design.TaskIdsColors
import com.example.ui.dialogs.ParentPinDialog
import com.example.ui.navigation.AppScreen
import com.example.viewmodel.MainViewModel

@Composable
fun GameHomeScreen(viewModel: MainViewModel) {
    val children by viewModel.children.collectAsState()
    val child by viewModel.currentChild.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    val current = child ?: return
    val activeTasks = tasks.filter { it.isActive }
    val completed = activeTasks.count { it.status == TaskStatus.COMPLETED }
    val nextMission = activeTasks.firstOrNull { it.status != TaskStatus.COMPLETED }
    val level = GameEngine.levelFor(current.totalXp)
    val theme = GameThemeKey.from(current.gameTheme)

    var showPin by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        GameWorldBackground(theme = theme, modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StarIcon(Modifier.size(32.dp))
                    Spacer(Modifier.width(9.dp))
                    Text(
                        "TASKIDS",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(Modifier.weight(1f))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    children.forEach { profile ->
                        ProfileGamePill(
                            profile = profile,
                            selected = profile.id == current.id,
                            onClick = { viewModel.selectChild(profile.id) }
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
                        .clickable { showPin = true }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text("PAIS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .width(250.dp)
                        .fillMaxHeight()
                        .background(Color.Black.copy(alpha = 0.13f), RoundedCornerShape(28.dp))
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "JOGADOR",
                        color = Color.White.copy(alpha = 0.60f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        current.name,
                        color = Color.White,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(Modifier.height(8.dp))

                    GameAvatar(
                        child = current,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    LevelHud(
                        child = current,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GameStatPill(
                            "COMBO",
                            "x${current.currentCombo}",
                            Modifier.weight(1f),
                            TaskIdsColors.Orange
                        )
                        GameStatPill(
                            "STREAK",
                            "${current.currentStreak}d",
                            Modifier.weight(1f),
                            TaskIdsColors.Green
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                "Jornada de hoje",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                "Complete as missões para avançar pelo mapa.",
                                color = Color.White.copy(alpha = 0.68f),
                                fontSize = 13.sp
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        GameStatPill(
                            label = "ESTRELAS",
                            value = current.totalStars.toString(),
                            accent = TaskIdsColors.Yellow
                        )
                        Spacer(Modifier.width(8.dp))
                        GameStatPill(
                            label = "CONQUISTAS",
                            value = achievements.count { it.unlocked }.toString(),
                            accent = TaskIdsColors.Purple
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    AchievementShelf(
                        achievements = achievements,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(14.dp))

                    if (nextMission != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.10f), RoundedCornerShape(24.dp))
                                .clickable { viewModel.selectTask(nextMission) }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(TaskIdsColors.Yellow, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                GameIcon(
                                    key = nextMission.iconKey,
                                    modifier = Modifier.size(38.dp),
                                    tint = TaskIdsColors.Ink
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "PRÓXIMA MISSÃO",
                                    color = Color.White.copy(alpha = 0.55f),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    nextMission.title,
                                    color = Color.White,
                                    fontSize = 21.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    "${nextMission.durationMinutes} min • +${nextMission.rewardStars} estrelas • +${nextMission.rewardXp} XP",
                                    color = Color.White.copy(alpha = 0.72f),
                                    fontSize = 11.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .background(TaskIdsColors.Yellow, RoundedCornerShape(18.dp))
                                    .padding(horizontal = 18.dp, vertical = 12.dp)
                            ) {
                                Text("JOGAR", color = TaskIdsColors.Ink, fontWeight = FontWeight.Black)
                            }
                        }
                    } else if (activeTasks.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(TaskIdsColors.Green.copy(alpha = 0.30f), RoundedCornerShape(24.dp))
                                .padding(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "JORNADA COMPLETA — recompensas desbloqueadas",
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    AdventureMap(
                        tasks = activeTasks,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onMissionClick = { viewModel.selectTask(it) }
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${completed} / ${activeTasks.size} missões concluídas",
                            color = Color.White.copy(alpha = 0.68f),
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            "NÍVEL ${level.level} • ${level.title}",
                            color = TaskIdsColors.Yellow,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                        Spacer(Modifier.width(16.dp))
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.10f), RoundedCornerShape(18.dp))
                                .clickable { viewModel.navigateTo(AppScreen.Rewards) }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text("RECOMPENSAS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }
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
}

@Composable
private fun ProfileGamePill(
    profile: Child,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                if (selected) Color.White.copy(alpha = 0.18f) else Color.Black.copy(alpha = 0.12f),
                RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(24.dp)
                .background(
                    if (selected) TaskIdsColors.Yellow else Color.White.copy(alpha = 0.25f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                profile.name.take(1).uppercase(),
                color = if (selected) TaskIdsColors.Ink else Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.width(7.dp))
        Text(profile.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
