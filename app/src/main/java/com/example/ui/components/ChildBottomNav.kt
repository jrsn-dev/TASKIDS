package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.design.TaskIdsColors
import com.example.ui.navigation.AppScreen

@Composable
fun ChildBottomNav(
    current: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    onParents: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            label = "Início",
            icon = Icons.Filled.Home,
            selected = current is AppScreen.Home,
            onClick = { onNavigate(AppScreen.Home) }
        )
        BottomNavItem(
            label = "Missões",
            icon = Icons.Filled.CheckCircle,
            selected = current is AppScreen.Missions,
            onClick = { onNavigate(AppScreen.Missions) }
        )
        BottomNavItem(
            label = "Recompensas",
            icon = Icons.Filled.Star,
            selected = current is AppScreen.Rewards,
            onClick = { onNavigate(AppScreen.Rewards) }
        )
        BottomNavItem(
            label = "Pais",
            icon = Icons.Filled.Person,
            selected = current is AppScreen.Parent || current is AppScreen.ParentAccess,
            onClick = onParents
        )
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 7.dp, vertical = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    if (selected) TaskIdsColors.SoftBlue else Color.Transparent,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) TaskIdsColors.Blue else TaskIdsColors.Muted,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            label,
            color = if (selected) TaskIdsColors.Blue else TaskIdsColors.Muted,
            fontSize = 9.sp,
            fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold
        )
    }
}
