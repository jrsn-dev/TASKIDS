package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Child
import com.example.model.Reward
import com.example.model.Routine
import com.example.model.Task
import com.example.ui.components.MetricCard
import com.example.ui.components.PrimaryTvButton
import com.example.ui.design.TaskIdsColors
import com.example.ui.dialogs.AddTaskDialog
import com.example.ui.navigation.AppScreen
import com.example.viewmodel.MainViewModel

private enum class ParentSection(val label: String, val icon: String) {
    OVERVIEW("Visão Geral", "🏠"),
    TASKS("Tarefas", "✅"),
    ROUTINES("Rotinas", "🗓"),
    REWARDS("Recompensas", "🎁"),
    PROFILES("Perfis", "👨‍👩‍👧‍👦"),
    SETTINGS("Configurações", "⚙")
}

@Composable
fun ParentDashboardScreen(viewModel: MainViewModel) {
    val child by viewModel.currentChild.collectAsState()
    val children by viewModel.children.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val rewards by viewModel.rewards.collectAsState()
    val routines by viewModel.routines.collectAsState()
    val executions by viewModel.executions.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()

    var section by remember { mutableStateOf(ParentSection.OVERVIEW) }
    var showAddTask by remember { mutableStateOf(false) }
    var showAddReward by remember { mutableStateOf(false) }
    var showAddProfile by remember { mutableStateOf(false) }
    var showAddRoutine by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(TaskIdsColors.SoftBg, RoundedCornerShape(32.dp))
    ) {
        Row(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .width(230.dp)
                    .fillMaxHeight()
                    .background(Color.White, RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐", fontSize = 28.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "TASKIDS",
                        color = TaskIdsColors.Ink,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "Área dos Pais",
                    color = TaskIdsColors.Muted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(26.dp))

                ParentSection.entries.forEach { item ->
                    val selected = section == item
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (selected) TaskIdsColors.SoftBlue else Color.Transparent,
                                RoundedCornerShape(15.dp)
                            )
                            .clickable { section = item }
                            .padding(horizontal = 13.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.icon, fontSize = 17.sp)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            item.label,
                            color = if (selected) TaskIdsColors.Blue else TaskIdsColors.Ink,
                            fontSize = 14.sp,
                            fontWeight = if (selected) FontWeight.Black else FontWeight.Medium
                        )
                    }
                    Spacer(Modifier.height(5.dp))
                }

                Spacer(Modifier.weight(1f))

                PrimaryTvButton(
                    text = "← Voltar ao app",
                    background = TaskIdsColors.Ink,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.navigateTo(AppScreen.Home) }
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(24.dp)
            ) {
                ParentHeader(
                    child = child,
                    onReports = { viewModel.navigateTo(AppScreen.Reports) }
                )

                Spacer(Modifier.height(18.dp))

                when (section) {
                    ParentSection.OVERVIEW -> OverviewSection(
                        child = child,
                        tasks = tasks,
                        rewards = rewards,
                        routines = routines,
                        completedCount = executions.size,
                        onReports = { viewModel.navigateTo(AppScreen.Reports) }
                    )
                    ParentSection.TASKS -> TasksSection(
                        tasks = tasks,
                        onAdd = { showAddTask = true },
                        onDelete = { viewModel.deleteTask(it) },
                        onToggle = {
                            viewModel.editTask(it.copy(isActive = !it.isActive))
                        }
                    )
                    ParentSection.ROUTINES -> RoutinesSection(
                        routines = routines,
                        tasks = tasks,
                        onAdd = { showAddRoutine = true }
                    )
                    ParentSection.REWARDS -> RewardsParentSection(
                        rewards = rewards,
                        onAdd = { showAddReward = true }
                    )
                    ParentSection.PROFILES -> ProfilesSection(
                        children = children,
                        currentId = child?.id,
                        onSelect = { viewModel.selectChild(it.id) },
                        onAdd = { showAddProfile = true }
                    )
                    ParentSection.SETTINGS -> SettingsSection(
                        soundEnabled = soundEnabled,
                        onToggleSound = { viewModel.setSoundEnabled(!soundEnabled) },
                        onUpdatePin = viewModel::updateParentalPin,
                        onReset = viewModel::restartAllTasks
                    )
                }
            }
        }
    }

    if (showAddTask) {
        AddTaskDialog(
            onDismiss = { showAddTask = false },
            onConfirm = { title, duration, icon, description, stars, time, days, recurring ->
                viewModel.addTask(title, duration, icon, description, stars, time, days, recurring)
                showAddTask = false
            }
        )
    }

    if (showAddReward) {
        AddRewardDialog(
            onDismiss = { showAddReward = false },
            onConfirm = { title, stars, icon, description ->
                viewModel.addReward(title, stars, icon, description)
                showAddReward = false
            }
        )
    }

    if (showAddProfile) {
        AddProfileDialog(
            onDismiss = { showAddProfile = false },
            onConfirm = { name, avatar ->
                viewModel.addChild(name, avatar)
                showAddProfile = false
            }
        )
    }

    if (showAddRoutine) {
        AddRoutineDialog(
            onDismiss = { showAddRoutine = false },
            onConfirm = { title, icon, time, days ->
                viewModel.addRoutine(title, icon, time, days)
                showAddRoutine = false
            }
        )
    }
}

