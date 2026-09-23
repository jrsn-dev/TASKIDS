package com.taskids.app.ui.parental

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskids.app.ui.components.FocusSurface
import com.taskids.app.ui.theme.Blue500
import com.taskids.app.ui.theme.Ink900
import com.taskids.app.ui.theme.Red500
import com.taskids.app.ui.theme.White
import com.taskids.app.viewmodel.MainViewModel

@Composable
fun ParentPinDialog(
    viewModel: MainViewModel,
    title: String = "Área protegida para responsáveis",
    onDismiss: () -> Unit,
    onVerified: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    val feedback by viewModel.pinFeedback.collectAsState()
    val pinConfigured by viewModel.pinConfigured.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = .78f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(White, RoundedCornerShape(28.dp))
                .padding(26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (pinConfigured) title else "Crie o PIN parental",
                color = Ink900,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                if (pinConfigured) {
                    "Digite seu PIN de 4 números."
                } else {
                    "Primeiro acesso: escolha 4 números que apenas os responsáveis conheçam."
                },
                color = Color(0xFF64748B),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(4) { index ->
                    Box(
                        Modifier
                            .size(16.dp)
                            .background(
                                if (index < pin.length) Blue500 else Color(0xFFDCE4EF),
                                CircleShape
                            )
                    )
                }
            }

            if (!feedback.isNullOrBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    feedback.orEmpty(),
                    color = Red500,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(18.dp))
            val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "←", "0", "✓")
            keys.chunked(3).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    row.forEach { key ->
                        FocusSurface(
                            onClick = {
                                viewModel.clearPinFeedback()
                                when (key) {
                                    "←" -> if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                    "✓" -> {
                                        if (!pinConfigured) {
                                            if (viewModel.updateParentalPin(pin)) {
                                                onVerified()
                                            } else {
                                                pin = ""
                                            }
                                        } else {
                                            val result = viewModel.verifyParentPin(pin)
                                            if (result.success) onVerified() else pin = ""
                                        }
                                    }
                                    else -> {
                                        if (pin.length < 4) pin += key
                                        if (pin.length == 4) {
                                            if (!pinConfigured) {
                                                if (viewModel.updateParentalPin(pin)) {
                                                    onVerified()
                                                } else {
                                                    pin = ""
                                                }
                                            } else {
                                                val result = viewModel.verifyParentPin(pin)
                                                if (result.success) onVerified() else pin = ""
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.size(58.dp),
                            background = Color(0xFFF0F5FB),
                            focusColor = Blue500,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    key,
                                    color = if (key == "✓") Blue500 else Ink900,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            FocusSurface(
                onClick = {
                    viewModel.clearPinFeedback()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                background = Color(0xFFF0F5FB),
                focusColor = Blue500,
                shape = RoundedCornerShape(14.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Cancelar", color = Ink900, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
