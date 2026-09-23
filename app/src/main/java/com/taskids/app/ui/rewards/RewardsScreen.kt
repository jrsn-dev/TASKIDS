package com.taskids.app.ui.rewards

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskids.app.domain.model.Reward
import com.taskids.app.ui.components.FocusSurface
import com.taskids.app.ui.theme.Ink900
import com.taskids.app.ui.theme.Purple500
import com.taskids.app.ui.theme.White
import com.taskids.app.ui.theme.Yellow500
import com.taskids.app.viewmodel.MainViewModel
import com.taskids.app.viewmodel.Screen

@Composable
fun RewardsScreen(viewModel: MainViewModel) {
    val child by viewModel.currentChild.collectAsState()
    val rewards by viewModel.rewards.collectAsState()
    val earned by viewModel.lastEarnedStars.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.linearGradient(listOf(Color(0xFF24115F), Color(0xFF4B1AA6), Color(0xFF18245E)))
        )
    ) {
        Text(
            "✦   ★   ✦",
            color = Yellow500.copy(alpha = .8f),
            fontSize = 34.sp,
            modifier = Modifier.align(Alignment.TopStart).padding(28.dp)
        )
        Text(
            "★   ✦   ★",
            color = Color(0xFFFF62A5).copy(alpha = .75f),
            fontSize = 30.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(38.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FocusSurface(
                    onClick = { viewModel.navigateTo(Screen.Home) },
                    background = Color.White.copy(alpha = .12f),
                    shape = RoundedCornerShape(999.dp),
                    padding = 10.dp
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ArrowBack, null, tint = White)
                        Spacer(Modifier.width(6.dp))
                        Text("Início", color = White, fontWeight = FontWeight.Bold)
                    }
                }
                Box(
                    Modifier
                        .background(Color(0xFF6742E6), CircleShape)
                        .padding(horizontal = 15.dp, vertical = 8.dp)
                ) {
                    Text("⭐ +$earned nesta missão", color = White, fontWeight = FontWeight.Black)
                }
            }

            Spacer(Modifier.height(18.dp))
            Text("🎉", fontSize = 54.sp)
            Text(
                "Parabéns, ${child?.name ?: "Kids"}!",
                color = Yellow500,
                fontSize = 38.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                "Você concluiu as tarefas de hoje.",
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Use suas estrelas em uma recompensa combinada com sua família.",
                color = White.copy(alpha = .78f),
                fontSize = 14.sp
            )
            Spacer(Modifier.height(26.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(rewards, key = { it.id }) { reward ->
                    RewardCard(
                        reward,
                        child?.totalStars ?: 0
                    ) { viewModel.redeemReward(reward) }
                }
            }
        }
    }
}

@Composable
private fun RewardCard(reward: Reward, wallet: Int, onRedeem: () -> Unit) {
    val canRedeem = wallet >= reward.costStars && reward.isEnabled
    FocusSurface(
        onClick = { if (canRedeem) onRedeem() },
        modifier = Modifier.width(270.dp).height(250.dp),
        background = White,
        focusColor = Yellow500,
        shape = RoundedCornerShape(30.dp),
        padding = 20.dp
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                Modifier.background(Color(0xFFF1ECFF), CircleShape).padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(reward.icon, fontSize = 42.sp)
            }
            Text(
                reward.title,
                color = Ink900,
                fontWeight = FontWeight.Black,
                fontSize = 19.sp,
                textAlign = TextAlign.Center
            )
            Text(
                reward.description,
                color = Color(0xFF64748B),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Box(
                Modifier
                    .background(if (canRedeem) Purple500 else Color(0xFFE2E8F0), CircleShape)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    "⭐ ${reward.costStars}",
                    color = if (canRedeem) White else Color(0xFF64748B),
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
