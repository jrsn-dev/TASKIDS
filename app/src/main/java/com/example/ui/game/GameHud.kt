package com.example.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameEngine
import com.example.model.Child
import com.example.ui.design.TaskIdsColors

@Composable
fun LevelHud(
    child: Child,
    modifier: Modifier = Modifier
) {
    val level = GameEngine.levelFor(child.totalXp)

    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.18f), RoundedCornerShape(22.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(TaskIdsColors.Yellow, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(level.level.toString(), color = TaskIdsColors.Ink, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    level.title,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
                Text(
                    "${level.currentXp} / 500 XP",
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 10.sp
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { level.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = TaskIdsColors.Yellow,
            trackColor = Color.White.copy(alpha = 0.15f)
        )
    }
}

@Composable
fun GameStatPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accent: Color = TaskIdsColors.Yellow
) {
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(9.dp).background(accent, CircleShape))
        Spacer(Modifier.width(8.dp))
        Text(label, color = Color.White.copy(alpha = 0.65f), fontSize = 10.sp)
        Spacer(Modifier.width(6.dp))
        Text(value, color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
    }
}
