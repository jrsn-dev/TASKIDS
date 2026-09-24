package com.example.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.game.GameEngine
import com.example.game.GameThemeKey
import com.example.model.Child
import com.example.model.Reward
import com.example.model.Task
import com.example.model.TaskStatus
import com.example.ui.components.TaskIdsWordmark
import com.example.ui.design.TaskIdsColors
import com.example.ui.dialogs.ParentPinDialog
import com.example.ui.navigation.AppScreen
import com.example.viewmodel.MainViewModel

private val navy = TaskIdsColors.Ink
private val muted = TaskIdsColors.Muted
private val cardShape = RoundedCornerShape(26.dp)

@Composable
private fun ChildBackdrop(child: Child?, content: @Composable BoxScope.() -> Unit) {
    Box(Modifier.fillMaxSize()) {
        GameWorldBackground(GameThemeKey.from(child?.gameTheme ?: "SKY"), Modifier.fillMaxSize())
        content()
    }
}

@Composable
private fun WhiteCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.background(Color.White.copy(alpha = 0.97f), cardShape).padding(18.dp), content = content)
}

@Composable
private fun BrandHeader(child: Child?, onParents: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        TaskIdsWordmark()
        Spacer(Modifier.weight(1f))
        Row(
            Modifier.background(Color.White, CircleShape).padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = TaskIdsColors.Yellow, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(4.dp))
            Text("${child?.totalStars ?: 0}", color = navy, fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.width(8.dp))
        Box(Modifier.size(40.dp).clip(CircleShape).background(Color.White)
            .semantics { contentDescription = "PAIS" }.clickable(onClick = onParents)) {
            if (child != null) GameAvatar(child, Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun ChildBottomBar(active: String, onHome: () -> Unit, onMissions: () -> Unit, onRewards: () -> Unit, onMore: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
            .navigationBarsPadding().padding(vertical = 9.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        listOf(
            Triple("Início", Icons.Default.Home, onHome),
            Triple("Missões", Icons.Default.CheckCircle, onMissions),
            Triple("Recompensas", Icons.Default.Star, onRewards),
            Triple("Mais", Icons.Default.MoreVert, onMore)
        ).forEach { (label, icon, click) ->
            val selected = label == active
            Column(
                Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).clickable(onClick = click).padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(icon, label, tint = if (selected) TaskIdsColors.Blue else muted, modifier = Modifier.size(24.dp))
                Spacer(Modifier.height(2.dp))
                Text(label, color = if (selected) TaskIdsColors.Blue else muted, fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, maxLines = 1)
            }
        }
    }
}

@Composable
private fun RoundedAction(text: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(modifier.clip(RoundedCornerShape(22.dp)).background(color).clickable(onClick = onClick)
        .padding(horizontal = 16.dp, vertical = 15.dp), contentAlignment = Alignment.Center) {
        Text(text, color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun ChildMobileHome(viewModel: MainViewModel) {
    val child by viewModel.currentChild.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val current = child ?: return
    val active = tasks.filter { it.isActive }
    val completed = active.count { it.status == TaskStatus.COMPLETED }
    val next = active.firstOrNull { it.status != TaskStatus.COMPLETED }
    val progress = if (active.isEmpty()) 0f else completed.toFloat() / active.size
    var showMissions by remember { mutableStateOf(false) }
    var showPin by remember { mutableStateOf(false) }

    ChildBackdrop(current) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                item { BrandHeader(current) { showPin = true } }
                if (showMissions) {
                    item {
                        Text("Minhas missões", color = navy, fontSize = 29.sp, fontWeight = FontWeight.Black)
                        Text("Escolha uma atividade para começar.", color = muted, fontSize = 14.sp)
                    }
                    items(active, key = { it.id }) { task ->
                        MobileMissionRow(task, onClick = { if (task.status != TaskStatus.COMPLETED) viewModel.selectTask(task) })
                    }
                } else {
                    item {
                        Row(Modifier.fillMaxWidth().height(194.dp), verticalAlignment = Alignment.Bottom) {
                            Column(Modifier.weight(1f).padding(bottom = 15.dp)) {
                                Text("Oi, ${current.name}!", color = navy, fontSize = 31.sp, fontWeight = FontWeight.Black)
                                Text("Pronto para sua próxima conquista?", color = navy, fontSize = 18.sp, lineHeight = 22.sp)
                            }
                            GameAvatar(current, Modifier.width(168.dp).fillMaxHeight())
                        }
                    }
                    item {
                        WhiteCard(Modifier.fillMaxWidth()) {
                            Text("Seu progresso de hoje", color = navy, fontSize = 18.sp, fontWeight = FontWeight.Black)
                            Spacer(Modifier.height(13.dp))
                            LinearProgressIndicator({ progress }, Modifier.fillMaxWidth().height(13.dp).clip(CircleShape),
                                color = TaskIdsColors.Green, trackColor = TaskIdsColors.SoftBlue)
                            Spacer(Modifier.height(11.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("$completed de ${active.size} missões concluídas", color = muted, fontSize = 13.sp)
                                Text("${(progress * 100).toInt()}%", color = TaskIdsColors.Blue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SmallMetric("★", current.totalStars.toString(), "Estrelas", TaskIdsColors.Yellow, Modifier.weight(1f))
                            SmallMetric("ϟ", current.totalXp.toString(), "XP", TaskIdsColors.Blue, Modifier.weight(1f))
                            SmallMetric("◎", completed.toString(), "Conquistas", TaskIdsColors.Pink, Modifier.weight(1f))
                        }
                    }
                    if (next != null) item {
                        Text("Missão em destaque", color = navy, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        WhiteCard(Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MissionArt(next.iconKey, Modifier.size(78.dp))
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(next.title, color = navy, fontSize = 18.sp, fontWeight = FontWeight.Black, maxLines = 2)
                                    Text("${next.durationMinutes} min  •  ★ ${next.rewardStars}", color = muted, fontSize = 13.sp)
                                }
                                Box(Modifier.size(48.dp).clip(CircleShape).background(TaskIdsColors.Blue)
                                    .clickable { viewModel.selectTask(next) }, contentAlignment = Alignment.Center) {
                                    Text("▶", color = Color.White, fontSize = 21.sp)
                                }
                            }
                        }
                    }
                    item { RoundedAction("Ver todas as missões  →", TaskIdsColors.Purple, Modifier.fillMaxWidth()) { showMissions = true } }
                }
            }
            ChildBottomBar(if (showMissions) "Missões" else "Início", { showMissions = false },
                { showMissions = true }, { viewModel.navigateTo(AppScreen.Rewards) }, { showPin = true })
        }
        if (showPin) ParentPinDialog(viewModel, { showPin = false }) {
            showPin = false
            viewModel.navigateTo(AppScreen.Parent)
        }
    }
}

@Composable
private fun SmallMetric(icon: String, value: String, label: String, accent: Color, modifier: Modifier) {
    WhiteCard(modifier) {
        Text(icon, color = accent, fontSize = 24.sp, fontWeight = FontWeight.Black)
        Text(value, color = navy, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Text(label, color = muted, fontSize = 10.sp, maxLines = 1)
    }
}

@Composable
private fun MissionArt(iconKey: String, modifier: Modifier = Modifier) {
    Box(modifier.background(TaskIdsColors.SoftBlue, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
        if (iconKey.uppercase() == "BOOK") {
            Image(painterResource(R.drawable.mission_book), null, Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
        } else GameIcon(iconKey, Modifier.fillMaxSize(0.60f), tint = TaskIdsColors.Blue)
    }
}

@Composable
private fun MobileMissionRow(task: Task, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().background(Color.White, cardShape).clickable(onClick = onClick).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        MissionArt(task.iconKey, Modifier.size(58.dp))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(task.title, color = navy, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("${task.durationMinutes} min  •  ★ ${task.rewardStars}", color = muted, fontSize = 12.sp)
        }
        Text(if (task.status == TaskStatus.COMPLETED) "✓" else "›", color = TaskIdsColors.Blue,
            fontSize = 25.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ChildMobileMission(viewModel: MainViewModel, taskId: Int) {
    val child by viewModel.currentChild.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val timer by viewModel.timerState.collectAsState()
    val task = tasks.firstOrNull { it.id == taskId } ?: return
    ChildBackdrop(child) {
        Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 18.dp, vertical = 12.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("‹  Voltar", Modifier.clickable { viewModel.navigateTo(AppScreen.Home) }, color = navy, fontSize = 15.sp)
                Spacer(Modifier.weight(1f)); TaskIdsWordmark()
            }
            Spacer(Modifier.height(16.dp))
            WhiteCard(Modifier.fillMaxWidth().weight(1f)) {
                Text("MISSÃO EM ANDAMENTO", color = muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MissionArt(task.iconKey, Modifier.size(112.dp))
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(task.title, color = navy, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        Text(task.description, color = muted, fontSize = 13.sp, maxLines = 3)
                    }
                }
                Spacer(Modifier.weight(1f))
                Box(Modifier.fillMaxWidth().height(230.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(progress = { timer.progress.coerceIn(0f, 1f) }, modifier = Modifier.size(220.dp),
                        color = TaskIdsColors.Blue, trackColor = TaskIdsColors.SoftBlue, strokeWidth = 15.dp)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("%02d:%02d".format(timer.remainingSeconds / 60, timer.remainingSeconds % 60),
                            color = navy, fontSize = 46.sp, fontWeight = FontWeight.Black)
                        Text("Tempo restante", color = muted, fontSize = 14.sp)
                    }
                }
                Spacer(Modifier.weight(1f))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RoundedAction(if (timer.isRunning) "Pausar" else "Retomar", TaskIdsColors.Blue, Modifier.weight(1f)) {
                        if (timer.isRunning) viewModel.pauseTimer() else viewModel.resumeTimer()
                    }
                    RoundedAction("+5 min", TaskIdsColors.Purple, Modifier.weight(1f)) { viewModel.extendTimer(5) }
                }
                Spacer(Modifier.height(8.dp))
                RoundedAction("✓  Concluir missão", TaskIdsColors.Green, Modifier.fillMaxWidth()) {
                    viewModel.markTaskAsCompleted(task)
                }
            }
        }
    }
}

@Composable
fun ChildMobileComplete(viewModel: MainViewModel) {
    val child by viewModel.currentChild.collectAsState()
    val result by viewModel.lastMissionCompletion.collectAsState()
    val current = child ?: return
    val level = GameEngine.levelFor(current.totalXp)
    ChildBackdrop(current) {
        Column(Modifier.fillMaxSize().statusBarsPadding().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            TaskIdsWordmark()
            Spacer(Modifier.height(10.dp))
            GameAvatar(current, Modifier.fillMaxWidth().height(200.dp), celebrate = true)
            WhiteCard(Modifier.fillMaxWidth()) {
                Text("Missão concluída!", color = navy, fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text("Muito bem, ${current.name}!", color = muted, fontSize = 17.sp)
            }
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallMetric("★", "+${result?.stars ?: 0}", "Estrelas", TaskIdsColors.Yellow, Modifier.weight(1f))
                SmallMetric("ϟ", "+${result?.xp ?: 0}", "XP", TaskIdsColors.Blue, Modifier.weight(1f))
                SmallMetric("◎", "x${result?.combo ?: 0}", "Combo", TaskIdsColors.Pink, Modifier.weight(1f))
            }
            Spacer(Modifier.height(14.dp))
            WhiteCard(Modifier.fillMaxWidth()) {
                Text("Nível ${level.level}  •  ${level.title}", color = navy, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(9.dp))
                LinearProgressIndicator({ level.progress }, Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
                    color = TaskIdsColors.Green, trackColor = TaskIdsColors.SoftBlue)
            }
            Spacer(Modifier.weight(1f))
            RoundedAction("Continuar jornada  →", TaskIdsColors.Purple, Modifier.fillMaxWidth()) { viewModel.navigateTo(AppScreen.Home) }
            Spacer(Modifier.height(9.dp))
            RoundedAction("Recompensas", TaskIdsColors.Blue, Modifier.fillMaxWidth()) { viewModel.navigateTo(AppScreen.Rewards) }
            Spacer(Modifier.navigationBarsPadding())
        }
    }
}

@Composable
fun ChildMobileRewards(viewModel: MainViewModel) {
    val child by viewModel.currentChild.collectAsState()
    val rewards by viewModel.rewards.collectAsState()
    val message by viewModel.lastRewardMessage.collectAsState()
    ChildBackdrop(child) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item { BrandHeader(child) { viewModel.navigateTo(AppScreen.Home) } }
                item {
                    Text("Recompensas", color = navy, fontSize = 29.sp, fontWeight = FontWeight.Black)
                    Text("Troque estrelas por momentos especiais.", color = muted, fontSize = 14.sp)
                }
                if (!message.isNullOrBlank()) item { WhiteCard(Modifier.fillMaxWidth()) { Text(message ?: "", color = navy) } }
                items(rewards, key = { it.id }) { reward ->
                    val affordable = (child?.totalStars ?: 0) >= reward.costStars
                    Row(Modifier.fillMaxWidth().background(Color.White, cardShape)
                        .clickable(enabled = affordable) { viewModel.redeemReward(reward) }.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        RewardArt(reward.type, Modifier.size(82.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(reward.title, color = navy, fontSize = 16.sp, fontWeight = FontWeight.Black, maxLines = 2)
                            Text("★ ${reward.costStars} estrelas", color = TaskIdsColors.Orange, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(if (affordable) "Desbloquear" else "Junte mais estrelas", color = if (affordable) TaskIdsColors.Green else muted, fontSize = 12.sp)
                        }
                    }
                }
            }
            ChildBottomBar("Recompensas", { viewModel.navigateTo(AppScreen.Home) },
                { viewModel.navigateTo(AppScreen.Home) }, {}, { viewModel.navigateTo(AppScreen.Home) })
        }
    }
}

@Composable
fun ChildMobileScreenTime(viewModel: MainViewModel) {
    val child by viewModel.currentChild.collectAsState()
    val remaining by viewModel.screenTimeRemainingSeconds.collectAsState()
    ChildBackdrop(child) {
        Column(Modifier.fillMaxSize().statusBarsPadding().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            TaskIdsWordmark(); Spacer(Modifier.weight(1f))
            WhiteCard(Modifier.fillMaxWidth()) {
                Text("Tempo de tela aprovado", color = navy, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("Um momento combinado com sua família.", color = muted, fontSize = 14.sp)
                Spacer(Modifier.height(24.dp))
                Text("%02d:%02d".format(remaining / 60, remaining % 60), color = navy,
                    fontSize = 64.sp, fontWeight = FontWeight.Black, modifier = Modifier.align(Alignment.CenterHorizontally))
                Text("Tempo restante", color = muted, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(Modifier.height(24.dp))
                RoundedAction("Encerrar", TaskIdsColors.Blue, Modifier.fillMaxWidth()) { viewModel.stopScreenTime() }
            }
            Spacer(Modifier.weight(1f))
        }
    }
}
