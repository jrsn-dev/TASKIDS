package com.example

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.material.icons.filled.Check
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.Task
import com.example.model.TaskStatus
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TaskBlue
import com.example.ui.theme.TaskGray
import com.example.ui.theme.TaskGreen
import com.example.ui.theme.TaskRed
import com.example.ui.theme.TaskYellow
import com.example.ui.theme.TvBackground
import com.example.ui.theme.TvPrimary
import com.example.ui.theme.TvSecondary
import com.example.ui.theme.TvSurface
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.MainViewModelFactory
import com.example.viewmodel.Screen
import com.example.viewmodel.TimerState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val viewModel: MainViewModel by viewModels {
            MainViewModelFactory(applicationContext)
        }

        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("main_scaffold"),
                    containerColor = Color.Transparent
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0D47A1), // Royal Blue
                                        Color(0xFF1565C0), // Dark Sky Blue
                                        Color(0xFF041029)  // Deep Midnight Space
                                    )
                                )
                            )
                            .padding(innerPadding)
                    ) {
                        AppNavigationFlow(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigationFlow(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    
    // Back press handler: child safety check
    BackHandler(enabled = true) {
        when (currentScreen) {
            is Screen.TaskList -> {
                // Let system exit or do nothing inside primary home on TV
            }
            is Screen.Timer -> {
                viewModel.navigateTo(Screen.TaskList)
            }
            is Screen.YouTubeReward -> {
                viewModel.navigateTo(Screen.TaskList)
            }
            is Screen.YouTubePlayer -> {
                // Parental back protection requires navigating home directly to abort
                viewModel.navigateTo(Screen.TaskList)
            }
            is Screen.Settings -> {
                viewModel.navigateTo(Screen.TaskList)
            }
        }
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        when (val screen = currentScreen) {
            is Screen.TaskList -> TaskListScreen(viewModel)
            is Screen.Timer -> TimerScreen(viewModel, screen.taskId)
            is Screen.YouTubeReward -> YouTubeRewardScreen(viewModel)
            is Screen.YouTubePlayer -> YouTubePlayerScreen(viewModel)
            is Screen.Settings -> SettingsScreen(viewModel)
        }
    }
}

// Focus state handler helper
@Composable
fun Modifier.tvFocusModifier(
    isFocused: Boolean,
    focusedOutlineColor: Color = TvPrimary,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp)
): Modifier {
    val scale by animateFloatAsState(targetValue = if (isFocused) 1.05f else 1.0f, label = "ScaleAnimation")
    val borderStroke = if (isFocused) 3.5.dp else 1.dp
    val borderColor = if (isFocused) focusedOutlineColor else Color.White.copy(alpha = 0.18f)
    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            clip = false
        }
        .border(
            width = borderStroke,
            color = borderColor,
            shape = shape
        )
}

// --------------------------------------------------------
// SCREEN 1: TASK LIST SCREEN
// --------------------------------------------------------
fun getTaskStars(task: Task): Int {
    return when (task.id) {
        1 -> 10
        3 -> 15
        2 -> 10
        4 -> 10
        else -> 10
    }
}

