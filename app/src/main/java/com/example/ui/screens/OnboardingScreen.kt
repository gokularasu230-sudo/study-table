package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryLight
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.FlameAccent
import com.example.ui.viewmodel.StudyFlowViewModel

@Composable
fun OnboardingScreen(
    viewModel: StudyFlowViewModel,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) }

    var selectedYear by remember { mutableStateOf("2nd Year") }
    var subjectsInput by remember { mutableStateOf("C Programming, Engineering Chemistry, Calculus, Data Structures") }
    var timetableHours by remember { mutableStateOf("College from 9:00 AM to 4:00 PM") }
    var studyHours by remember { mutableStateOf("Evening (5:00 PM - 10:00 PM)") }

    val allGoals = listOf(
        "Better grades 🎯",
        "Complete assignments on time 📚",
        "Prepare for upcoming exams ⏳",
        "Learn coding & software 💻",
        "Improve consistency 🔥",
        "Reduce procrastination ⚡"
    )
    val selectedGoals = remember { mutableStateListOf("Better grades 🎯", "Reduce procrastination ⚡", "Learn coding & software 💻") }
    var isGeneratingFirstPlan by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Step indicator dots
        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (1..7).forEach { s ->
                Box(
                    modifier = Modifier
                        .size(if (step == s) 24.dp else 8.dp, 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (step >= s) BrandPrimary else MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }

        // Step Content
        AnimatedContent(targetState = step, label = "onboarding_step") { currentStep ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (currentStep) {
                    1 -> {
                        // Welcome
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(BrandPrimary.copy(alpha = 0.4f), Color.Transparent)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(48.dp))
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "STUDYFLOW AI",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            color = BrandSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Your college life,\norganized.",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "\"Plan it. Learn it. Finish it.\"\nPersonalized timetable generation, task prioritization, and Pomodoro focus engine built for ambitious students.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    2 -> {
                        // Select Year
                        Text(
                            text = "Select your academic year",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        listOf("1st Year (Freshman)", "2nd Year (Sophomore)", "3rd Year (Junior)", "4th Year (Senior)", "Postgraduate / Other").forEach { yr ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable { selectedYear = yr },
                                color = if (selectedYear == yr) BrandPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (selectedYear == yr) BrandPrimary else Color.Transparent
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = yr, fontWeight = FontWeight.SemiBold)
                                    if (selectedYear == yr) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = BrandPrimary)
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        // Add Subjects
                        Text(
                            text = "What subjects are you taking?",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Enter your current courses separated by commas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = subjectsInput,
                            onValueChange = { subjectsInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("subjects_input"),
                            minLines = 4,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    4 -> {
                        // Timetable
                        Text(
                            text = "Class & College Hours",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "When are you busy at college each day?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        listOf(
                            "9:00 AM – 4:00 PM (Full Day)",
                            "8:30 AM – 1:30 PM (Morning Half)",
                            "1:00 PM – 6:00 PM (Afternoon Shift)",
                            "Flexible / Self-Paced"
                        ).forEach { tt ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable { timetableHours = tt },
                                color = if (timetableHours == tt) BrandPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (timetableHours == tt) BrandPrimary else Color.Transparent
                                )
                            ) {
                                Text(
                                    text = tt,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }

                    5 -> {
                        // Study Hours
                        Text(
                            text = "Preferred Study Window",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "When do you feel most productive studying?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        listOf(
                            "🌅 Early Morning (5:00 AM - 8:30 AM)",
                            "🌆 Evening (5:00 PM - 9:00 PM)",
                            "🌙 Night Owl (9:00 PM - 1:00 AM)",
                            "⚡ Custom Multi-Block"
                        ).forEach { sh ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable { studyHours = sh },
                                color = if (studyHours == sh) BrandPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (studyHours == sh) BrandPrimary else Color.Transparent
                                )
                            ) {
                                Text(
                                    text = sh,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }

                    6 -> {
                        // Goals Selection
                        Text(
                            text = "What are your primary goals?",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        allGoals.forEach { goal ->
                            val isSelected = selectedGoals.contains(goal)
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        if (isSelected) selectedGoals.remove(goal) else selectedGoals.add(goal)
                                    },
                                color = if (isSelected) BrandPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) BrandPrimary else Color.Transparent
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = goal, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    7 -> {
                        // AI Final plan generation
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(FlameAccent.copy(alpha = 0.4f), Color.Transparent)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = FlameAccent, modifier = Modifier.size(48.dp))
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Your first plan is ready 🚀",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "✨ Today's AI Personalized Schedule",
                                    fontWeight = FontWeight.Bold,
                                    color = BrandSecondary,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "• 5:00–5:45 PM → C loops & pointer logic\n• 5:45–6:00 PM → Break ☕\n• 6:00–6:45 PM → Chemistry assignment\n• 7:30–8:00 PM → C practice exercises",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (step > 1) {
                TextButton(onClick = { step -= 1 }) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.width(60.dp))
            }

            Button(
                onClick = {
                    if (step < 7) {
                        step += 1
                    } else {
                        val subs = subjectsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        viewModel.repository.completeOnboarding(selectedYear, subs, selectedGoals.toList(), studyHours)
                        onFinish()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("onboarding_next_btn")
            ) {
                Text(if (step == 7) "Start StudyFlow 🚀" else "Continue")
            }
        }
    }
}
