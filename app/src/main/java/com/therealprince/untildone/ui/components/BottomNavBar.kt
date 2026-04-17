package com.therealprince.untildone.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import com.therealprince.untildone.ui.theme.UntilDoneTheme

@Composable
fun BottomNavBar(
    currentScreen: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = UntilDoneTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .background(colors.bottomNavBackground)
            // Consume all taps on the nav bar background so nothing behind it gets clicked
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.bottomNavBorder)
                .align(Alignment.TopCenter)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 28.dp, end = 28.dp, top = 8.dp, bottom = 8.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavButton(
                icon = Icons.Outlined.Home,
                active = currentScreen == "home",
                onClick = { onNavigate("home") }
            )
            NavButton(
                icon = Icons.Outlined.Flag,
                active = currentScreen == "missions",
                onClick = { onNavigate("missions") }
            )
            NavButton(
                icon = Icons.Outlined.BarChart,
                active = currentScreen == "analytics",
                onClick = { onNavigate("analytics") }
            )
            NavButton(
                icon = Icons.Outlined.Settings,
                active = currentScreen == "settings",
                onClick = { onNavigate("settings") }
            )
        }
    }
}

@Composable
private fun NavButton(
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit
) {
    val colors = UntilDoneTheme.colors

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(
                if (active) colors.navActiveBackground else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (active) colors.navActiveContent else colors.navInactiveContent,
            modifier = Modifier.size(22.dp)
        )
    }
}
