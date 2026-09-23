package com.taskids.app.ui.parental

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskids.app.domain.model.ApprovedContent
import com.taskids.app.domain.model.ChildProfile
import com.taskids.app.domain.model.Reward
import com.taskids.app.domain.model.RewardType
import com.taskids.app.domain.model.RoutinePeriod
import com.taskids.app.domain.model.Task
import com.taskids.app.ui.components.FocusSurface
import com.taskids.app.ui.theme.Blue500
import com.taskids.app.ui.theme.Cloud100
import com.taskids.app.ui.theme.Cloud50
import com.taskids.app.ui.theme.Green500
import com.taskids.app.ui.theme.Ink600
import com.taskids.app.ui.theme.Ink900
import com.taskids.app.ui.theme.Orange500
import com.taskids.app.ui.theme.Purple500
import com.taskids.app.ui.theme.Red500
import com.taskids.app.ui.theme.White
import com.taskids.app.ui.theme.Yellow500
import com.taskids.app.viewmodel.MainViewModel
import com.taskids.app.viewmodel.ParentDashboardUiState
import com.taskids.app.viewmodel.Screen

private enum class ParentTab(val label: String, val icon: String) {
    OVERVIEW("Visão geral", "⌂"),
    TASKS("Tarefas", "✓"),
    REWARDS("Recompensas", "★"),
    PROFILES("Perfis", "☺"),
    CONTENT("Conteúdo", "▶"),
    SETTINGS("Configurações", "⚙")
}

