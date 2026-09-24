package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Child
import com.example.model.Reward
import com.example.model.Routine
import com.example.model.Task
import com.example.ui.design.TaskIdsColors
import com.example.ui.components.TaskIdsWordmark
import com.example.ui.game.GameAvatar
import com.example.ui.game.GameIcon
import com.example.ui.game.GameWorldBackground
import com.example.game.GameThemeKey
import com.example.model.TaskStatus
import com.example.ui.library.ParentLibraryContent
import com.example.ui.navigation.AppScreen
import com.example.viewmodel.MainViewModel

private enum class MobileParentSection(val label: String) {
    OVERVIEW("Resumo"),
    CHILDREN("Crianças"),
    MISSIONS("Missões"),
    ROUTINES("Rotinas"),
    REWARDS("Prêmios"),
    REPORTS("Relatórios"),
    LIBRARY("Biblioteca"),
    SETTINGS("Ajustes")
}

@Composable
fun ParentMobileDashboardScreen(viewModel: MainViewModel) {
    val child by viewModel.currentChild.collectAsState()
    val children by viewModel.children.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val rewards by viewModel.rewards.collectAsState()
    val routines by viewModel.routines.collectAsState()
    val executions by viewModel.executions.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()

    var section by remember { mutableStateOf(MobileParentSection.OVERVIEW) }
    var showAddTask by remember { mutableStateOf(false) }
    var showAddProfile by remember { mutableStateOf(false) }
    var showAddRoutine by remember { mutableStateOf(false) }
    var showAddReward by remember { mutableStateOf(false) }
    var showMore by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
      GameWorldBackground(GameThemeKey.from(child?.gameTheme ?: "SKY"), Modifier.fillMaxSize())
      Column(Modifier.fillMaxSize().statusBarsPadding()) {
        MobileParentHeader(
            child = child,
            onExit = { viewModel.navigateTo(AppScreen.Home) }
        )

        if (showMore) Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MobileParentSection.entries.forEach { item ->
                val selected = item == section
                Text(
                    text = item.label,
                    color = if (selected) Color.White else TaskIdsColors.Ink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .background(
                            if (selected) TaskIdsColors.Blue else TaskIdsColors.SoftBlue,
                            RoundedCornerShape(22.dp)
                        )
                        .clickable { section = item }
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            when (section) {
                MobileParentSection.OVERVIEW -> MobileOverview(
                    child = child,
                    tasks = tasks,
                    routines = routines,
                    rewards = rewards,
                    completedCount = executions.size,
                    onMissions = { section = MobileParentSection.MISSIONS },
                    onLibrary = { section = MobileParentSection.LIBRARY }
                )
                MobileParentSection.CHILDREN -> MobileChildren(
                    children = children,
                    currentId = child?.id,
                    onSelect = { viewModel.selectChild(it.id) },
                    onAdd = { showAddProfile = true }
                )
                MobileParentSection.MISSIONS -> MobileMissions(
                    tasks = tasks,
                    onAdd = { showAddTask = true },
                    onToggle = { viewModel.editTask(it.copy(isActive = !it.isActive)) },
                    onDelete = viewModel::deleteTask
                )
                MobileParentSection.ROUTINES -> MobileRoutines(
                    routines = routines,
                    onAdd = { showAddRoutine = true }
                )
                MobileParentSection.REWARDS -> MobileRewards(
                    rewards = rewards,
                    onAdd = { showAddReward = true }
                )
                MobileParentSection.REPORTS -> MobileReports(
                    child = child,
                    executions = executions.map {
                        "${it.taskTitle} • +${it.earnedStars} estrelas • +${it.earnedXp} XP"
                    }
                )
                MobileParentSection.LIBRARY -> ParentLibraryContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 12.dp)
                )
                MobileParentSection.SETTINGS -> MobileSettings(
                    child = child,
                    soundEnabled = soundEnabled,
                    onToggleSound = { viewModel.setSoundEnabled(!soundEnabled) },
                    onUpdatePin = viewModel::updateParentalPin,
                    onReset = viewModel::restartAllTasks,
                    onCharacterChange = { type ->
                        child?.let {
                            viewModel.updateAvatar(
                                type,
                                it.avatarSkinTone,
                                it.avatarHairStyle,
                                it.avatarHairColor,
                                it.avatarOutfitColor
                            )
                        }
                    }
                )
            }
        }
        MobileParentBottomBar(
            active = section,
            onHome = { viewModel.navigateTo(AppScreen.Home) },
            onOverview = { section = MobileParentSection.OVERVIEW; showMore = false },
            onRewards = { section = MobileParentSection.REWARDS; showMore = false },
            onMore = { showMore = !showMore }
        )
      }
    }

    if (showAddTask) {
        MobileTaskDialog(
            onDismiss = { showAddTask = false },
            onSave = { title, minutes, stars, xp ->
                viewModel.addTask(
                    title = title,
                    durationMinutes = minutes,
                    icon = "",
                    description = "",
                    rewardStars = stars,
                    rewardXp = xp,
                    iconKey = "GENERIC"
                )
                showAddTask = false
            }
        )
    }

    if (showAddProfile) {
        MobileTextDialog(
            title = "Novo perfil",
            label = "Nome da criança",
            onDismiss = { showAddProfile = false },
            onSave = {
                viewModel.addChild(it, "")
                showAddProfile = false
            }
        )
    }

    if (showAddRoutine) {
        MobileRoutineDialog(
            onDismiss = { showAddRoutine = false },
            onSave = { title, time ->
                viewModel.addRoutine(title, "", time.ifBlank { null }, "1,2,3,4,5")
                showAddRoutine = false
            }
        )
    }

    if (showAddReward) {
        MobileRewardDialog(
            onDismiss = { showAddReward = false },
            onSave = { title, stars ->
                viewModel.addReward(title, stars, "", "")
                showAddReward = false
            }
        )
    }
}

