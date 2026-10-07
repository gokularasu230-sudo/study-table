package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CircularProgressRing
import com.example.ui.components.ShareProgressDialog
import com.example.ui.components.StreakFlameBadge
import com.example.ui.components.TaskCardItem
import com.example.ui.components.XpLevelBadge
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryLight
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.FlameAccent
import com.example.ui.theme.RoseDanger
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.StudyFlowViewModel
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: StudyFlowViewModel,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val exams by viewModel.exams.collectAsState()
    val isAiPlanning by viewModel.isAiPlanning.collectAsState()
    val aiPlanResult by viewModel.aiPlanResult.collectAsState()
    val showShareCard by viewModel.showShareCard.collectAsState()

    val completedTasks = tasks.count { it.isCompleted }
    val totalTasks = tasks.size
    val progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }

    val pendingTasks = tasks.filter { !it.isCompleted }
    val currentTask = pendingTasks.firstOrNull()
    val nextTask = pendingTasks.getOrNull(1)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Header with User Profile, Streak, XP and Share
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$greeting, ${userProfile.displayName} 👋",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "You're ${(progress * 100).toInt()}% through today's plan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleShareCard(true) },
                    modifier = Modifier.testTag("share_progress_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share progress",
                        tint = BrandPrimary
                    )
                }
            }
        }

        // Badges Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StreakFlameBadge(streakDays = userProfile.streakDays)
                XpLevelBadge(level = userProfile.level, levelTitle = userProfile.levelTitle, xp = userProfile.xp)
            }
        }

        // Daily Progress Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TODAY'S TARGET",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = BrandSecondary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${pendingTasks.size} Tasks Remaining",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Estimated study time: ${pendingTasks.sumOf { it.estimatedMinutes }} mins",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    CircularProgressRing(
                        progress = progress,
                        completedCount = completedTasks,
                        totalCount = totalTasks
                    )
                }
            }
        }

        // AI Quick Action Row ("✨ Plan My Day" & "🚑 Rescue My Day")
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.planMyDay() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("plan_my_day_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isAiPlanning
                ) {
                    if (isAiPlanning) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Plan My Day", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                OutlinedButton(
                    onClick = { viewModel.rescueMyDay() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("rescue_my_day_btn"),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isAiPlanning
                ) {
                    Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(18.dp), tint = FlameAccent)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Rescue My Day", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = FlameAccent)
                }
            }
        }

        // CURRENT Task Card
        if (currentTask != null) {
            item {
                Text(
                    text = "CURRENT TASK",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = BrandSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BrandPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = BrandPrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = currentTask.subjectName,
                                    color = BrandPrimaryLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "${currentTask.estimatedMinutes} min",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentTask.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (currentTask.description.isNotBlank()) {
                            Text(
                                text = currentTask.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.selectFocusTask(currentTask.title)
                                    onNavigate(AppScreen.FOCUS)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Start Focus (25m)", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.repository.toggleTaskCompletion(currentTask.id) },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp), tint = EmeraldSuccess)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Done", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // NEXT Task Card
        if (nextTask != null) {
            item {
                Text(
                    text = "NEXT UP",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = nextTask.subjectName,
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandSecondary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = nextTask.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${nextTask.estimatedMinutes}m",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Upcoming Exam Countdown
        val nearestExam = exams.minByOrNull { it.daysRemaining }
        if (nearestExam != null) {
            item {
                Text(
                    text = "UPCOMING EXAM",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = RoseDanger,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(AppScreen.EXAMS) },
                    colors = CardDefaults.cardColors(
                        containerColor = RoseDanger.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(RoseDanger.copy(alpha = 0.4f), Color.Transparent)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "⏳ ${nearestExam.daysRemaining} days until ${nearestExam.subjectName} Exam",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Exam on ${nearestExam.examDate} • Confidence: ${nearestExam.confidenceLevel}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = { onNavigate(AppScreen.EXAMS) },
                            colors = ButtonDefaults.buttonColors(containerColor = RoseDanger),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Prep", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // AI Productivity Coach Tip
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = BrandSecondary.copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = BrandSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AI Study Coach Tip",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = BrandSecondary
                        )
                        Text(
                            text = "You study 28% faster between 5 PM and 8 PM. Put your phone in Do Not Disturb and start a 25m Pomodoro block!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // AI Schedule Result Modal
    if (aiPlanResult != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissAiPlanResult() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BrandPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AI Plan Generated 🚀")
                }
            },
            text = {
                LazyColumn {
                    item {
                        Text(
                            text = aiPlanResult ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissAiPlanResult() },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Apply & Start")
                }
            }
        )
    }

    // Social Progress Card Preview Dialog
    if (showShareCard) {
        ShareProgressDialog(
            userProfile = userProfile,
            onDismiss = { viewModel.toggleShareCard(false) }
        )
    }
}
