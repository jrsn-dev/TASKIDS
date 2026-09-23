package com.example.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Achievement
import com.example.ui.design.TaskIdsColors

@Composable
fun AchievementShelf(
    achievements: List<Achievement>,
    modifier: Modifier = Modifier
) {
    val items = achievements.take(6)
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.14f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "CONQUISTAS",
            color = Color.White.copy(alpha = 0.58f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )

        items.forEachIndexed { index, achievement ->
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        if (achievement.unlocked) {
                            listOf(
                                TaskIdsColors.Yellow,
                                TaskIdsColors.Green,
                                TaskIdsColors.Blue,
                                TaskIdsColors.Purple,
                                TaskIdsColors.Orange,
                                TaskIdsColors.Pink
                            )[index % 6]
                        } else Color.White.copy(alpha = 0.10f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (achievement.unlocked) (index + 1).toString() else "·",
                    color = if (achievement.unlocked) TaskIdsColors.Ink else Color.White.copy(alpha = 0.35f),
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                )
            }
        }
    }
}
