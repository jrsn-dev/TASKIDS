package com.example.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.game.GameEngine
import com.example.model.Child
import com.example.model.Task
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
    val progress = if (activeTasks.isEmpty()) 0f else completed.toFloat() / activeTasks.size.toFloat()

    var showPin by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF8FBFF), Color(0xFFF2F6FF), Color(0xFFF9FAFF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 18.dp)
        ) {
            HomeTopBar(
                children = children,
                current = current,
                onSelectProfile = { viewModel.selectChild(it) },
                onParents = { showPin = true }
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HomeHeroCard(
                    child = current,
                    levelTitle = level.title,
                    levelNumber = level.level,
                    completed = completed,
                    total = activeTasks.size,
                    nextMission = nextMission,
                    modifier = Modifier
                        .weight(1.45f)
                        .fillMaxHeight(),
                    onStartMission = {
                        nextMission?.let(viewModel::selectTask)
                    }
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TodayProgressCard(
                        completed = completed,
                        total = activeTasks.size,
                        progress = progress,
                        unlockedAchievements = achievements.count { it.unlocked },
                        stars = current.totalStars,
                        xp = current.totalXp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    MissionListCard(
                        tasks = activeTasks,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        onMissionClick = viewModel::selectTask
                    )

                    RewardsButton(
                        onClick = { viewModel.navigateTo(AppScreen.Rewards) }
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
}

@Composable
private fun HomeTopBar(
    children: List<Child>,
    current: Child,
    onSelectProfile: (Long) -> Unit,
    onParents: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(R.drawable.kids_task_logo_1780663813391),
            contentDescription = "TASKIDS",
            modifier = Modifier
                .width(118.dp)
                .height(42.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            children.forEach { profile ->
                val selected = profile.id == current.id
                Box(
                    modifier = Modifier
                        .background(
                            if (selected) TaskIdsColors.Ink else Color.White,
                            RoundedCornerShape(18.dp)
                        )
                        .clickable { onSelectProfile(profile.id) }
                        .padding(horizontal = 13.dp, vertical = 9.dp)
                ) {
                    Text(
                        profile.name,
                        color = if (selected) Color.White else TaskIdsColors.Ink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .background(TaskIdsColors.Purple, RoundedCornerShape(18.dp))
                .clickable(onClick = onParents)
                .padding(horizontal = 16.dp, vertical = 10.dp)
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
private fun HomeHeroCard(
    child: Child,
    levelTitle: String,
    levelNumber: Int,
    completed: Int,
    total: Int,
    nextMission: Task?,
    modifier: Modifier = Modifier,
    onStartMission: () -> Unit
) {
    Row(
        modifier = modifier
            .shadow(14.dp, RoundedCornerShape(34.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color.White, Color(0xFFF5F1FF), Color(0xFFEAF5FF))
                ),
                RoundedCornerShape(34.dp)
            )
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.15f)) {
            Text(
                "Oi, ${child.name}!",
                color = TaskIdsColors.Purple,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(Modifier.height(4.dp))

            Text(
                "Pronto para sua próxima conquista?",
                color = TaskIdsColors.Ink,
                fontSize = 30.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(Modifier.height(14.dp))

            Text(
                "$levelTitle • Nível $levelNumber",
                color = TaskIdsColors.Muted,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeroMetric(child.totalStars.toString(), "estrelas", TaskIdsColors.Yellow)
                HeroMetric(child.totalXp.toString(), "XP", TaskIdsColors.Blue)
                HeroMetric("$completed/$total", "hoje", TaskIdsColors.Green)
            }

            Spacer(Modifier.height(18.dp))

            if (nextMission != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.88f), RoundedCornerShape(22.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        "Sua próxima missão",
                        color = TaskIdsColors.Muted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        nextMission.title,
                        color = TaskIdsColors.Ink,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "${nextMission.durationMinutes} min • +${nextMission.rewardStars} estrelas • +${nextMission.rewardXp} XP",
                        color = TaskIdsColors.Muted,
                        fontSize = 11.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .background(TaskIdsColors.Ink, RoundedCornerShape(16.dp))
                            .clickable(onClick = onStartMission)
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Text(
                            "COMEÇAR AGORA",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            } else {
                Text(
                    if (total > 0) "Você concluiu tudo por hoje." else "As missões vão aparecer aqui.",
                    color = TaskIdsColors.Green,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.width(18.dp))

        Box(
            modifier = Modifier
                .width(250.dp)
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(listOf(Color(0xFFE9F5FF), Color(0xFFF2ECFF))),
                    RoundedCornerShape(28.dp)
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.Center)
                    .background(Color.White.copy(alpha = 0.55f), CircleShape)
            )

            GameAvatar(
                child = child,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 18.dp, start = 10.dp, end = 10.dp)
            )
        }
    }
}

@Composable
private fun HeroMetric(value: String, label: String, accent: Color) {
    Row(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.86f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(8.dp).background(accent, CircleShape))
        Spacer(Modifier.width(7.dp))
        Column {
            Text(value, color = TaskIdsColors.Ink, fontSize = 15.sp, fontWeight = FontWeight.Black)
            Text(label, color = TaskIdsColors.Muted, fontSize = 9.sp)
        }
    }
}

@Composable
private fun TodayProgressCard(
    completed: Int,
    total: Int,
    progress: Float,
    unlockedAchievements: Int,
    stars: Int,
    xp: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(26.dp))
            .background(Color.White, RoundedCornerShape(26.dp))
            .padding(18.dp)
    ) {
        Text("Progresso de hoje", color = TaskIdsColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(9.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(10.dp),
            color = TaskIdsColors.Purple,
            trackColor = Color(0xFFE9EDF6)
        )
        Spacer(Modifier.height(9.dp))
        Text("$completed de $total missões concluídas", color = TaskIdsColors.Muted, fontSize = 11.sp)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallStat("Conquistas", unlockedAchievements.toString(), Modifier.weight(1f))
            SmallStat("Estrelas", stars.toString(), Modifier.weight(1f))
            SmallStat("XP", xp.toString(), Modifier.weight(1f))
        }
    }
}

@Composable
private fun SmallStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(TaskIdsColors.SoftBg, RoundedCornerShape(15.dp))
            .padding(10.dp)
    ) {
        Text(value, color = TaskIdsColors.Ink, fontSize = 15.sp, fontWeight = FontWeight.Black)
        Text(label, color = TaskIdsColors.Muted, fontSize = 8.sp)
    }
}

@Composable
private fun MissionListCard(
    tasks: List<Task>,
    modifier: Modifier = Modifier,
    onMissionClick: (Task) -> Unit
) {
    Column(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(26.dp))
            .background(Color.White, RoundedCornerShape(26.dp))
            .padding(18.dp)
    ) {
        Text("Missões de hoje", color = TaskIdsColors.Ink, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(10.dp))

        if (tasks.isEmpty()) {
            Text("Nenhuma missão ativa.", color = TaskIdsColors.Muted, fontSize = 12.sp)
        } else {
            tasks.take(4).forEachIndexed { index, task ->
                val done = task.status == TaskStatus.COMPLETED
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !done) { onMissionClick(task) }
                        .padding(vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                if (done) Color(0xFFE2F6E9) else when (index % 3) {
                                    0 -> Color(0xFFEAF3FF)
                                    1 -> Color(0xFFF2ECFF)
                                    else -> Color(0xFFFFF3D8)
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (done) "✓" else "${index + 1}",
                            color = if (done) TaskIdsColors.Green else TaskIdsColors.Ink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Column(Modifier.weight(1f)) {
                        Text(task.title, color = TaskIdsColors.Ink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "${task.durationMinutes} min • +${task.rewardXp} XP",
                            color = TaskIdsColors.Muted,
                            fontSize = 9.sp
                        )
                    }

                    Text(
                        if (done) "CONCLUÍDA" else "ABRIR",
                        color = if (done) TaskIdsColors.Green else TaskIdsColors.Purple,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun RewardsButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TaskIdsColors.Ink, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Ver recompensas", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.weight(1f))
        Text("→", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
    }
}