@Composable
private fun MobileParentHeader(child: Child?, onExit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TaskIdsWordmark()
        Spacer(Modifier.weight(1f))
        Text(
            "Sair",
            color = TaskIdsColors.Ink,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(18.dp))
                .clickable(onClick = onExit)
                .padding(horizontal = 14.dp, vertical = 9.dp)
        )
    }
}

@Composable
private fun MobileParentBottomBar(
    active: MobileParentSection,
    onHome: () -> Unit,
    onOverview: () -> Unit,
    onRewards: () -> Unit,
    onMore: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .navigationBarsPadding().padding(horizontal = 6.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        listOf(
            Triple("Início", Icons.Default.Home, onHome),
            Triple("Área dos Pais", Icons.Default.CheckCircle, onOverview),
            Triple("Recompensas", Icons.Default.Star, onRewards),
            Triple("Mais", Icons.Default.MoreVert, onMore)
        ).forEach { (label, icon, click) ->
            val selected = when (label) {
                "Área dos Pais" -> active == MobileParentSection.OVERVIEW
                "Recompensas" -> active == MobileParentSection.REWARDS
                else -> false
            }
            Column(Modifier.weight(1f).clickable(onClick = click).padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(icon, label, tint = if (selected) TaskIdsColors.Blue else TaskIdsColors.Muted,
                    modifier = Modifier.size(22.dp))
                Text(label, color = if (selected) TaskIdsColors.Blue else TaskIdsColors.Muted,
                    fontSize = 10.sp, maxLines = 1, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun MobileOverview(
    child: Child?,
    tasks: List<Task>,
    routines: List<Routine>,
    rewards: List<Reward>,
    completedCount: Int,
    onMissions: () -> Unit,
    onLibrary: () -> Unit
) {
    val active = tasks.filter { it.isActive }
    val completed = active.count { it.status == TaskStatus.COMPLETED }
    val progress = if (active.isEmpty()) 0f else completed.toFloat() / active.size
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth().height(192.dp), verticalAlignment = Alignment.Bottom) {
                Column(Modifier.weight(1f).padding(bottom = 12.dp)) {
                    Text("Área dos Pais", color = TaskIdsColors.Ink, fontSize = 31.sp, fontWeight = FontWeight.Black)
                    Text("Acompanhe o progresso e apoie as conquistas de ${child?.name ?: "sua criança"}.",
                        color = TaskIdsColors.Muted, fontSize = 15.sp, lineHeight = 19.sp)
                }
                if (child != null) GameAvatar(child, Modifier.width(153.dp).fillMaxHeight())
            }
        }
        if (child != null) item {
            Row(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(24.dp))
                .padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                GameAvatar(child, Modifier.size(54.dp))
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(child.name, color = TaskIdsColors.Ink, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    Text("$completed de ${active.size} missões concluídas", color = TaskIdsColors.Muted, fontSize = 12.sp)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                MobileMetric("Estrelas", (child?.totalStars ?: 0).toString(), TaskIdsColors.Yellow, Modifier.weight(1f))
                MobileMetric("XP", (child?.totalXp ?: 0).toString(), TaskIdsColors.Blue, Modifier.weight(1f))
                MobileMetric("Missões", active.size.toString(), TaskIdsColors.Purple, Modifier.weight(1f))
                MobileMetric("Concluídas", completed.toString(), TaskIdsColors.Pink, Modifier.weight(1f))
            }
        }
        item {
            Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(26.dp)).padding(17.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Progresso de hoje", color = TaskIdsColors.Ink, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    Text("${(progress * 100).toInt()}%", color = TaskIdsColors.Blue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator({ progress }, Modifier.fillMaxWidth().height(14.dp),
                    color = TaskIdsColors.Green, trackColor = TaskIdsColors.SoftBlue)
                Spacer(Modifier.height(9.dp))
                Text("$completed de ${active.size} missões concluídas", color = TaskIdsColors.Muted, fontSize = 13.sp)
            }
        }
        item {
            Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(26.dp)).padding(15.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Missões pendentes", color = TaskIdsColors.Ink, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    Text("Ver todas  ›", color = TaskIdsColors.Blue, fontSize = 13.sp,
                        modifier = Modifier.clickable(onClick = onMissions))
                }
                Spacer(Modifier.height(10.dp))
                val pending = active.filter { it.status != TaskStatus.COMPLETED }.take(3)
                if (pending.isEmpty()) Text("Tudo concluído por hoje!", color = TaskIdsColors.Muted)
                pending.forEach { task ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable(onClick = onMissions),
                        verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(50.dp).background(TaskIdsColors.SoftBlue, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center) {
                            if (task.iconKey == "BOOK") Image(painterResource(R.drawable.mission_book), null,
                                Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                            else GameIcon(task.iconKey, Modifier.size(30.dp), tint = TaskIdsColors.Blue)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(task.title, color = TaskIdsColors.Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text("${task.durationMinutes} min  •  ★ ${task.rewardStars}", color = TaskIdsColors.Muted, fontSize = 12.sp)
                        }
                        Text("›", color = TaskIdsColors.Blue, fontSize = 23.sp)
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuickAction("Gerenciar missões  →", TaskIdsColors.Blue, Modifier.weight(1f), onMissions)
                QuickAction("Abrir biblioteca  →", TaskIdsColors.Purple, Modifier.weight(1f), onLibrary)
            }
        }
    }
}

