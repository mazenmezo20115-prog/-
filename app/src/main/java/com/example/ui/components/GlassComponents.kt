package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary

/**
 * Apple-inspired frosted glass surface card with translucent fill and subtle specular highlight border.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    backgroundColor: Color = Color(0x33101C26),
    borderColor: Color = Color(0x33FFFFFF),
    borderWidth: Dp = 1.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor.copy(alpha = (backgroundColor.alpha * 1.15f).coerceAtMost(0.9f)),
                        backgroundColor.copy(alpha = (backgroundColor.alpha * 0.85f))
                    )
                )
            )
            .border(
                BorderStroke(
                    borderWidth,
                    Brush.verticalGradient(
                        colors = listOf(
                            borderColor,
                            borderColor.copy(alpha = borderColor.alpha * 0.4f)
                        )
                    )
                ),
                shape = shape
            )
    ) {
        content()
    }
}

/**
 * Sleek glass button with Apple-like micro-border and subtle gradient.
 */
@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isPrimary: Boolean = false,
    accentColor: Color = EmeraldPrimary
) {
    val bgBrush = if (isPrimary) {
        Brush.horizontalGradient(
            colors = listOf(accentColor, accentColor.copy(alpha = 0.85f))
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(Color(0x33FFFFFF), Color(0x1AFFFFFF))
        )
    }

    val textColor = if (isPrimary) Color.Black else Color.White

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgBrush)
            .border(
                BorderStroke(
                    1.dp,
                    if (isPrimary) accentColor.copy(alpha = 0.8f) else Color(0x4DFFFFFF)
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                androidx.compose.material3.Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )
        }
    }
}

/**
 * Football role badge (GK, DEF, MID, ATT) with Apple-styled translucent coloring.
 */
@Composable
fun RoleBadge(
    role: String,
    modifier: Modifier = Modifier
) {
    val (badgeColor, textColor) = when (role.uppercase()) {
        "GK" -> Pair(Color(0xFFFFD60A), Color.Black)
        "DEF", "CB", "LB", "RB", "LWB", "RWB" -> Pair(Color(0xFF00E676), Color.Black)
        "MID", "CDM", "CM", "CAM", "LM", "RM" -> Pair(Color(0xFF0A84FF), Color.White)
        else -> Pair(Color(0xFFFF453A), Color.White) // ATT, ST, CF, LW, RW
    }

    Surface(
        color = badgeColor.copy(alpha = 0.22f),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(0.5.dp, badgeColor.copy(alpha = 0.6f)),
        modifier = modifier
    ) {
        Text(
            text = role,
            color = if (badgeColor == Color(0xFFFFD60A)) Color(0xFFFFD60A) else badgeColor,
            fontSize = 10.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
