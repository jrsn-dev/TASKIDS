package com.taskids.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskids.app.ui.theme.Blue500
import com.taskids.app.ui.theme.Yellow500

@Composable
fun FocusSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    background: Color = Color.White.copy(alpha = 0.10f),
    focusColor: Color = Color.White,
    scale: Float = 1.035f,
    padding: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    var focused by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(if (focused) scale else 1f, label = "focus_scale")

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .shadow(if (focused) 14.dp else 0.dp, shape)
            .clip(shape)
            .background(background)
            .border(
                if (focused) 3.dp else 1.dp,
                if (focused) focusColor else Color.White.copy(alpha = .10f),
                shape
            )
            .onFocusChanged { focused = it.isFocused }
            .clickable(onClick = onClick)
            .padding(padding)
    ) {
        content()
    }
}

@Composable
fun StarPill(stars: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(Color(0xFF5633D8), CircleShape)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("⭐", fontSize = 18.sp)
        Text(stars.toString(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 17.sp)
    }
}

@Composable
fun ProfilePill(
    avatar: String,
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FocusSurface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        background = if (selected) Blue500 else Color.White.copy(alpha = .10f),
        focusColor = Yellow500,
        padding = 5.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White.copy(alpha = .94f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(avatar, fontSize = 22.sp)
            }
            Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
fun SectionChip(text: String, color: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(color.copy(alpha = .16f), CircleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(7.dp).background(color, CircleShape))
        Spacer(Modifier.width(7.dp))
        Text(text, color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
