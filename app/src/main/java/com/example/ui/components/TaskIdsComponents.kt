package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Child
import com.example.ui.design.TaskIdsColors

@Composable
fun Modifier.taskIdsFocus(
    isFocused: Boolean,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    focusColor: Color = Color.White
): Modifier {
    val scale by animateFloatAsState(if (isFocused) 1.05f else 1f, label = "focus-scale")
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.border(
        width = if (isFocused) 3.dp else 1.dp,
        color = if (isFocused) focusColor else Color.White.copy(alpha = 0.12f),
        shape = shape
    )
}

@Composable
fun StarPill(stars: Int, dark: Boolean = true) {
    Row(
        modifier = Modifier
            .background(
                if (dark) Color(0xFF4D35C8) else TaskIdsColors.SoftBlue,
                CircleShape
            )
            .padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("⭐", fontSize = 18.sp)
        Spacer(Modifier.width(7.dp))
        Text(
            text = stars.toString(),
            color = if (dark) Color.White else TaskIdsColors.Ink,
            fontSize = 17.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun ProfilePill(
    child: Child,
    selected: Boolean,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .onFocusChanged { focused = it.isFocused }
            .taskIdsFocus(focused, RoundedCornerShape(24.dp), TaskIdsColors.Yellow)
            .background(
                if (selected) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color.White.copy(alpha = 0.18f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(child.avatarEmoji, fontSize = 21.sp)
        }
        Spacer(Modifier.width(8.dp))
        Text(
            child.name,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PrimaryTvButton(
    text: String,
    background: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .onFocusChanged { focused = it.isFocused }
            .taskIdsFocus(focused, RoundedCornerShape(18.dp), Color.White)
            .background(background, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MetricCard(
    icon: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accent: Color = TaskIdsColors.Blue
) {
    Column(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(22.dp))
            .border(1.dp, Color(0xFFE6ECF5), RoundedCornerShape(22.dp))
            .padding(18.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(accent.copy(alpha = 0.13f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 20.sp)
        }
        Spacer(Modifier.height(12.dp))
        Text(label, color = TaskIdsColors.Muted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(3.dp))
        Text(value, color = TaskIdsColors.Ink, fontSize = 20.sp, fontWeight = FontWeight.Black)
    }
}
