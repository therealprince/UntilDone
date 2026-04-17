package com.therealprince.untildone.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therealprince.untildone.data.Journey
import com.therealprince.untildone.ui.components.JourneyCard
import com.therealprince.untildone.ui.theme.Emerald500
import com.therealprince.untildone.ui.theme.Emerald600
import com.therealprince.untildone.ui.theme.Neutral300
import com.therealprince.untildone.ui.theme.Neutral400
import com.therealprince.untildone.ui.theme.UntilDoneTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    journeys: List<Journey>,
    userName: String,
    profileImagePath: String,
    onNavigate: (String, Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = UntilDoneTheme.colors
    val today = remember {
        SimpleDateFormat("EEEE, MMM d", Locale.US).format(Date())
    }
    val userInitial = remember(userName) {
        userName.firstOrNull()?.uppercase() ?: "U"
    }
    val profileBitmap = remember(profileImagePath) {
        if (profileImagePath.isNotBlank()) {
            try { BitmapFactory.decodeFile(profileImagePath) }
            catch (e: Exception) { null }
        } else null
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header — Apple-style
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Today",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp
                    ),
                    color = colors.textPrimary
                )
                Text(
                    text = today,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textTertiary
                )
            }

            // Profile avatar (no dropdown)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(colors.profileBackground),
                contentAlignment = Alignment.Center
            ) {
                if (profileBitmap != null) {
                    Image(
                        bitmap = profileBitmap.asImageBitmap(),
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = userInitial,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.profileContent
                    )
                }
            }
        }

        // Focus Session Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(colors.focusCardStart, colors.focusCardEnd),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccessTime,
                            contentDescription = null,
                            tint = Emerald500,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "FOCUS SESSION",
                            style = MaterialTheme.typography.labelMedium,
                            color = Neutral300,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Deep Work",
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                        color = androidx.compose.ui.graphics.Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "25 mins undistracted",
                        style = MaterialTheme.typography.bodySmall,
                        color = Neutral400
                    )
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Emerald600)
                        .clickable { onNavigate("timer", null) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Start Focus",
                        tint = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Active Missions Section
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Active Missions",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.textPrimary
                )
                Text(
                    text = "${journeys.size} Open",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textTertiary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (journeys.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.cardBackground)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No active missions",
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap + to start your first mission",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textTertiary
                        )
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    journeys.forEach { journey ->
                        JourneyCard(
                            journey = journey,
                            onClick = { onNavigate("detail", journey.id) }
                        )
                    }
                }
            }
        }
    }
}
