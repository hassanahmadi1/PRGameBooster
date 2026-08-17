package com.prgamebooster.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prgamebooster.core.theme.*

/** کارت پایه با گوشه‌های نرم و پس‌زمینه تیره طبق پالت رنگ پروژه. */
@Composable
fun PRCard(
    modifier: Modifier = Modifier,
    active: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(if (active) CardActiveColor else CardColor)
            .then(
                if (active) Modifier.border(BorderStroke(1.5.dp, PrimaryGold), shape) else Modifier
            )
            .padding(16.dp),
        content = content
    )
}

/** دکمه اصلی طلایی با Touch Feedback واقعی (scale هنگام فشار به‌جای Ripple ساده). */
@Composable
fun PRPrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, label = "buttonScale")

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(listOf(PrimaryGoldLight, PrimaryGold)))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = BackgroundPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

/** نشانگر وضعیت رنگی (سبز/قرمز/نارنجی) با یک نقطه کوچک + برچسب. */
@Composable
fun StatusDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, color = TextSecondary, fontSize = 13.sp)
    }
}

/** کارت متریک کوچک برای صفحه خانه/مانیتور (باتری، شبکه، تأخیر، FPS). */
@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    valueColor: Color = TextPrimary,
    subtitle: String? = null
) {
    PRCard(modifier = modifier) {
        Text(text = title, color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = value, color = valueColor, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
        }
    }
}
