package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Reward
import com.example.ui.components.PrimaryTvButton
import com.example.ui.components.StarPill
import com.example.ui.components.taskIdsFocus
import com.example.ui.design.TaskIdsColors
import com.example.ui.game.RewardArt
import com.example.ui.navigation.AppScreen
import com.example.viewmodel.MainViewModel

@Composable
fun RewardsScreen(viewModel: MainViewModel) {
    val rewards by viewModel.rewards.collectAsState()
    val child by viewModel.currentChild.collectAsState()
    val message by viewModel.lastRewardMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PrimaryTvButton(
                text = "← Início",
                background = TaskIdsColors.Blue,
                onClick = { viewModel.navigateTo(AppScreen.Home) }
            )
            Spacer(Modifier.width(18.dp))
            Column {
                Text(
                    "Recompensas",
                    color = TaskIdsColors.Ink,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "Troque estrelas por experiências aprovadas pela família.",
                    color = TaskIdsColors.Muted,
                    fontSize = 14.sp
                )
            }
            Spacer(Modifier.weight(1f))
            StarPill(child?.totalStars ?: 0)
        }

        Spacer(Modifier.height(20.dp))

        if (!message.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Text(message ?: "", color = TaskIdsColors.Ink, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(14.dp))
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(rewards, key = { it.id }) { reward ->
                RewardCard(
                    reward = reward,
                    availableStars = child?.totalStars ?: 0,
                    onRedeem = { viewModel.redeemReward(reward) }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Text(
                "Tempo de tela não abre um navegador livre. A experiência permanece dentro da área controlada do TASKIDS.",
                color = TaskIdsColors.Muted,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun RewardCard(
    reward: Reward,
    availableStars: Int,
    onRedeem: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val canRedeem = availableStars >= reward.costStars

    Column(
        modifier = Modifier
            .width(270.dp)
            .height(300.dp)
            .onFocusChanged { focused = it.isFocused }
            .taskIdsFocus(focused, RoundedCornerShape(28.dp), TaskIdsColors.Yellow)
            .background(Color.White, RoundedCornerShape(28.dp))
            .clickable(enabled = canRedeem, onClick = onRedeem)
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .background(TaskIdsColors.SoftBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            RewardArt(type = reward.type, modifier = Modifier.size(82.dp))
        }

        Spacer(Modifier.height(15.dp))

        Text(
            reward.title,
            color = TaskIdsColors.Ink,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Text(
            reward.description,
            color = TaskIdsColors.Muted,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.weight(1f))

        Text(
            "${reward.costStars} estrelas",
            color = TaskIdsColors.Ink,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(Modifier.height(9.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (canRedeem) TaskIdsColors.Green else Color(0xFFCBD4E2),
                    RoundedCornerShape(16.dp)
                )
                .padding(vertical = 11.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (canRedeem) "Desbloquear" else "Junte mais estrelas",
                color = TaskIdsColors.Ink,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
            )
        }
    }
}
