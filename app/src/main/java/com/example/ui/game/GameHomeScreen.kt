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
                .padding(horizontal = 28.dp, vertical = 20.dp)
        ) {
            GameHomeTopBar(
                children = children,
                current = current,
                onSelectProfile = { viewModel.selectChild(it) },
                onParents = { showPin = true }
            )

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                PlayerSummaryCard(
                    child = current,
                    levelTitle = level.title,
                    levelNumber = level.level,
                    modifier = Modifier
                        .width(300.dp)
                        .fillMaxHeight()
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    JourneyHeader(
                        child = current,
                        unlockedAchievements = achievements.count { it.unlocked },
                        completed = completed,
                        total = activeTasks.size
                    )

                    Spacer(Modifier.height(14.dp))

                    if (nextMission != null) {
                        NextMissionCard(
                            taskTitle = nextMission.title,
                            iconKey = nextMission.iconKey,
                            duration = nextMission.durationMinutes,
                            stars = nextMission.rewardStars,
                            xp = nextMission.rewardXp,
                            onClick = { viewModel.selectTask(nextMission) }
                        )
                    } else if (activeTasks.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    TaskIdsColors.Green.copy(alpha = 0.28f),
                                    RoundedCornerShape(22.dp)
                                )
                                .padding(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Jornada completa por hoje",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(
                                Color.Black.copy(alpha = 0.10f),
                                RoundedCornerShape(28.dp)
                            )
                            .padding(14.dp)
                    ) {
                        AdventureMap(
                            tasks = activeTasks,
                            modifier = Modifier.fillMaxSize(),
                            onMissionClick = { viewModel.selectTask(it) }
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${completed} de ${activeTasks.size} missões concluídas",
                            color = Color.White.copy(alpha = 0.78f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .background(
                                    Color.White.copy(alpha = 0.12f),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { viewModel.navigateTo(AppScreen.Rewards) }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                "RECOMPENSAS",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
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
private fun GameHomeTopBar(
    children: List<Child>,
    current: Child,
    onSelectProfile: (Long) -> Unit,
    onParents: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StarIcon(Modifier.size(28.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "TASKIDS",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            children.forEach { profile ->
                val selected = profile.id == current.id
                Box(
                    modifier = Modifier
                        .background(
                            if (selected) Color.White.copy(alpha = 0.18f)
                            else Color.Black.copy(alpha = 0.10f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelectProfile(profile.id) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        profile.name,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.16f), RoundedCornerShape(16.dp))
                .clickable(onClick = onParents)
                .padding(horizontal = 15.dp, vertical = 9.dp)
        ) {
            Text(
                "PAIS",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun PlayerSummaryCard(
    child: Child,
    levelTitle: String,
    levelNumber: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                Color.Black.copy(alpha = 0.14f),
                RoundedCornerShape(28.dp)
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "OLÁ, ${child.name.uppercase()}",
            color = Color.White.copy(alpha = 0.68f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(Modifier.height(4.dp))

        Text(
            levelTitle,
            color = Color.White,
            fontSize = 21.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(Modifier.height(8.dp))

        GameAvatar(
            child = child,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Spacer(Modifier.height(10.dp))

        LevelHud(
            child = child,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GameStatPill(
                label = "COMBO",
                value = "x${child.currentCombo}",
                modifier = Modifier.weight(1f),
                accent = TaskIdsColors.Orange
            )
            GameStatPill(
                label = "STREAK",
                value = "${child.currentStreak}d",
                modifier = Modifier.weight(1f),
                accent = TaskIdsColors.Green
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            "NÍVEL $levelNumber",
            color = TaskIdsColors.Yellow,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun JourneyHeader(
    child: Child,
    unlockedAchievements: Int,
    completed: Int,
    total: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Jornada de hoje",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                "Escolha a próxima missão e avance pelo mapa.",
                color = Color.White.copy(alpha = 0.68f),
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.weight(1f))

        CompactHomeStat(
            label = "ESTRELAS",
            value = child.totalStars.toString(),
            accent = TaskIdsColors.Yellow
        )
        Spacer(Modifier.width(8.dp))
        CompactHomeStat(
            label = "CONQUISTAS",
            value = unlockedAchievements.toString(),
            accent = TaskIdsColors.Purple
        )
        Spacer(Modifier.width(8.dp))
        CompactHomeStat(
            label = "HOJE",
            value = "$completed/$total",
            accent = TaskIdsColors.Green
        )
    }
}

@Composable
private fun CompactHomeStat(
    label: String,
    value: String,
    accent: Color
) {
    Column(
        modifier = Modifier
            .width(92.dp)
            .background(Color.Black.copy(alpha = 0.12f), RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) {
        Box(Modifier.size(7.dp).background(accent, CircleShape))
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            label,
            color = Color.White.copy(alpha = 0.58f),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun NextMissionCard(
    taskTitle: String,
    iconKey: String,
    duration: Int,
    stars: Int,
    xp: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .background(TaskIdsColors.Yellow, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            GameIcon(
                key = iconKey,
                modifier = Modifier.size(34.dp),
                tint = TaskIdsColors.Ink
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(Modifier.weight(1f)) {
            Text(
                "PRÓXIMA MISSÃO",
                color = Color.White.copy(alpha = 0.56f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                taskTitle,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                "$duration min  •  +$stars estrelas  •  +$xp XP",
                color = Color.White.copy(alpha = 0.74f),
                fontSize = 11.sp
            )
        }

        Box(
            modifier = Modifier
                .background(TaskIdsColors.Yellow, RoundedCornerShape(16.dp))
                .padding(horizontal = 18.dp, vertical = 11.dp)
        ) {
            Text(
                "COMEÇAR",
                color = TaskIdsColors.Ink,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
            )
        }
    }
}