@Composable
fun ParentScreen(viewModel: MainViewModel) {
    val child by viewModel.currentChild.collectAsState()
    val children by viewModel.children.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val rewards by viewModel.rewards.collectAsState()
    val dashboard by viewModel.parentDashboard.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val content by viewModel.approvedContent.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()

    var tab by remember { mutableStateOf(ParentTab.OVERVIEW) }
    var taskEditor by remember { mutableStateOf<Task?>(null) }
    var newTask by remember { mutableStateOf(false) }
    var newReward by remember { mutableStateOf(false) }
    var newProfile by remember { mutableStateOf(false) }
    var newContent by remember { mutableStateOf(false) }

    Row(Modifier.fillMaxSize().background(Cloud50)) {
        Column(
            modifier = Modifier.width(220.dp).fillMaxHeight().background(White).padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(38.dp).background(Blue500, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("★", color = White, fontSize = 20.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("TASKIDS", color = Ink900, fontWeight = FontWeight.Black, fontSize = 20.sp)
                }
                Spacer(Modifier.height(26.dp))
                ParentTab.entries.forEach { item ->
                    val selected = tab == item
                    FocusSurface(
                        onClick = { tab = item },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        background = if (selected) Color(0xFFE8F1FF) else Color.Transparent,
                        focusColor = Blue500,
                        shape = RoundedCornerShape(12.dp),
                        padding = 10.dp
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.icon, color = if (selected) Blue500 else Ink600)
                            Spacer(Modifier.width(9.dp))
                            Text(
                                item.label,
                                color = if (selected) Blue500 else Ink600,
                                fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            FocusSurface(
                onClick = { viewModel.navigateTo(Screen.Home) },
                modifier = Modifier.fillMaxWidth(),
                background = Cloud100,
                focusColor = Blue500,
                shape = RoundedCornerShape(12.dp),
                padding = 10.dp
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ArrowBack, null, tint = Ink900, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Voltar para Kids", color = Ink900, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Column(Modifier.weight(1f).fillMaxHeight().padding(26.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Área dos Pais", color = Ink900, fontSize = 30.sp, fontWeight = FontWeight.Black)
                    Text(
                        "Gerencie a rotina e acompanhe o progresso com leveza.",
                        color = Ink600,
                        fontSize = 13.sp
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(child?.avatar ?: "🧒", fontSize = 30.sp)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(child?.name ?: "Perfil", color = Ink900, fontWeight = FontWeight.Black)
                        Text(
                            "${child?.age ?: 0} anos • ⭐ ${child?.totalStars ?: 0}",
                            color = Ink600,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))

            when (tab) {
                ParentTab.OVERVIEW -> OverviewTab(
                    dashboard = dashboard,
                    unlocked = achievements.count { it.unlocked },
                    stars = child?.totalStars ?: 0
                )
                ParentTab.TASKS -> TasksTab(
                    tasks = tasks,
                    onAdd = { newTask = true },
                    onEdit = { taskEditor = it },
                    onToggle = viewModel::toggleTask,
                    onDelete = viewModel::deleteTask
                )
                ParentTab.REWARDS -> RewardsTab(
                    rewards = rewards,
                    onAdd = { newReward = true },
                    onDelete = viewModel::deleteReward
                )
                ParentTab.PROFILES -> ProfilesTab(
                    children = children,
                    currentId = child?.id ?: 0,
                    onSelect = viewModel::selectChild,
                    onAdd = { newProfile = true }
                )
                ParentTab.CONTENT -> ContentTab(
                    content = content,
                    onAdd = { newContent = true },
                    onDelete = viewModel::deleteApprovedContent
                )
                ParentTab.SETTINGS -> SettingsTab(soundEnabled, viewModel)
            }
        }
    }

    if (newTask || taskEditor != null) {
        TaskDialog(
            task = taskEditor,
            onDismiss = { newTask = false; taskEditor = null },
            onSave = { old, title, desc, minutes, icon, stars, days, period ->
                if (old == null) {
                    viewModel.addTask(title, desc, minutes, icon, stars, days, period)
                } else {
                    viewModel.updateTask(
                        old.copy(
                            title = title,
                            description = desc,
                            durationMinutes = minutes,
                            icon = icon,
                            rewardPoints = stars,
                            recurrenceMask = days,
                            routinePeriod = period
                        )
                    )
                }
                newTask = false
                taskEditor = null
            }
        )
    }
    if (newReward) {
        RewardDialog(
            onDismiss = { newReward = false },
            onSave = { title, icon, cost, type, minutes ->
                viewModel.addReward(title, icon, cost, type, minutes)
                newReward = false
            }
        )
    }
    if (newProfile) {
        ProfileDialog(
            onDismiss = { newProfile = false },
            onSave = { name, age, avatar, color ->
                viewModel.addChild(name, age, avatar, color)
                newProfile = false
            }
        )
    }
    if (newContent) {
        ContentDialog(
            onDismiss = { newContent = false },
            onSave = { title, input ->
                viewModel.addApprovedContent(title, input) { if (it) newContent = false }
            }
        )
    }
}

@Composable
private fun OverviewTab(
    dashboard: ParentDashboardUiState,
    unlocked: Int,
    stars: Int
) {
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricCard("Concluídas", dashboard.completedLast7Days.toString(), Blue500, Modifier.weight(1f))
            MetricCard("Estrelas/7d", dashboard.starsLast7Days.toString(), Yellow500, Modifier.weight(1f))
            MetricCard("Sequência", "${dashboard.streakDays}d", Orange500, Modifier.weight(1f))
            MetricCard("Conquistas", unlocked.toString(), Purple500, Modifier.weight(1f))
            MetricCard("Saldo", "⭐ $stars", Green500, Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))
        Row(
            Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                Modifier.weight(1.5f).fillMaxHeight().background(White, RoundedCornerShape(22.dp)).padding(20.dp)
            ) {
                Text("Ritmo da semana", color = Ink900, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Text("Tarefas concluídas por dia", color = Ink600, fontSize = 11.sp)
                Spacer(Modifier.height(16.dp))
                val max = (dashboard.dailyReports.maxOfOrNull { it.completed } ?: 1).coerceAtLeast(1)
                Row(
                    Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    dashboard.dailyReports.forEach { report ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(report.completed.toString(), color = Blue500, fontSize = 10.sp)
                            Spacer(Modifier.height(4.dp))
                            Box(
                                Modifier.width(28.dp)
                                    .height((24 + 90 * report.completed / max).dp)
                                    .background(Blue500, RoundedCornerShape(8.dp))
                            )
                            Spacer(Modifier.height(5.dp))
                            Text(report.dateKey.takeLast(5), color = Ink600, fontSize = 9.sp)
                        }
                    }
                }
            }
            Column(
                Modifier.weight(1f).fillMaxHeight().background(White, RoundedCornerShape(22.dp)).padding(20.dp)
            ) {
                Text("Resumo", color = Ink900, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(Modifier.height(14.dp))
                InfoLine("⏱", "Tempo médio", "${dashboard.averageMinutes} min")
                InfoLine("🔥", "Sequência", "${dashboard.streakDays} dias")
                InfoLine("⭐", "Estrelas na semana", dashboard.starsLast7Days.toString())
                Spacer(Modifier.height(10.dp))
                Text(
                    "O progresso é individual: o TASKIDS não cria ranking entre crianças.",
                    color = Ink600,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier
) {
    Column(modifier.background(White, RoundedCornerShape(18.dp)).padding(14.dp)) {
        Box(Modifier.size(28.dp).background(color.copy(alpha = .14f), CircleShape), contentAlignment = Alignment.Center) {
            Box(Modifier.size(9.dp).background(color, CircleShape))
        }
        Spacer(Modifier.height(10.dp))
        Text(value, color = Ink900, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Text(title, color = Ink600, fontSize = 10.sp)
    }
}

@Composable
private fun InfoLine(icon: String, title: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(36.dp).background(Cloud100, CircleShape), contentAlignment = Alignment.Center) {
            Text(icon)
        }
        Spacer(Modifier.width(9.dp))
        Column {
            Text(title, color = Ink600, fontSize = 10.sp)
            Text(value, color = Ink900, fontWeight = FontWeight.Black, fontSize = 14.sp)
        }
    }
}

@Composable
private fun TasksTab(
    tasks: List<Task>,
    onAdd: () -> Unit,
    onEdit: (Task) -> Unit,
    onToggle: (Task) -> Unit,
    onDelete: (Task) -> Unit
) {
    Panel {
        Header("Tarefas e rotinas", "Dias, período, duração e estrelas.", "Adicionar tarefa", onAdd)
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tasks, key = { it.id }) { task ->
                Row(
                    Modifier.fillMaxWidth().background(Cloud50, RoundedCornerShape(14.dp)).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(42.dp).background(Color(0xFFE8F1FF), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(task.icon, fontSize = 24.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(task.title, color = Ink900, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text(
                            "${task.routinePeriod.icon} ${task.routinePeriod.label} • ${task.durationMinutes} min • ⭐ ${task.rewardPoints}",
                            color = Ink600,
                            fontSize = 10.sp
                        )
                    }
                    Switch(checked = task.isEnabled, onCheckedChange = { onToggle(task) })
                    Spacer(Modifier.width(8.dp))
                    TinyButton(Icons.Default.Edit, Blue500) { onEdit(task) }
                    Spacer(Modifier.width(5.dp))
                    TinyButton(Icons.Default.Delete, Red500) { onDelete(task) }
                }
            }
        }
    }
}

@Composable
private fun RewardsTab(rewards: List<Reward>, onAdd: () -> Unit, onDelete: (Reward) -> Unit) {
    Panel {
        Header("Recompensas", "A família define o que pode ser resgatado.", "Nova recompensa", onAdd)
        Spacer(Modifier.height(14.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(rewards, key = { it.id }) { reward ->
                Column(
                    Modifier.width(220.dp).background(Cloud50, RoundedCornerShape(20.dp)).padding(16.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(reward.icon, fontSize = 34.sp)
                        TinyButton(Icons.Default.Delete, Red500) { onDelete(reward) }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(reward.title, color = Ink900, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    Text(
                        if (reward.type == RewardType.SCREEN_TIME) {
                            "${reward.valueMinutes} min de tela"
                        } else {
                            reward.type.name.lowercase()
                        },
                        color = Ink600,
                        fontSize = 10.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Text("⭐ ${reward.costStars}", color = Orange500, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun ProfilesTab(
    children: List<ChildProfile>,
    currentId: Int,
    onSelect: (Int) -> Unit,
    onAdd: () -> Unit
) {
    Panel {
        Header(
            "Perfis das crianças",
            "Rotina, histórico e estrelas são independentes por perfil.",
            "Adicionar perfil",
            onAdd
        )
        Spacer(Modifier.height(14.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(children, key = { it.id }) { child ->
                val selected = child.id == currentId
                FocusSurface(
                    onClick = { onSelect(child.id) },
                    modifier = Modifier.width(190.dp).height(175.dp),
                    background = if (selected) Color(0xFFE8F1FF) else Cloud50,
                    focusColor = Blue500,
                    shape = RoundedCornerShape(22.dp),
                    padding = 16.dp
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(child.avatar, fontSize = 50.sp)
                        Text(child.name, color = Ink900, fontWeight = FontWeight.Black, fontSize = 17.sp)
                        Text("${child.age} anos • ⭐ ${child.totalStars}", color = Ink600, fontSize = 11.sp)
                        if (selected) Text("Perfil ativo", color = Blue500, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentTab(
    content: List<ApprovedContent>,
    onAdd: () -> Unit,
    onDelete: (ApprovedContent) -> Unit
) {
    Panel {
        Header(
            "Conteúdo aprovado",
            "Vídeos específicos substituem navegação livre no YouTube.",
            "Adicionar vídeo",
            onAdd
        )
        Spacer(Modifier.height(14.dp))
        if (content.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛡️", fontSize = 46.sp)
                    Text("Nenhum vídeo aprovado", color = Ink900, fontWeight = FontWeight.Black)
                    Text(
                        "Cadastre conteúdos antes de liberar recompensas de tela.",
                        color = Ink600,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(content, key = { it.id }) { item ->
                    Row(
                        Modifier.fillMaxWidth().background(Cloud50, RoundedCornerShape(14.dp)).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("▶️", fontSize = 24.sp)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(item.title, color = Ink900, fontWeight = FontWeight.Black)
                            Text("ID: ${item.youtubeVideoId}", color = Ink600, fontSize = 10.sp)
                        }
                        TinyButton(Icons.Default.Delete, Red500) { onDelete(item) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsTab(soundEnabled: Boolean, viewModel: MainViewModel) {
    var pin by remember { mutableStateOf("") }
    Panel {
        Text("Configurações", color = Ink900, fontWeight = FontWeight.Black, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))
        Row(
            Modifier.fillMaxWidth().background(Cloud50, RoundedCornerShape(16.dp)).padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Efeitos sonoros", color = Ink900, fontWeight = FontWeight.Black)
                Text("Conclusões e alertas do timer", color = Ink600, fontSize = 10.sp)
            }
            Switch(checked = soundEnabled, onCheckedChange = viewModel::setSoundEnabled)
        }
        Spacer(Modifier.height(12.dp))
        Column(
            Modifier.fillMaxWidth().background(Cloud50, RoundedCornerShape(16.dp)).padding(14.dp)
        ) {
            Text("Alterar PIN parental", color = Ink900, fontWeight = FontWeight.Black)
            Text("Hash + salt e bloqueio temporário após tentativas repetidas.", color = Ink600, fontSize = 10.sp)
            Spacer(Modifier.height(9.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ParentField(
                    value = pin,
                    onChange = { if (it.length <= 4 && it.all(Char::isDigit)) pin = it },
                    label = "4 números",
                    modifier = Modifier.width(180.dp),
                    keyboard = KeyboardType.Number
                )
                Spacer(Modifier.width(10.dp))
                FocusSurface(
                    onClick = { if (viewModel.updateParentalPin(pin)) pin = "" },
                    background = Blue500,
                    focusColor = Yellow500,
                    shape = RoundedCornerShape(12.dp),
                    padding = 13.dp
                ) {
                    Text("Atualizar PIN", color = White, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun Panel(content: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(White, RoundedCornerShape(22.dp)).padding(20.dp)
    ) {
        content()
    }
}

@Composable
private fun Header(title: String, subtitle: String, action: String, onAction: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, color = Ink900, fontWeight = FontWeight.Black, fontSize = 19.sp)
            Text(subtitle, color = Ink600, fontSize = 10.sp)
        }
        FocusSurface(
            onClick = onAction,
            background = Blue500,
            focusColor = Yellow500,
            shape = RoundedCornerShape(12.dp),
            padding = 10.dp
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, null, tint = White, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(5.dp))
                Text(action, color = White, fontWeight = FontWeight.Black, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun TinyButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    FocusSurface(
        onClick = onClick,
        modifier = Modifier.size(34.dp),
        background = tint.copy(alpha = .10f),
        focusColor = tint,
        shape = RoundedCornerShape(9.dp)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun TaskDialog(
    task: Task?,
    onDismiss: () -> Unit,
    onSave: (Task?, String, String, Int, String, Int, Int, RoutinePeriod) -> Unit
) {
    var title by remember(task) { mutableStateOf(task?.title.orEmpty()) }
    var desc by remember(task) { mutableStateOf(task?.description.orEmpty()) }
    var minutes by remember(task) { mutableStateOf((task?.durationMinutes ?: 20).toString()) }
    var stars by remember(task) { mutableStateOf((task?.rewardPoints ?: 10).toString()) }
    var icon by remember(task) { mutableStateOf(task?.icon ?: "⭐") }
    var mask by remember(task) { mutableStateOf(task?.recurrenceMask ?: Task.EVERY_DAY) }
    var period by remember(task) { mutableStateOf(task?.routinePeriod ?: RoutinePeriod.ANYTIME) }

    DialogShell {
        Text(if (task == null) "Nova tarefa" else "Editar tarefa", color = Ink900, fontWeight = FontWeight.Black, fontSize = 21.sp)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("⭐","📚","🛁","🧸","📝","🦷","⚽","🍽️").forEach {
                Choice(it, icon == it) { icon = it }
            }
        }
        Spacer(Modifier.height(8.dp))
        ParentField(title, { title = it }, "Nome da tarefa")
        Spacer(Modifier.height(8.dp))
        ParentField(desc, { desc = it }, "Descrição")
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ParentField(
                minutes,
                { if (it.all(Char::isDigit)) minutes = it },
                "Minutos",
                Modifier.weight(1f),
                KeyboardType.Number
            )
            ParentField(
                stars,
                { if (it.all(Char::isDigit)) stars = it },
                "Estrelas",
                Modifier.weight(1f),
                KeyboardType.Number
            )
        }
        Spacer(Modifier.height(8.dp))
        Text("Dias", color = Ink900, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            listOf("S","T","Q","Q","S","S","D").forEachIndexed { index, label ->
                Choice(label, mask and (1 shl index) != 0) {
                    mask = if (mask and (1 shl index) != 0) {
                        mask and (1 shl index).inv()
                    } else {
                        mask or (1 shl index)
                    }
                    if (mask == 0) mask = 1 shl index
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            RoutinePeriod.entries.forEach {
                Choice("${it.icon} ${it.label}", period == it) { period = it }
            }
        }
        Spacer(Modifier.height(12.dp))
        DialogActions(onDismiss) {
            onSave(
                task,
                title,
                desc,
                minutes.toIntOrNull() ?: 20,
                icon,
                stars.toIntOrNull() ?: 10,
                mask,
                period
            )
        }
    }
}

@Composable
private fun RewardDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Int, RewardType, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("🎁") }
    var cost by remember { mutableStateOf("30") }
    var minutes by remember { mutableStateOf("20") }
    var type by remember { mutableStateOf(RewardType.CUSTOM) }

    DialogShell {
        Text("Nova recompensa", color = Ink900, fontWeight = FontWeight.Black, fontSize = 21.sp)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("🎁","📺","🍿","🎮","🍦","🎨").forEach { Choice(it, icon == it) { icon = it } }
        }
        Spacer(Modifier.height(8.dp))
        ParentField(title, { title = it }, "Nome")
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ParentField(cost, { if (it.all(Char::isDigit)) cost = it }, "Custo", Modifier.weight(1f), KeyboardType.Number)
            ParentField(minutes, { if (it.all(Char::isDigit)) minutes = it }, "Minutos", Modifier.weight(1f), KeyboardType.Number)
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            RewardType.entries.forEach { Choice(it.name.replace('_', ' '), type == it) { type = it } }
        }
        Spacer(Modifier.height(12.dp))
        DialogActions(onDismiss) {
            onSave(title, icon, cost.toIntOrNull() ?: 30, type, minutes.toIntOrNull() ?: 20)
        }
    }
}

@Composable
private fun ProfileDialog(
    onDismiss: () -> Unit,
    onSave: (String, Int, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("7") }
    var avatar by remember { mutableStateOf("🧒") }
    var color by remember { mutableStateOf("#3B82F6") }

    DialogShell {
        Text("Adicionar perfil", color = Ink900, fontWeight = FontWeight.Black, fontSize = 21.sp)
        Text("Opções para meninos e meninas, com diferentes tons de pele.", color = Ink600, fontSize = 10.sp)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("👦","👧","🧒","👦🏽","👧🏽","🧒🏾").forEach {
                Choice(it, avatar == it) { avatar = it }
            }
        }
        Spacer(Modifier.height(8.dp))
        ParentField(name, { name = it }, "Nome")
        Spacer(Modifier.height(8.dp))
        ParentField(age, { if (it.all(Char::isDigit)) age = it }, "Idade", keyboard = KeyboardType.Number)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("#3B82F6","#A855F7","#EC4899","#22C55E","#F97316").forEach {
                Choice("●", color == it) { color = it }
            }
        }
        Spacer(Modifier.height(12.dp))
        DialogActions(onDismiss) { onSave(name, age.toIntOrNull() ?: 7, avatar, color) }
    }
}

@Composable
private fun ContentDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var input by remember { mutableStateOf("") }

    DialogShell {
        Text("Adicionar vídeo aprovado", color = Ink900, fontWeight = FontWeight.Black, fontSize = 21.sp)
        Text("Cole URL do YouTube ou ID do vídeo. Apenas o vídeo aprovado será aberto.", color = Ink600, fontSize = 10.sp)
        Spacer(Modifier.height(10.dp))
        ParentField(title, { title = it }, "Título")
        Spacer(Modifier.height(8.dp))
        ParentField(input, { input = it }, "URL ou ID")
        Spacer(Modifier.height(12.dp))
        DialogActions(onDismiss) { onSave(title, input) }
    }
}

@Composable
private fun DialogShell(content: @Composable () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(Color.Black.copy(alpha = .62f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.width(620.dp).background(White, RoundedCornerShape(24.dp)).padding(22.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun DialogActions(onDismiss: () -> Unit, onSave: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FocusSurface(
            onClick = onDismiss,
            modifier = Modifier.weight(1f).height(45.dp),
            background = Cloud100,
            focusColor = Blue500,
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Cancelar", color = Ink900, fontWeight = FontWeight.Bold)
            }
        }
        FocusSurface(
            onClick = onSave,
            modifier = Modifier.weight(1f).height(45.dp),
            background = Blue500,
            focusColor = Yellow500,
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Salvar", color = White, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun Choice(text: String, selected: Boolean, onClick: () -> Unit) {
    FocusSurface(
        onClick = onClick,
        background = if (selected) Color(0xFFE4EEFF) else Cloud100,
        focusColor = Blue500,
        shape = RoundedCornerShape(10.dp),
        padding = 8.dp
    ) {
        Text(
            text,
            color = if (selected) Blue500 else Ink900,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun ParentField(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboard: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Blue500,
            unfocusedBorderColor = Color(0xFFCBD5E1),
            focusedTextColor = Ink900,
            unfocusedTextColor = Ink900,
            focusedLabelColor = Blue500,
            unfocusedLabelColor = Ink600,
            cursorColor = Blue500,
            focusedContainerColor = White,
            unfocusedContainerColor = White
        )
    )
}
