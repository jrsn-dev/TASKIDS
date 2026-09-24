package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TaskIdsLogo
import com.example.ui.design.TaskIdsColors
import com.example.ui.navigation.AppScreen
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.PinVerificationResult

@Composable
fun ParentAccessScreen(viewModel: MainViewModel) {
    var entered by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("Digite o PIN de 4 dígitos") }

    fun submit() {
        when (viewModel.verifyParentalPin(entered)) {
            PinVerificationResult.SUCCESS -> viewModel.navigateTo(AppScreen.Parent)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFEAF5FF), Color(0xFFF6F2FF), Color.White)
                )
            )
            .statusBarsPadding()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .background(Color.White, RoundedCornerShape(32.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TaskIdsLogo(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .height(52.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Área dos Pais",
                color = TaskIdsColors.Ink,
                fontSize = 29.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                message,
                color = TaskIdsColors.Muted,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(
                                if (index < entered.length) TaskIdsColors.Blue else Color(0xFFDCE5F2),
                                CircleShape
                            )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            val keys = listOf("1","2","3","4","5","6","7","8","9","←","0","✓")
            keys.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { key ->
                        Box(
                            modifier = Modifier
                                .size(82.dp, 58.dp)
                                .background(
                                    when (key) {
                                        "✓" -> TaskIdsColors.Green
                                        else -> TaskIdsColors.SoftBg
                                    },
                                    RoundedCornerShape(18.dp)
                                )
                                .clickable {
                                    when (key) {
                                        "←" -> if (entered.isNotEmpty()) entered = entered.dropLast(1)
                                        "✓" -> if (entered.length == 4) submit()
                                        else -> if (entered.length < 4) {
                                            entered += key
                                            if (entered.length == 4) submit()
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                key,
                                color = if (key == "✓") Color.White else TaskIdsColors.Ink,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "Voltar ao app",
                color = TaskIdsColors.Blue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { viewModel.navigateTo(AppScreen.Home) }
                    .padding(10.dp)
            )
        }
    }
}