@Composable
private fun MobileMetric(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(22.dp))
            .padding(14.dp)
    ) {
        Text("★", color = accent, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(8.dp))
        Text(value, color = TaskIdsColors.Ink, fontSize = 21.sp, fontWeight = FontWeight.Black)
        Text(label, color = TaskIdsColors.Muted, fontSize = 10.sp, maxLines = 1)
    }
}

@Composable
private fun QuickAction(text: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Text(
        text,
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        modifier = modifier
            .background(color, RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    )
}

@Composable
private fun MobileChildren(
    children: List<Child>,
    currentId: Long?,
    onSelect: (Child) -> Unit,
    onAdd: () -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            SectionTitle("Crianças", "Perfis, progresso e personagem.")
            ActionButton("ADICIONAR PERFIL", TaskIdsColors.Green, onAdd)
        }
        items(children) { profile ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .clickable { onSelect(profile) }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GameAvatar(profile, Modifier.size(70.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(profile.name, color = TaskIdsColors.Ink, fontSize = 17.sp, fontWeight = FontWeight.Black)
                    Text("${profile.totalStars} estrelas • ${profile.totalXp} XP", color = TaskIdsColors.Muted, fontSize = 13.sp)
                    Text("Sequência: ${profile.currentStreak} dias", color = TaskIdsColors.Muted, fontSize = 13.sp)
                }
                if (profile.id == currentId) {
                    Text("ATIVO", color = TaskIdsColors.Blue, fontWeight = FontWeight.Black, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun MobileMissions(
    tasks: List<Task>,
    onAdd: () -> Unit,
    onToggle: (Task) -> Unit,
    onDelete: (Task) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        item {
            SectionTitle("Missões", "Crie, ative, pause ou remova tarefas.")
            ActionButton("NOVA MISSÃO", TaskIdsColors.Blue, onAdd)
        }
        items(tasks) { task ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(22.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GameIcon(task.iconKey, Modifier.size(34.dp), TaskIdsColors.Blue)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(task.title, color = TaskIdsColors.Ink, fontWeight = FontWeight.Black)
                    Text(
                        "${task.durationMinutes} min • +${task.rewardStars} estrelas • +${task.rewardXp} XP",
                        color = TaskIdsColors.Muted,
                        fontSize = 10.sp
                    )
                }
                Text(
                    if (task.isActive) "ATIVA" else "PAUSADA",
                    color = if (task.isActive) TaskIdsColors.Green else TaskIdsColors.Muted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .clickable { onToggle(task) }
                        .padding(8.dp)
                )
                Text(
                    "EXCLUIR",
                    color = TaskIdsColors.Pink,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .clickable { onDelete(task) }
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun MobileRoutines(routines: List<Routine>, onAdd: () -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        item {
            SectionTitle("Rotinas", "Agrupe hábitos por período e dia.")
            ActionButton("NOVA ROTINA", TaskIdsColors.Purple, onAdd)
        }
        items(routines) { routine ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(22.dp))
                    .padding(14.dp)
            ) {
                Text(routine.title, color = TaskIdsColors.Ink, fontWeight = FontWeight.Black)
                Text(
                    "Horário: ${routine.startTime ?: "livre"} • Dias: ${routine.daysCsv}",
                    color = TaskIdsColors.Muted,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun MobileRewards(rewards: List<Reward>, onAdd: () -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        item {
            SectionTitle("Recompensas", "Defina metas e prêmios por estrelas.")
            ActionButton("NOVA RECOMPENSA", TaskIdsColors.Orange, onAdd)
        }
        items(rewards) { reward ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(22.dp))
                    .padding(14.dp)
            ) {
                Text(reward.title, color = TaskIdsColors.Ink, fontWeight = FontWeight.Black)
                Text("${reward.costStars} estrelas", color = TaskIdsColors.Orange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                if (reward.description.isNotBlank()) {
                    Text(reward.description, color = TaskIdsColors.Muted, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun MobileReports(child: Child?, executions: List<String>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        item {
            SectionTitle("Relatórios", "Histórico recente de missões e evolução.")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MobileMetric("XP total", (child?.totalXp ?: 0).toString(), TaskIdsColors.Blue, Modifier.weight(1f))
                MobileMetric("Sequência", (child?.currentStreak ?: 0).toString(), TaskIdsColors.Green, Modifier.weight(1f))
            }
        }
        if (executions.isEmpty()) {
            item {
                Text(
                    "Ainda não há missões concluídas neste perfil.",
                    color = TaskIdsColors.Muted,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(executions.take(30)) { line ->
                Text(
                    line,
                    color = TaskIdsColors.Ink,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                )
            }
        }
    }
}

@Composable
private fun MobileSettings(
    child: Child?,
    soundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onUpdatePin: (String) -> Boolean,
    onReset: () -> Unit,
    onCharacterChange: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var pinMessage by remember { mutableStateOf("") }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { SectionTitle("Configurações", "Segurança, som, personagem e rotina diária.") }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Text("PIN parental", color = TaskIdsColors.Ink, fontWeight = FontWeight.Black)
                Text("O PIN padrão inicial é 0000.", color = TaskIdsColors.Muted, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it.filter(Char::isDigit).take(4) },
                    label = { Text("Novo PIN de 4 dígitos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                ActionButton("ALTERAR PIN", TaskIdsColors.Blue) {
                    pinMessage = if (onUpdatePin(pin)) {
                        pin = ""
                        "PIN atualizado."
                    } else {
                        "Digite exatamente 4 números."
                    }
                }
                if (pinMessage.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(pinMessage, color = TaskIdsColors.Muted, fontSize = 13.sp)
                }
            }
        }
        item {
            SettingsRow(
                title = "Som e alertas",
                detail = if (soundEnabled) "Ativado" else "Desativado",
                action = if (soundEnabled) "DESATIVAR" else "ATIVAR",
                onClick = onToggleSound
            )
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Text("Personagem", color = TaskIdsColors.Ink, fontWeight = FontWeight.Black)
                Text("Escolha a base visual do perfil infantil.", color = TaskIdsColors.Muted, fontSize = 13.sp)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("BOY" to "MENINO", "GIRL" to "MENINA").forEach { (key, label) ->
                        Text(
                            label,
                            color = if (child?.avatarCharacter == key) Color.White else TaskIdsColors.Ink,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .background(
                                    if (child?.avatarCharacter == key) TaskIdsColors.Blue else TaskIdsColors.SoftBg,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { onCharacterChange(key) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
        item {
            SettingsRow(
                title = "Reiniciar jornada diária",
                detail = "Mantém histórico, XP e estrelas.",
                action = "REINICIAR",
                onClick = onReset
            )
        }
    }
}

@Composable
private fun SettingsRow(title: String, detail: String, action: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, color = TaskIdsColors.Ink, fontWeight = FontWeight.Black)
            Text(detail, color = TaskIdsColors.Muted, fontSize = 13.sp)
        }
        ActionButton(action, TaskIdsColors.Ink, onClick)
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Text(title, color = TaskIdsColors.Ink, fontSize = 24.sp, fontWeight = FontWeight.Black)
    Text(subtitle, color = TaskIdsColors.Muted, fontSize = 14.sp)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun ActionButton(text: String, color: Color, onClick: () -> Unit) {
    Text(
        text,
        color = Color.White,
        fontSize = 10.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier
            .background(color, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    )
}

@Composable
private fun MobileTextDialog(
    title: String,
    label: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var value by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it.take(40) },
                label = { Text(label) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { if (value.isNotBlank()) onSave(value.trim()) }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun MobileTaskDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int, Int, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("15") }
    var stars by remember { mutableStateOf("10") }
    var xp by remember { mutableStateOf("100") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova missão") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(title, { title = it.take(60) }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(minutes, { minutes = it.filter(Char::isDigit).take(3) }, label = { Text("Minutos") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(stars, { stars = it.filter(Char::isDigit).take(3) }, label = { Text("Estrelas") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(xp, { xp = it.filter(Char::isDigit).take(4) }, label = { Text("XP") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) onSave(
                        title.trim(),
                        minutes.toIntOrNull()?.coerceIn(1, 240) ?: 15,
                        stars.toIntOrNull()?.coerceIn(1, 100) ?: 10,
                        xp.toIntOrNull()?.coerceIn(10, 1000) ?: 100
                    )
                }
            ) { Text("Criar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun MobileRoutineDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova rotina") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(title, { title = it.take(50) }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(time, { time = it.take(5) }, label = { Text("Horário (HH:mm)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = { if (title.isNotBlank()) onSave(title.trim(), time) }) { Text("Criar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun MobileRewardDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var stars by remember { mutableStateOf("50") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova recompensa") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(title, { title = it.take(60) }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(stars, { stars = it.filter(Char::isDigit).take(4) }, label = { Text("Custo em estrelas") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) onSave(title.trim(), stars.toIntOrNull()?.coerceIn(1, 9999) ?: 50)
                }
            ) { Text("Criar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
