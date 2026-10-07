package com.example.data.model

import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
data class UserProfile(
    val userId: String = "",
    val displayName: String = "Student",
    val email: String = "",
    val collegeYear: String = "2nd Year",
    val goals: List<String> = listOf("Better grades", "Reduce procrastination", "Learn coding"),
    val preferredStudyHours: String = "Evening (5 PM - 10 PM)",
    val streakDays: Int = 3,
    val lastActiveDate: String = "",
    val xp: Int = 420,
    val level: Int = 2,
    val levelTitle: String = "Learner",
    val totalFocusMinutes: Int = 185,
    val tasksCompleted: Int = 14,
    val isOnboarded: Boolean = true
) {
    fun calculateLevelTitle(): String {
        return when {
            level >= 5 -> "Academic Beast"
            level == 4 -> "Achiever"
            level == 3 -> "Consistent"
            level == 2 -> "Learner"
            else -> "Freshman"
        }
    }
}

@JsonClass(generateAdapter = true)
data class Subject(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val name: String = "",
    val code: String = "",
    val colorHex: String = "#6366F1",
    val targetGrade: String = "A",
    val creditHours: Int = 3
)

@JsonClass(generateAdapter = true)
data class Subtask(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val isCompleted: Boolean = false
)

@JsonClass(generateAdapter = true)
data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val subjectName: String = "",
    val subjectId: String = "",
    val priority: String = "MEDIUM", // "HIGH", "MEDIUM", "LOW"
    val deadline: String = "Tomorrow",
    val estimatedMinutes: Int = 30,
    val isCompleted: Boolean = false,
    val scheduledDate: String = "Today",
    val scheduledTime: String = "5:00 PM",
    val isAIGenerated: Boolean = false,
    val subtasks: List<Subtask> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ExamItem(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val subjectName: String = "",
    val examDate: String = "",
    val daysRemaining: Int = 7,
    val topics: String = "",
    val confidenceLevel: String = "Medium", // "Low", "Medium", "High"
    val aiStudyPlan: String = ""
)

@JsonClass(generateAdapter = true)
data class FocusSession(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val taskTitle: String = "",
    val durationMinutes: Int = 25,
    val xpEarned: Int = 50,
    val timestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val userId: String = "",
    val role: String = "assistant", // "user", "assistant"
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean = false,
    val xpReward: Int = 100
)

data class StudyTool(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val placeholder: String,
    val promptPrefix: String
)
