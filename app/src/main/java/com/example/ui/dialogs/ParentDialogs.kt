package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PrimaryTvButton
import com.example.ui.design.TaskIdsColors
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.PinVerificationResult

@Composable
fun ParentPinDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onVerified: () -> Unit
) {
    var entered by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("Digite o PIN de 4 dígitos") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.78f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(390.dp)
                .background(Color.White, RoundedCornerShape(30.dp))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🔒", fontSize = 36.sp)
            Spacer(Modifier.height(10.dp))
            Text(
                "Área dos Pais",
                color = TaskIdsColors.Ink,
                fontSize = 25.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                message,
                color = TaskIdsColors.Muted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                if (index < entered.length) TaskIdsColors.Blue else Color(0xFFDCE5F2),
                                CircleShape
                            )
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            val keys = listOf("1","2","3","4","5","6","7","8","9","⌫","0","✓")
            keys.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { key ->
                        Box(
                            modifier = Modifier
                                .size(68.dp, 52.dp)
                                .background(TaskIdsColors.SoftBg, RoundedCornerShape(14.dp))
                                .clickable {
                                    when (key) {
                                        "⌫" -> if (entered.isNotEmpty()) entered = entered.dropLast(1)
                                        "✓" -> {
                                            when (viewModel.verifyParentalPin(entered)) {
                                                PinVerificationResult.SUCCESS -> onVerified()
                                                PinVerificationResult.INVALID -> {
                                                    message = "PIN incorreto. Tente novamente."
                                                    entered = ""
                                                }
                                                PinVerificationResult.LOCKED -> {
                                                    message = "Muitas tentativas. Aguarde 30 segundos."
                                                    entered = ""
                                                }
                                            }
                                        }
                                        else -> if (entered.length < 4) {
                                            entered += key
                                            if (entered.length == 4) {
                                                when (viewModel.verifyParentalPin(entered)) {
                                                    PinVerificationResult.SUCCESS -> onVerified()
                                                    PinVerificationResult.INVALID -> {
                                                        message = "PIN incorreto. Tente novamente."
                                                        entered = ""
                                                    }
                                                    PinVerificationResult.LOCKED -> {
                                                        message = "Muitas tentativas. Aguarde 30 segundos."
                                                        entered = ""
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                key,
                                color = when (key) {
                                    "✓" -> TaskIdsColors.Green
                                    "⌫" -> TaskIdsColors.Pink
                                    else -> TaskIdsColors.Ink
                                },
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            PrimaryTvButton(
                text = "Voltar",
                background = TaskIdsColors.Ink,
                modifier = Modifier.fillMaxWidth(),
                onClick = onDismiss
            )
        }
    }
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        duration: Int,
        icon: String,
        description: String,
        stars: Int,
        scheduledTime: String?,
        recurrenceDays: String,
        recurring: Boolean
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("15") }
    var stars by remember { mutableStateOf("10") }
    var description by remember { mutableStateOf("") }
    var scheduledTime by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("📋") }
    var recurring by remember { mutableStateOf(false) }
    var selectedDays by remember { mutableStateOf(setOf(1,2,3,4,5)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(620.dp)
                .background(Color.White, RoundedCornerShape(28.dp))
                .padding(26.dp)
        ) {
            Text(
                "Nova atividade",
                color = TaskIdsColors.Ink,
                fontSize = 25.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                "Defina tempo, estrelas e quando essa missão aparece.",
                color = TaskIdsColors.Muted,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("📋","📚","🛁","🍽️","📝","🧸","🦷","⚽","🎨","🧹").forEach { option ->
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                if (icon == option) TaskIdsColors.SoftBlue else TaskIdsColors.SoftBg,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { icon = option },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(option, fontSize = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nome da tarefa") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it.filter(Char::isDigit).take(3) },
                    label = { Text("Minutos") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = stars,
                    onValueChange = { stars = it.filter(Char::isDigit).take(3) },
                    label = { Text("Estrelas") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = scheduledTime,
                    onValueChange = { scheduledTime = it.take(5) },
                    label = { Text("Horário (HH:mm)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Repetir na semana",
                    color = TaskIdsColors.Ink,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .background(
                            if (recurring) TaskIdsColors.Green else Color(0xFFDCE5F2),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { recurring = !recurring }
                        .padding(horizontal = 15.dp, vertical = 8.dp)
                ) {
                    Text(
                        if (recurring) "ATIVO" else "DESATIVADO",
                        color = if (recurring) Color.White else TaskIdsColors.Muted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            if (recurring) {
                Spacer(Modifier.height(12.dp))
                val dayLabels = listOf("S","T","Q","Q","S","S","D")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    dayLabels.forEachIndexed { index, label ->
                        val day = index + 1
                        val selected = day in selectedDays
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(
                                    if (selected) TaskIdsColors.Blue else TaskIdsColors.SoftBg,
                                    CircleShape
                                )
                                .clickable {
                                    selectedDays = if (selected) selectedDays - day else selectedDays + day
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                color = if (selected) Color.White else TaskIdsColors.Muted,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PrimaryTvButton(
                    text = "Cancelar",
                    background = TaskIdsColors.Muted,
                    modifier = Modifier.weight(1f),
                    onClick = onDismiss
                )
                PrimaryTvButton(
                    text = "Adicionar tarefa",
                    background = TaskIdsColors.Blue,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val safeTitle = title.trim()
                        if (safeTitle.isNotEmpty()) {
                            onConfirm(
                                safeTitle,
                                duration.toIntOrNull()?.coerceIn(1,240) ?: 15,
                                icon,
                                description,
                                stars.toIntOrNull()?.coerceIn(1,100) ?: 10,
                                scheduledTime.ifBlank { null },
                                selectedDays.sorted().joinToString(","),
                                recurring
                            )
                        }
                    }
                )
            }
        }
    }
}