@Composable
private fun ParentHeader(child: Child?, onReports: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Área dos Pais",
                color = TaskIdsColors.Ink,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                "Gerencie rotina, recompensas e acompanhe a evolução com foco em autonomia.",
                color = TaskIdsColors.Muted,
                fontSize = 13.sp
            )
        }

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(20.dp))
                .clickable(onClick = onReports)
                .padding(horizontal = 16.dp, vertical = 11.dp)
        ) {
            Text("📊 Relatórios", color = TaskIdsColors.Blue, fontWeight = FontWeight.Black)
        }

        Spacer(Modifier.width(10.dp))

        Row(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(child?.avatarEmoji ?: "🧒", fontSize = 24.sp)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    child?.name ?: "Perfil",
                    color = TaskIdsColors.Ink,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "⭐ ${child?.totalStars ?: 0}",
                    color = TaskIdsColors.Muted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun OverviewSection(
    child: Child?,
    tasks: List<Task>,
    rewards: List<Reward>,
    routines: List<Routine>,
    completedCount: Int,
    onReports: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
            MetricCard("⭐", "Saldo de estrelas", (child?.totalStars ?: 0).toString(), Modifier.weight(1f), TaskIdsColors.Yellow)
            MetricCard("✅", "Tarefas cadastradas", tasks.size.toString(), Modifier.weight(1f), TaskIdsColors.Green)
            MetricCard("🔥", "Sequência", "${child?.currentStreak ?: 0} dias", Modifier.weight(1f), TaskIdsColors.Orange)
            MetricCard("🎯", "Conclusões históricas", completedCount.toString(), Modifier.weight(1f), TaskIdsColors.Purple)
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Text("Próximas missões", color = TaskIdsColors.Ink, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(tasks.take(6), key = { it.id }) { task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(TaskIdsColors.SoftBg, RoundedCornerShape(15.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(task.icon, fontSize = 22.sp)
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(task.title, color = TaskIdsColors.Ink, fontWeight = FontWeight.Bold)
                                Text(
                                    task.scheduledTime ?: "Sem horário fixo",
                                    color = TaskIdsColors.Muted,
                                    fontSize = 11.sp
                                )
                            }
                            Text("⭐ +${task.rewardStars}", color = TaskIdsColors.Ink, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight()
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Text("Rotinas e recompensas", color = TaskIdsColors.Ink, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))
                Text("🗓 ${routines.size} rotinas configuradas", color = TaskIdsColors.Muted)
                Spacer(Modifier.height(8.dp))
                Text("🎁 ${rewards.size} recompensas disponíveis", color = TaskIdsColors.Muted)
                Spacer(Modifier.height(18.dp))
                PrimaryTvButton(
                    text = "Abrir relatório completo",
                    background = TaskIdsColors.Blue,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onReports
                )
            }
        }
    }
}

@Composable
private fun TasksSection(
    tasks: List<Task>,
    onAdd: () -> Unit,
    onDelete: (Task) -> Unit,
    onToggle: (Task) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Gerenciar atividades", color = TaskIdsColors.Ink, fontSize = 21.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.weight(1f))
            PrimaryTvButton("＋ Adicionar tarefa", TaskIdsColors.Blue, onClick = onAdd)
        }
        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, RoundedCornerShape(24.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TaskIdsColors.SoftBg, RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(task.icon, fontSize = 25.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(task.title, color = TaskIdsColors.Ink, fontWeight = FontWeight.Black)
                        Text(
                            "${task.durationMinutes} min • ⭐ +${task.rewardStars} • ${task.scheduledTime ?: "sem horário"}",
                            color = TaskIdsColors.Muted,
                            fontSize = 11.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                if (task.isActive) TaskIdsColors.Green else Color(0xFFCAD3E1),
                                RoundedCornerShape(18.dp)
                            )
                            .clickable { onToggle(task) }
                            .padding(horizontal = 13.dp, vertical = 7.dp)
                    ) {
                        Text(
                            if (task.isActive) "ATIVA" else "PAUSADA",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFE9EC), CircleShape)
                            .clickable { onDelete(task) }
                            .padding(9.dp)
                    ) {
                        Text("🗑", fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutinesSection(
    routines: List<Routine>,
    tasks: List<Task>,
    onAdd: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Rotinas", color = TaskIdsColors.Ink, fontSize = 21.sp, fontWeight = FontWeight.Black)
                Text(
                    "Organize manhã, tarde e noite sem duplicar tarefas.",
                    color = TaskIdsColors.Muted,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.weight(1f))
            PrimaryTvButton("＋ Nova rotina", TaskIdsColors.Purple, onClick = onAdd)
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (routines.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🗓", fontSize = 42.sp)
                        Text("Nenhuma rotina criada ainda.", color = TaskIdsColors.Muted)
                    }
                }
            } else {
                routines.take(3).forEach { routine ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color.White, RoundedCornerShape(24.dp))
                            .padding(18.dp)
                    ) {
                        Text(routine.icon, fontSize = 34.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(routine.title, color = TaskIdsColors.Ink, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text(routine.startTime ?: "Horário flexível", color = TaskIdsColors.Muted, fontSize = 12.sp)
                        Spacer(Modifier.height(14.dp))
                        Text("Dias: ${routine.daysCsv}", color = TaskIdsColors.Muted, fontSize = 11.sp)
                        Spacer(Modifier.height(14.dp))
                        Text(
                            "Tarefas podem ser associadas a esta rotina na próxima etapa de edição detalhada.",
                            color = TaskIdsColors.Muted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardsParentSection(rewards: List<Reward>, onAdd: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Catálogo de recompensas", color = TaskIdsColors.Ink, fontSize = 21.sp, fontWeight = FontWeight.Black)
                Text("Combine tempo de tela com experiências fora da tela.", color = TaskIdsColors.Muted, fontSize = 12.sp)
            }
            Spacer(Modifier.weight(1f))
            PrimaryTvButton("＋ Nova recompensa", TaskIdsColors.Orange, onClick = onAdd)
        }
        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, RoundedCornerShape(24.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(rewards, key = { it.id }) { reward ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TaskIdsColors.SoftBg, RoundedCornerShape(16.dp))
                        .padding(13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(reward.icon, fontSize = 27.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(reward.title, color = TaskIdsColors.Ink, fontWeight = FontWeight.Black)
                        Text(reward.description, color = TaskIdsColors.Muted, fontSize = 11.sp)
                    }
                    Text("⭐ ${reward.costStars}", color = TaskIdsColors.Ink, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun ProfilesSection(
    children: List<Child>,
    currentId: Long?,
    onSelect: (Child) -> Unit,
    onAdd: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Perfis das crianças", color = TaskIdsColors.Ink, fontSize = 21.sp, fontWeight = FontWeight.Black)
                Text(
                    "Cada criança tem tarefas, estrelas, rotina e histórico independentes.",
                    color = TaskIdsColors.Muted,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.weight(1f))
            PrimaryTvButton("＋ Adicionar perfil", TaskIdsColors.Green, onClick = onAdd)
        }

        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            children.forEach { profile ->
                val selected = profile.id == currentId
                Column(
                    modifier = Modifier
                        .width(220.dp)
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .clickable { onSelect(profile) }
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(
                                if (selected) TaskIdsColors.SoftBlue else TaskIdsColors.SoftBg,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(profile.avatarEmoji, fontSize = 42.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(profile.name, color = TaskIdsColors.Ink, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Text("⭐ ${profile.totalStars}", color = TaskIdsColors.Muted, fontSize = 12.sp)
                    if (selected) {
                        Spacer(Modifier.height(8.dp))
                        Text("Perfil ativo", color = TaskIdsColors.Blue, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    soundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onUpdatePin: (String) -> Boolean,
    onReset: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var pinMessage by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Text("🔊 Som", fontSize = 22.sp)
                Spacer(Modifier.height(8.dp))
                Text("Alertas e confirmações", color = TaskIdsColors.Ink, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(12.dp))
                PrimaryTvButton(
                    text = if (soundEnabled) "Ativado" else "Desativado",
                    background = if (soundEnabled) TaskIdsColors.Green else TaskIdsColors.Muted,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onToggleSound
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Text("🔒 PIN parental", fontSize = 22.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it.filter(Char::isDigit).take(4) },
                    label = { Text("Novo PIN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                PrimaryTvButton(
                    text = "Atualizar PIN",
                    background = TaskIdsColors.Blue,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        pinMessage = if (onUpdatePin(pin)) {
                            pin = ""
                            "PIN atualizado com segurança."
                        } else "Use exatamente 4 números."
                    }
                )
                if (pinMessage.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(pinMessage, color = TaskIdsColors.Muted, fontSize = 11.sp)
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Text("Reiniciar tarefas do perfil", color = TaskIdsColors.Ink, fontWeight = FontWeight.Black)
            Text(
                "Reinicia apenas o estado diário. Histórico e estrelas não são apagados.",
                color = TaskIdsColors.Muted,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(12.dp))
            PrimaryTvButton(
                text = "↻ Reiniciar tarefas",
                background = TaskIdsColors.Orange,
                onClick = onReset
            )
        }
    }
}

@Composable
private fun AddRewardDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var stars by remember { mutableStateOf("50") }
    var icon by remember { mutableStateOf("🎁") }
    var description by remember { mutableStateOf("") }

    SimpleFormDialog("Nova recompensa", onDismiss) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("🎁","🍿","🎮","🍦","⚽","🎨","📚").forEach {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(if (icon == it) TaskIdsColors.SoftBlue else TaskIdsColors.SoftBg, RoundedCornerShape(10.dp))
                        .clickable { icon = it },
                    contentAlignment = Alignment.Center
                ) { Text(it) }
            }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(title, { title = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            stars,
            { stars = it.filter(Char::isDigit).take(4) },
            label = { Text("Custo em estrelas") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(description, { description = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(14.dp))
        PrimaryTvButton(
            "Salvar recompensa",
            TaskIdsColors.Orange,
            Modifier.fillMaxWidth()
        ) {
            if (title.isNotBlank()) onConfirm(title, stars.toIntOrNull() ?: 50, icon, description)
        }
    }
}

@Composable
private fun AddProfileDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var avatar by remember { mutableStateOf("🧒") }

    SimpleFormDialog("Novo perfil infantil", onDismiss) {
        Text("Escolha um avatar", color = TaskIdsColors.Muted, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("🧒","👧","👦","🧑","👩‍🦱","👨‍🦱","🦸","🦸‍♀️").forEach {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(if (avatar == it) TaskIdsColors.SoftBlue else TaskIdsColors.SoftBg, RoundedCornerShape(11.dp))
                        .clickable { avatar = it },
                    contentAlignment = Alignment.Center
                ) { Text(it, fontSize = 21.sp) }
            }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(name, { name = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(14.dp))
        PrimaryTvButton("Criar perfil", TaskIdsColors.Green, Modifier.fillMaxWidth()) {
            if (name.isNotBlank()) onConfirm(name, avatar)
        }
    }
}

@Composable
private fun AddRoutineDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String?, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("🌞") }
    var time by remember { mutableStateOf("") }
    var days by remember { mutableStateOf(setOf(1,2,3,4,5)) }

    SimpleFormDialog("Nova rotina", onDismiss) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("🌞","🌤","🌙","🎒","🏠").forEach {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(if (icon == it) TaskIdsColors.SoftBlue else TaskIdsColors.SoftBg, RoundedCornerShape(10.dp))
                        .clickable { icon = it },
                    contentAlignment = Alignment.Center
                ) { Text(it) }
            }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(title, { title = it }, label = { Text("Nome da rotina") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(time, { time = it.take(5) }, label = { Text("Horário inicial (HH:mm)") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            listOf("S","T","Q","Q","S","S","D").forEachIndexed { index, label ->
                val day = index + 1
                val selected = day in days
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (selected) TaskIdsColors.Blue else TaskIdsColors.SoftBg, CircleShape)
                        .clickable { days = if (selected) days - day else days + day },
                    contentAlignment = Alignment.Center
                ) {
                    Text(label, color = if (selected) Color.White else TaskIdsColors.Muted, fontWeight = FontWeight.Black)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        PrimaryTvButton("Salvar rotina", TaskIdsColors.Purple, Modifier.fillMaxWidth()) {
            if (title.isNotBlank()) onConfirm(title, icon, time.ifBlank { null }, days.sorted().joinToString(","))
        }
    }
}

@Composable
private fun SimpleFormDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(470.dp)
                .background(Color.White, RoundedCornerShape(26.dp))
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, color = TaskIdsColors.Ink, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .background(TaskIdsColors.SoftBg, CircleShape)
                        .clickable(onClick = onDismiss)
                        .padding(8.dp)
                ) {
                    Text("✕", color = TaskIdsColors.Muted)
                }
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}