fun getTaskCardColor(task: Task): Color {
    return when (task.id) {
        1 -> Color(0xFF2EBF52) // Ler livro: Green
        3 -> Color(0xFFFFB300) // Almoço: Yellow-Orange
        2 -> Color(0xFF2196F3) // Tomar Banho: Sky Blue
        4 -> Color(0xFF9E7BFF) // Fazer Lição: Lavender Purple
        else -> {
            val list = listOf(Color(0xFF2EBF52), Color(0xFFFFB300), Color(0xFF2196F3), Color(0xFF9E7BFF), Color(0xFFFF4081))
            list[java.lang.Math.abs(task.id) % list.size]
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskListScreen(viewModel: MainViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    var showParentPinDialog by remember { mutableStateOf(false) }

    val completedCount = tasks.count { it.status == TaskStatus.COMPLETED }
    val totalStars = 100 + tasks.filter { it.status == TaskStatus.COMPLETED }.sumOf { getTaskStars(it) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Kids Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Playful star trophy logo
                Image(
                    painter = painterResource(id = R.drawable.kids_task_logo_1780663813391),
                    contentDescription = "Kids Task Manager Logo",
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    // Multi-colored cartoon bubbly heading
                    val kidsTitle = buildAnnotatedString {
                        pushStyle(SpanStyle(color = Color(0xFFFF4D80), fontWeight = FontWeight.Black))
                        append("K")
                        pop()
                        pushStyle(SpanStyle(color = Color(0xFF3AC7FF), fontWeight = FontWeight.Black))
                        append("i")
                        pop()
                        pushStyle(SpanStyle(color = Color(0xFFFFD166), fontWeight = FontWeight.Black))
                        append("d")
                        pop()
                        pushStyle(SpanStyle(color = Color(0xFF2EC4B6), fontWeight = FontWeight.Black))
                        append("s")
                        pop()
                        pushStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.Black))
                        append(" Task Manager")
                        pop()
                    }
                    Text(
                        text = kidsTitle,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Complete as tarefas do dia, ganhe estrelas e divirta-se!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.82f)
                    )
                }
            }
            
            // Sub-rows on the right: Star badge, Alex Avatar and configurations button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Gold Star count badge
                Row(
                    modifier = Modifier
                        .background(Color(0xFF6236FF).copy(alpha = 0.85f), CircleShape)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⭐", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$totalStars",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                
                // Active Avatar Badge
                Row(
                    modifier = Modifier
                        .background(Color(0xFF2D62ED).copy(alpha = 0.85f), CircleShape)
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF81C784), CircleShape)
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👦", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Alex",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }

                // Parental Configurations Button
                var isSettingsFocused by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .onFocusChanged { isSettingsFocused = it.isFocused }
                        .tvFocusModifier(isSettingsFocused, TvSecondary, RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .clickable { showParentPinDialog = true }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("settings_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Área dos Pais",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Área dos Pais",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Large Horizontal list of task cards for Android TV
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhuma tarefa cadastrada. Vá para 'Área dos Pais' para adicionar!",
                    color = TaskGray,
                    fontSize = 18.sp
                )
            }
        } else {
            LazyRow(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .focusGroup()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(tasks) { task ->
                    TaskCard(task = task, onSelect = { viewModel.selectTask(task) })
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Progress bar container styled exactly as in the mock image
        if (tasks.isNotEmpty()) {
            val progress = completedCount.toFloat() / tasks.size.toFloat()
            val progressPercent = (progress * 100).toInt()
            
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Gold star completed tasks count badge on the left
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFFFA000), CircleShape)
                            .border(2.dp, Color(0xFFFFD54F), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$completedCount",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Column {
                        Text(
                            text = "Muito bem, Alex!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1C1B1F)
                        )
                        Text(
                            text = "Você completou $completedCount de ${tasks.size} tarefas",
                            fontSize = 13.sp,
                            color = Color(0xFF5F6368),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Centered neon green glowing progress bar
                Box(
                    modifier = Modifier
                        .weight(1.6f)
                        .height(20.dp)
                        .background(Color(0xFFE2E8F0), CircleShape)
                        .clip(CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF2EBF52),
                                        Color(0xFF66BB6A)
                                    )
                                )
                            )
                    )
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                // Animated percentage stats on the right
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.weight(0.8f)
                ) {
                    Text(
                        text = "$progressPercent%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF2196F3)
                    )
                    Text(
                        text = "Tarefas Concluídas",
                        fontSize = 11.sp,
                        color = Color(0xFF7F8C8D),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // PIN unlock dialog overlay
    if (showParentPinDialog) {
        ParentPinDialog(
            viewModel = viewModel,
            onDismiss = { showParentPinDialog = false },
            onVerified = {
                showParentPinDialog = false
                viewModel.navigateTo(Screen.Settings)
            }
        )
    }
}

@Composable
fun TaskCard(
    task: Task,
    onSelect: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    
    val cardColor = getTaskCardColor(task)

    Card(
        modifier = Modifier
            .width(240.dp)
            .height(280.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .tvFocusModifier(isFocused, focusedOutlineColor = Color.White, shape = RoundedCornerShape(32.dp))
            .clickable { onSelect() }
            .testTag("task_card_${task.id}"),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Task status circle button on the top right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (task.status == TaskStatus.COMPLETED) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Concluída",
                            tint = Color(0xFF2EBF52),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .border(2.dp, Color.White.copy(alpha = 0.45f), CircleShape)
                    )
                }
            }

            // Task emoji bubble matching the 3D items in the illustration
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(Color.White.copy(alpha = 0.22f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = task.icon,
                    fontSize = 58.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Bold Task Name
            Text(
                text = task.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                maxLines = 1,
                textAlign = TextAlign.Center
            )

            // Star rewarding badge at the bottom center of the card
            Row(
                modifier = Modifier
                    .background(Color(0x2B000000), CircleShape)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⭐", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${getTaskStars(task)} Estrelas",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

// --------------------------------------------------------
// SCREEN 2: ACTIVE COUNTDOWN TIMER SCREEN
// --------------------------------------------------------
@Composable
fun TimerScreen(viewModel: MainViewModel, taskId: Int) {
    val tasks by viewModel.tasks.collectAsState()
    val timerState by viewModel.timerState.collectAsState()
    val activeTask = tasks.find { it.id == taskId }

    if (activeTask == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Tarefa não encontrada!", color = Color.White)
        }
        return
    }

    val minutes = timerState.remainingSeconds / 60
    val seconds = timerState.remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val progressColor = when {
        timerState.remainingSeconds > timerState.totalSeconds / 2 -> TaskGreen
        timerState.remainingSeconds > timerState.totalSeconds / 4 -> TaskYellow
        else -> TaskRed
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Back Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var isBackFocused by remember { mutableStateOf(false) }
            IconButton(
                onClick = { viewModel.navigateTo(Screen.TaskList) },
                modifier = Modifier
                    .onFocusChanged { isBackFocused = it.isFocused }
                    .tvFocusModifier(isBackFocused, shape = RoundedCornerShape(50.dp))
                    .background(TvSurface, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${activeTask.icon} ${activeTask.title}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Gigantic Counter Screen
        Text(
            text = timeFormatted,
            fontSize = 110.sp,
            fontWeight = FontWeight.Black,
            color = progressColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sweeping Progress bar
        LinearProgressIndicator(
            progress = { timerState.progress },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(16.dp)
                .clip(CircleShape),
            color = progressColor,
            trackColor = TvSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (activeTask.description.isNotEmpty()) {
            Text(
                text = activeTask.description,
                fontSize = 16.sp,
                color = TaskGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 48.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Control Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pause / Resume
            var isPauseFocused by remember { mutableStateOf(false) }
            Button(
                onClick = {
                    if (timerState.isRunning) viewModel.pauseTimer() else viewModel.resumeTimer()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (timerState.isRunning) TvSurface else TaskBlue
                ),
                modifier = Modifier
                    .onFocusChanged { isPauseFocused = it.isFocused }
                    .tvFocusModifier(isPauseFocused)
                    .width(180.dp)
                    .height(56.dp)
                    .testTag("pause_resume_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (timerState.isRunning) "⏸ PAUSAR" else "▶ RETOMAR",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Extend +5 MIN
            var isExtendFocused by remember { mutableStateOf(false) }
            Button(
                onClick = { viewModel.extendTimer() },
                colors = ButtonDefaults.buttonColors(containerColor = TvSurface),
                modifier = Modifier
                    .onFocusChanged { isExtendFocused = it.isFocused }
                    .tvFocusModifier(isExtendFocused)
                    .width(180.dp)
                    .height(56.dp)
                    .testTag("extend_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "➕ +5 MIN",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
            }

            // Completed Button
            var isCompleteFocused by remember { mutableStateOf(false) }
            Button(
                onClick = { viewModel.markTaskAsCompleted(activeTask) },
                colors = ButtonDefaults.buttonColors(containerColor = TaskGreen),
                modifier = Modifier
                    .onFocusChanged { isCompleteFocused = it.isFocused }
                    .tvFocusModifier(isCompleteFocused, shape = RoundedCornerShape(12.dp))
                    .width(220.dp)
                    .height(56.dp)
                    .testTag("complete_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "✓ CONCLUÍDO!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// --------------------------------------------------------
// SCREEN 3: YOUTUBE REWARD UNLOCKED SCREEN
// --------------------------------------------------------
@Composable
fun YouTubeRewardScreen(viewModel: MainViewModel) {
    val durationLimit by viewModel.youtubeConfigLimit.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Star rewards
        Text(
            text = "🎉🎈🎊",
            fontSize = 72.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "PARABÉNS! VOCÊ FEZ TUDO!",
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            color = TvSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Como recompensa, você ganhou $durationLimit minutos no YouTube!",
            fontSize = 18.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Watch YouTube
            var isWatchFocused by remember { mutableStateOf(false) }
            Button(
                onClick = { viewModel.navigateTo(Screen.YouTubePlayer) },
                colors = ButtonDefaults.buttonColors(containerColor = TaskRed),
                modifier = Modifier
                    .onFocusChanged { isWatchFocused = it.isFocused }
                    .tvFocusModifier(isWatchFocused)
                    .width(260.dp)
                    .height(64.dp)
                    .testTag("watch_youtube_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ASSISTIR YOUTUBE",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Restart Task List
            var isRestartFocused by remember { mutableStateOf(false) }
            Button(
                onClick = { viewModel.restartAllTasks() },
                colors = ButtonDefaults.buttonColors(containerColor = TvSurface),
                modifier = Modifier
                    .onFocusChanged { isRestartFocused = it.isFocused }
                    .tvFocusModifier(isRestartFocused)
                    .width(260.dp)
                    .height(64.dp)
                    .testTag("restart_tasks_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "RECOMEÇAR TAREFAS",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray
                )
            }
        }
    }
}

// --------------------------------------------------------
// SCREEN 4: EMBEDDED YOUTUBE REWARD PLAYER SCREEN
// --------------------------------------------------------
@Composable
fun YouTubePlayerScreen(viewModel: MainViewModel) {
    val remainingSecs by viewModel.youtubeRemainingSeconds.collectAsState()
    val totalSecs by viewModel.youtubeTotalSeconds.collectAsState()

    val minutes = remainingSecs / 60
    val seconds = remainingSecs % 60
    val formattedCounter = String.format("%02d:%02d", minutes, seconds)

    // Child escape prevention pin state
    var showParentExitVerify by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Real active YouTube Web View loading kids safe educational animation
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = false
                    webViewClient = WebViewClient()
                    // Open YouTube in TV format
                    loadUrl("https://www.youtube.com")
                }
            },
            update = { /* Updates if required */ }
        )

        // Floating Parent control HUD overlay
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.82f), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(TaskRed)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "YouTube Kids Timer: ",
                color = Color.LightGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formattedCounter,
                color = if (remainingSecs < 60) TaskRed else TaskYellow,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.width(16.dp))
            
            // Abort/Lock return button
            var isLockFocused by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .onFocusChanged { isLockFocused = it.isFocused }
                    .tvFocusModifier(isLockFocused, shape = RoundedCornerShape(4.dp))
                    .background(TaskRed.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .clickable { showParentExitVerify = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "FECHAR",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }

    if (showParentExitVerify) {
        ParentPinDialog(
            viewModel = viewModel,
            title = "Apenas Pais: Deseja fechar e voltar?",
            onDismiss = { showParentExitVerify = false },
            onVerified = {
                showParentExitVerify = false
                viewModel.navigateTo(Screen.TaskList)
            }
        )
    }
}

// --------------------------------------------------------
// SCREEN 5: PARENT CONFIGURATION SETTINGS SCREEN
// --------------------------------------------------------
@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val limitCfg by viewModel.youtubeConfigLimit.collectAsState()
    val soundOn by viewModel.soundEnabled.collectAsState()
    val savedPin by viewModel.parentalPin.collectAsState()

    var showAddTaskForm by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Left Column: Main variables
        Column(
            modifier = Modifier
                .width(420.dp)
                .fillMaxHeight()
                .background(TvSurface, RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.TaskList) },
                        modifier = Modifier.background(TvBackground, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "⚙ Área dos Pais",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Limit Selector
                Text(text = "Recompensa de YouTube", fontSize = 16.sp, color = TaskGray, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val limits = listOf(15, 30, 45, 60)
                    limits.forEach { minutes ->
                        var isLimFocused by remember { mutableStateOf(false) }
                        val isSelected = limitCfg == minutes
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged { isLimFocused = it.isFocused }
                                .tvFocusModifier(isLimFocused, shape = RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) TvPrimary else Color(0x2B000000),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.setYouTubeTimeLimit(minutes) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${minutes}m",
                                color = if (isSelected) Color.White else TaskGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Toggle sound
                Text(text = "Sons Alerta", fontSize = 16.sp, color = TaskGray, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                var isSoundFocused by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isSoundFocused = it.isFocused }
                        .tvFocusModifier(isSoundFocused, shape = RoundedCornerShape(8.dp))
                        .background(Color(0x2B000000), RoundedCornerShape(8.dp))
                        .clickable { viewModel.setSoundEnabled(!soundOn) }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Efeitos Sonoros", color = Color.White, fontSize = 16.sp)
                        Text(
                            text = if (soundOn) "ATIVADO 🔊" else "DESATIVADO 🔇",
                            color = if (soundOn) TaskGreen else TaskRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Update PIN Form
                Text(text = "Senha PIN Parental", fontSize = 16.sp, color = TaskGray, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                var isPinFocused by remember { mutableStateOf(false) }
                var editablePin by remember { mutableStateOf(savedPin) }
                OutlinedTextField(
                    value = editablePin,
                    onValueChange = {
                        if (it.length <= 4) {
                            editablePin = it
                            if (it.length == 4) {
                                viewModel.updateParentalPin(it)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { isPinFocused = it.isFocused }
                        .tvFocusModifier(isPinFocused, shape = RoundedCornerShape(8.dp)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TvSecondary,
                        unfocusedBorderColor = Color(0x2B000000),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0x2B000000),
                        unfocusedContainerColor = Color(0x2B000000)
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text(text = "Ex: 1234", color = TaskGray) }
                )
            }

            // Quick reset database seeds
            var isResetFocused by remember { mutableStateOf(false) }
            Button(
                onClick = { viewModel.restartAllTasks() },
                colors = ButtonDefaults.buttonColors(containerColor = TaskYellow.copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isResetFocused = it.isFocused }
                    .tvFocusModifier(isResetFocused, focusedOutlineColor = TaskYellow, shape = RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "🔄 REINICIAR TAREFAS DOS FILHOS", color = TaskYellow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        // Right Column: Manage Task items list which supports adding + deleting
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(TvSurface, RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gerenciar Atividades",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                var isAddFocused by remember { mutableStateOf(false) }
                IconButton(
                    onClick = { showAddTaskForm = true },
                    modifier = Modifier
                        .onFocusChanged { isAddFocused = it.isFocused }
                        .tvFocusModifier(isAddFocused, shape = RoundedCornerShape(50.dp))
                        .background(TvPrimary, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Task list manage row items
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tasks) { task ->
                    var isItemFocused by remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isItemFocused = it.isFocused }
                            .tvFocusModifier(isItemFocused, shape = RoundedCornerShape(8.dp))
                            .background(Color(0x2B000000), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = task.icon, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = task.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(text = "${task.durationMinutes} minutos", color = TaskGray, fontSize = 13.sp)
                            }
                        }

                        var isDeleteFocused by remember { mutableStateOf(false) }
                        IconButton(
                            onClick = { viewModel.deleteTask(task) },
                            modifier = Modifier
                                .onFocusChanged { isDeleteFocused = it.isFocused }
                                .tvFocusModifier(isDeleteFocused, shape = RoundedCornerShape(50.dp))
                                .background(TaskRed.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Deletar",
                                tint = TaskRed
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal dialog trigger for adding a custom task
    if (showAddTaskForm) {
        AddTaskDialog(
            onDismiss = { showAddTaskForm = false },
            onConfirm = { title, duration, icon, desc ->
                viewModel.addTask(title, duration, icon, desc)
                showAddTaskForm = false
            }
        )
    }
}

// Dialog: Add Custom Chores Form
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, duration: Int, icon: String, description: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("📋") }
    var description by remember { mutableStateOf("") }

    // Overlay Box
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(460.dp)
                .background(TvSurface, RoundedCornerShape(16.dp))
                .border(2.dp, TvPrimary, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🆕 Nova Atividade",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Icon field selectors (Emjoi)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Ícone Emoji:", color = TaskGray, fontSize = 14.sp)
                val iconsOptions = listOf("📋", "📚", "🧼", "🍽️", "📝", "🧸", "🦷", "⚽")
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    iconsOptions.forEach { opt ->
                        var isOptFocused by remember { mutableStateOf(false) }
                        val isSelected = opt == icon
                        Box(
                            modifier = Modifier
                                .onFocusChanged { isOptFocused = it.isFocused }
                                .tvFocusModifier(isOptFocused, shape = RoundedCornerShape(4.dp))
                                .background(if (isSelected) TvPrimary else Color(0x2B000000), RoundedCornerShape(4.dp))
                                .clickable { icon = opt }
                                .padding(6.dp)
                        ) {
                            Text(text = opt, fontSize = 18.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title field
            var isTitleFocused by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(text = "Nome da tarefa", color = TaskGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isTitleFocused = it.isFocused }
                    .tvFocusModifier(isTitleFocused, shape = RoundedCornerShape(8.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TvPrimary,
                    unfocusedBorderColor = Color(0x2B000000),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0x2B000000),
                    unfocusedContainerColor = Color(0x2B000000)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Duration in minutes
            var isDurFocused by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = durationText,
                onValueChange = { durationText = it },
                label = { Text(text = "Duração em Minutos", color = TaskGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isDurFocused = it.isFocused }
                    .tvFocusModifier(isDurFocused, shape = RoundedCornerShape(8.dp)),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TvPrimary,
                    unfocusedBorderColor = Color(0x2B000000),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0x2B000000),
                    unfocusedContainerColor = Color(0x2B000000)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            var isDescFocused by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(text = "Descrição (Opcional)", color = TaskGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isDescFocused = it.isFocused }
                    .tvFocusModifier(isDescFocused, shape = RoundedCornerShape(8.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TvPrimary,
                    unfocusedBorderColor = Color(0x2B000000),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0x2B000000),
                    unfocusedContainerColor = Color(0x2B000000)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var isBackFocused by remember { mutableStateOf(false) }
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x2B000000)),
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { isBackFocused = it.isFocused }
                        .tvFocusModifier(isBackFocused),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Cancelar", color = Color.White, fontWeight = FontWeight.Bold)
                }

                var isConfFocused by remember { mutableStateOf(false) }
                Button(
                    onClick = {
                        val durationVal = durationText.toIntOrNull() ?: 15
                        if (title.isNotEmpty()) {
                            onConfirm(title, durationVal, icon, description)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TaskGreen),
                    modifier = Modifier
                        .weight(1f)
                        .onFocusChanged { isConfFocused = it.isFocused }
                        .tvFocusModifier(isConfFocused),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Confirmar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --------------------------------------------------------
// OVERLAY UTILITY COMPONENT: PIN ENTRY FOR PARENTS
// --------------------------------------------------------
@Composable
fun ParentPinDialog(
    viewModel: MainViewModel,
    title: String = "Apenas Pais: Digite o PIN de 4 dígitos",
    onDismiss: () -> Unit,
    onVerified: () -> Unit
) {
    val correctPin by viewModel.parentalPin.collectAsState()
    var enteredPin by remember { mutableStateOf("") }
    var isPinIncorrect by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(360.dp)
                .background(TvSurface, RoundedCornerShape(16.dp))
                .border(2.dp, TvSecondary, RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // PIN bubble indicators represent dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val isFilled = i < enteredPin.length
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(
                                if (isPinIncorrect) TaskRed
                                else if (isFilled) TvSecondary
                                else Color(0x2B000000)
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isPinIncorrect) {
                Text(text = "PIN Incorreto! Tente de novo.", color = TaskRed, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            } else {
                Text(text = "Senha padrão: 1234", color = TaskGray, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // D-Pad numeric keyboard for perfect TV navigation
            val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "X", "0", "✓")
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in 0 until 4) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (col in 0 until 3) {
                            val keyIndex = row * 3 + col
                            val keyLabel = keys[keyIndex]

                            var isKeyFocused by remember { mutableStateOf(false) }
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .onFocusChanged { isKeyFocused = it.isFocused }
                                    .tvFocusModifier(isKeyFocused, focusedOutlineColor = TvSecondary, shape = RoundedCornerShape(12.dp))
                                    .background(Color(0x2B000000), RoundedCornerShape(12.dp))
                                    .clickable {
                                        isPinIncorrect = false
                                        when (keyLabel) {
                                            "X" -> {
                                                if (enteredPin.isNotEmpty()) {
                                                    enteredPin = enteredPin.dropLast(1)
                                                }
                                            }
                                            "✓" -> {
                                                if (enteredPin == correctPin) {
                                                    onVerified()
                                                } else {
                                                    isPinIncorrect = true
                                                    enteredPin = ""
                                                }
                                            }
                                            else -> {
                                                if (enteredPin.length < 4) {
                                                    enteredPin += keyLabel
                                                    if (enteredPin.length == 4) {
                                                        if (enteredPin == correctPin) {
                                                            onVerified()
                                                        } else {
                                                            isPinIncorrect = true
                                                            enteredPin = ""
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    .testTag("pin_keyboard_${keyLabel}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = keyLabel,
                                    color = if (keyLabel == "✓") TaskGreen else if (keyLabel == "X") TaskRed else Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            var isCancelFocused by remember { mutableStateOf(false) }
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x2B000000)),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isCancelFocused = it.isFocused }
                    .tvFocusModifier(isCancelFocused, shape = RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Voltar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
