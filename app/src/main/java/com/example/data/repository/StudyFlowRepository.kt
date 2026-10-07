package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.ai.GeminiClient
import com.example.data.model.Achievement
import com.example.data.model.ChatMessage
import com.example.data.model.ExamItem
import com.example.data.model.FocusSession
import com.example.data.model.Subject
import com.example.data.model.Subtask
import com.example.data.model.TaskItem
import com.example.data.model.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class StudyFlowRepository(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val auth = Firebase.auth

    private val firestore: FirebaseFirestore? by lazy {
        try {
            val dbId = context.getString(R.string.firestore_database_id)
            FirebaseFirestore.getInstance(dbId)
        } catch (e: Exception) {
            Log.e("StudyFlowRepo", "Firestore init error: ${e.message}")
            null
        }
    }

    // State flows
    private val _userProfile = MutableStateFlow(
        UserProfile(
            userId = auth.currentUser?.uid ?: "local_student",
            displayName = auth.currentUser?.displayName ?: "Gokul",
            email = auth.currentUser?.email ?: "gokularasu230@gmail.com",
            collegeYear = "2nd Year",
            goals = listOf("Better grades", "Reduce procrastination", "Learn coding"),
            streakDays = 5,
            xp = 580,
            level = 2,
            levelTitle = "Learner",
            totalFocusMinutes = 145,
            tasksCompleted = 9,
            isOnboarded = true
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _subjects = MutableStateFlow<List<Subject>>(
        listOf(
            Subject(id = "sub_1", name = "C Programming", code = "CS101", colorHex = "#6366F1", targetGrade = "A", creditHours = 4),
            Subject(id = "sub_2", name = "Engineering Chemistry", code = "CH102", colorHex = "#06B6D4", targetGrade = "A+", creditHours = 3),
            Subject(id = "sub_3", name = "Calculus & Linear Algebra", code = "MA103", colorHex = "#8B5CF6", targetGrade = "A", creditHours = 4),
            Subject(id = "sub_4", name = "Data Structures", code = "CS201", colorHex = "#10B981", targetGrade = "O", creditHours = 4)
        )
    )
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskItem>>(
        listOf(
            TaskItem(
                id = "task_1",
                title = "Practice nested loops & pointers in C",
                description = "Solve 4 pattern printing questions and 2 pointer exercises from lecture notes",
                subjectName = "C Programming",
                subjectId = "sub_1",
                priority = "HIGH",
                deadline = "Today",
                estimatedMinutes = 25,
                isCompleted = false,
                scheduledDate = "Today",
                scheduledTime = "5:00 PM",
                isAIGenerated = true,
                subtasks = listOf(
                    Subtask(id = "st_1", title = "Read pointer syntax cheat sheet", isCompleted = true),
                    Subtask(id = "st_2", title = "Implement pyramid pattern", isCompleted = false),
                    Subtask(id = "st_3", title = "Write pointer swap function", isCompleted = false)
                )
            ),
            TaskItem(
                id = "task_2",
                title = "Engineering Chemistry Assignment",
                description = "Zirconia refractory bricks and phase rule diagrams",
                subjectName = "Engineering Chemistry",
                subjectId = "sub_2",
                priority = "HIGH",
                deadline = "Tomorrow",
                estimatedMinutes = 45,
                isCompleted = false,
                scheduledDate = "Today",
                scheduledTime = "6:00 PM",
                isAIGenerated = true,
                subtasks = listOf(
                    Subtask(id = "st_4", title = "Draw one-component water system diagram", isCompleted = false),
                    Subtask(id = "st_5", title = "Explain zirconia brick stabilization", isCompleted = false)
                )
            ),
            TaskItem(
                id = "task_3",
                title = "Calculus problem set #4",
                description = "Eigenvalues and Cayley-Hamilton theorem verification",
                subjectName = "Calculus & Linear Algebra",
                subjectId = "sub_3",
                priority = "MEDIUM",
                deadline = "Thursday",
                estimatedMinutes = 35,
                isCompleted = true,
                scheduledDate = "Today",
                scheduledTime = "3:30 PM",
                isAIGenerated = false,
                subtasks = emptyList()
            )
        )
    )
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    private val _exams = MutableStateFlow<List<ExamItem>>(
        listOf(
            ExamItem(
                id = "exam_1",
                subjectName = "C Programming",
                examDate = "Friday",
                daysRemaining = 4,
                topics = "Pointers, Structs, File I/O, Dynamic Memory Allocation",
                confidenceLevel = "Medium",
                aiStudyPlan = "Day 1: Pointers & Arrays | Day 2: Dynamic Allocation (malloc/free) | Day 3: Structs & Unions | Day 4: Past paper test simulation"
            ),
            ExamItem(
                id = "exam_2",
                subjectName = "Engineering Chemistry",
                examDate = "Next Wednesday",
                daysRemaining = 9,
                topics = "Phase Rule, Refractory Materials, Polymers, Electrochemistry",
                confidenceLevel = "High",
                aiStudyPlan = "Focus on Phase diagrams and numericals in Electrochemistry."
            )
        )
    )
    val exams: StateFlow<List<ExamItem>> = _exams.asStateFlow()

    private val _focusSessions = MutableStateFlow<List<FocusSession>>(
        listOf(
            FocusSession(id = "fs_1", taskTitle = "Calculus Eigenvalues", durationMinutes = 25, xpEarned = 50, timestamp = System.currentTimeMillis() - 7200000),
            FocusSession(id = "fs_2", taskTitle = "C Programming Pointers", durationMinutes = 25, xpEarned = 50, timestamp = System.currentTimeMillis() - 3600000)
        )
    )
    val focusSessions: StateFlow<List<FocusSession>> = _focusSessions.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "msg_init",
                role = "assistant",
                content = "👋 Welcome to **StudyFlow AI**! I've loaded your schedule. Ask me to explain difficult topics, plan your day, quiz you with MCQs, or break down large assignments.",
                timestamp = System.currentTimeMillis()
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _achievements = MutableStateFlow<List<Achievement>>(
        listOf(
            Achievement("ach_1", "🔥 5-Day Streak", "Studied 5 consecutive days without breaking flow", "flame", isUnlocked = true, xpReward = 150),
            Achievement("ach_2", "⚡ First 10 Tasks", "Completed 10 tasks in StudyFlow", "bolt", isUnlocked = true, xpReward = 100),
            Achievement("ach_3", "🎯 Exam Warrior", "Created a dedicated AI exam revision schedule", "target", isUnlocked = true, xpReward = 120),
            Achievement("ach_4", "💻 Coding Beast", "Spent over 2 hours coding C programming exercises", "laptop", isUnlocked = false, xpReward = 200),
            Achievement("ach_5", "📚 10 Hours Studied", "Logged 10 hours of focused study sessions", "book", isUnlocked = false, xpReward = 250),
            Achievement("ach_6", "🏆 100 Tasks Completed", "Master of productivity: 100 completed tasks", "trophy", isUnlocked = false, xpReward = 500)
        )
    )
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    // Task Actions
    fun toggleTaskCompletion(taskId: String) {
        _tasks.update { list ->
            list.map { task ->
                if (task.id == taskId) {
                    val nextState = !task.isCompleted
                    if (nextState) {
                        addXp(30)
                        _userProfile.update { it.copy(tasksCompleted = it.tasksCompleted + 1) }
                    }
                    task.copy(isCompleted = nextState)
                } else task
            }
        }
        syncTasksToFirestore()
    }

    fun toggleSubtask(taskId: String, subtaskId: String) {
        _tasks.update { list ->
            list.map { task ->
                if (task.id == taskId) {
                    val updatedSubtasks = task.subtasks.map { st ->
                        if (st.id == subtaskId) st.copy(isCompleted = !st.isCompleted) else st
                    }
                    val allCompleted = updatedSubtasks.isNotEmpty() && updatedSubtasks.all { it.isCompleted }
                    task.copy(subtasks = updatedSubtasks, isCompleted = allCompleted)
                } else task
            }
        }
        syncTasksToFirestore()
    }

    fun addTask(
        title: String,
        subjectName: String,
        description: String = "",
        deadline: String = "Tomorrow",
        estimatedMinutes: Int = 30,
        priority: String = "MEDIUM",
        scheduledTime: String = "5:00 PM"
    ) {
        val newTask = TaskItem(
            id = UUID.randomUUID().toString(),
            userId = auth.currentUser?.uid ?: "local_student",
            title = title,
            subjectName = subjectName,
            description = description,
            deadline = deadline,
            estimatedMinutes = estimatedMinutes,
            priority = priority,
            scheduledTime = scheduledTime,
            isCompleted = false,
            scheduledDate = "Today"
        )
        _tasks.update { listOf(newTask) + it }
        addXp(15)
        syncTasksToFirestore()
    }

    fun deleteTask(taskId: String) {
        _tasks.update { it.filter { t -> t.id != taskId } }
        syncTasksToFirestore()
    }

    fun addSubject(name: String, code: String, colorHex: String, targetGrade: String = "A", creditHours: Int = 3) {
        val sub = Subject(
            id = UUID.randomUUID().toString(),
            userId = auth.currentUser?.uid ?: "local_student",
            name = name,
            code = code,
            colorHex = colorHex,
            targetGrade = targetGrade,
            creditHours = creditHours
        )
        _subjects.update { it + sub }
        addXp(20)
    }

    fun addExam(subjectName: String, date: String, topics: String, confidence: String = "Medium") {
        val exam = ExamItem(
            id = UUID.randomUUID().toString(),
            userId = auth.currentUser?.uid ?: "local_student",
            subjectName = subjectName,
            examDate = date,
            daysRemaining = 6,
            topics = topics,
            confidenceLevel = confidence,
            aiStudyPlan = "AI Plan: Day 1-2 Concept Mastery | Day 3-4 Practice Problems | Day 5 Past Year Questions | Day 6 Mock Test"
        )
        _exams.update { listOf(exam) + it }
        addXp(50)
    }

    fun logFocusSession(taskTitle: String, minutes: Int) {
        val xp = (minutes * 2).coerceAtLeast(20)
        val session = FocusSession(
            id = UUID.randomUUID().toString(),
            userId = auth.currentUser?.uid ?: "local_student",
            taskTitle = taskTitle,
            durationMinutes = minutes,
            xpEarned = xp,
            timestamp = System.currentTimeMillis()
        )
        _focusSessions.update { listOf(session) + it }
        _userProfile.update {
            it.copy(totalFocusMinutes = it.totalFocusMinutes + minutes)
        }
        addXp(xp)
    }

    fun addXp(amount: Int) {
        _userProfile.update { current ->
            val newXp = current.xp + amount
            val newLevel = when {
                newXp >= 1500 -> 5
                newXp >= 1000 -> 4
                newXp >= 600 -> 3
                newXp >= 300 -> 2
                else -> 1
            }
            val title = when (newLevel) {
                5 -> "Academic Beast"
                4 -> "Achiever"
                3 -> "Consistent"
                2 -> "Learner"
                else -> "Freshman"
            }
            current.copy(xp = newXp, level = newLevel, levelTitle = title)
        }
    }

    // AI Features
    suspend fun planMyDayWithAi(): String {
        val taskTitles = _tasks.value.filter { !it.isCompleted }.joinToString(", ") { "${it.title} (${it.subjectName})" }
        val prompt = "User's pending tasks: $taskTitles. College hours are 9 AM to 4 PM. Available evening study hours: 5 PM to 10 PM. Create an optimal, realistic study schedule for today with focused blocks and 15-minute breaks."
        val result = GeminiClient.generate(prompt, "You are StudyFlow AI, an elite college productivity planner. Return a clean, formatted schedule.")
        val schedule = result.getOrDefault("")
        // Add structured AI tasks if empty
        if (_tasks.value.none { !it.isCompleted }) {
            addTask("AI Recommended: Deep Concept Review", "C Programming", "Focus on pointers and dynamic memory", "Today", 30, "HIGH", "5:00 PM")
            addTask("AI Recommended: Revision & Practice", "Engineering Chemistry", "Phase diagram practice", "Today", 30, "MEDIUM", "6:15 PM")
        }
        addXp(40)
        return schedule
    }

    suspend fun rescueMyDayWithAi(): String {
        val pending = _tasks.value.filter { !it.isCompleted }
        val prompt = "The student fell behind schedule! Unfinished tasks: ${pending.joinToString { it.title }}. Reorganize the remaining evening without stress. Cut down low-priority items and focus on the most critical ones."
        val result = GeminiClient.generate(prompt, "You are StudyFlow AI Rescue Engine. Reassure the student and provide a realistic 2-block recovery plan.")
        addXp(30)
        return result.getOrDefault("")
    }

    suspend fun breakdownTaskWithAi(task: TaskItem): List<Subtask> {
        val prompt = "Break down this college assignment/task into 4 to 6 smaller actionable subtasks: '${task.title}' for subject '${task.subjectName}'. Return each subtask as a bullet point."
        val result = GeminiClient.generate(prompt, "You are StudyFlow AI Task Breakdown Assistant.")
        val text = result.getOrDefault("")
        val lines = text.lines()
            .map { it.trim().removePrefix("•").removePrefix("-").removePrefix("*").trim() }
            .filter { it.isNotBlank() && !it.startsWith("#") && !it.startsWith("📋") }
            .take(6)

        val newSubtasks = if (lines.isNotEmpty()) {
            lines.map { Subtask(id = UUID.randomUUID().toString(), title = it, isCompleted = false) }
        } else {
            listOf(
                Subtask(id = UUID.randomUUID().toString(), title = "Review prompt & requirements", isCompleted = false),
                Subtask(id = UUID.randomUUID().toString(), title = "Draft core points & outline", isCompleted = false),
                Subtask(id = UUID.randomUUID().toString(), title = "Complete main work / calculations", isCompleted = false),
                Subtask(id = UUID.randomUUID().toString(), title = "Verify and review final submission", isCompleted = false)
            )
        }

        _tasks.update { list ->
            list.map { t ->
                if (t.id == task.id) t.copy(subtasks = newSubtasks) else t
            }
        }
        addXp(25)
        syncTasksToFirestore()
        return newSubtasks
    }

    suspend fun sendChatMessage(userText: String): String {
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = "user",
            content = userText,
            timestamp = System.currentTimeMillis()
        )
        _chatMessages.update { it + userMsg }

        val contextInfo = "Student context: 2nd Year, Subjects: ${_subjects.value.joinToString { it.name }}. Tasks remaining: ${_tasks.value.count { !it.isCompleted }}."
        val result = GeminiClient.generate(userText, "You are StudyFlow AI: an encouraging, highly knowledgeable college study companion. $contextInfo Keep answers structured, friendly, concise and practical.")
        val aiReply = result.getOrDefault("I'm here to help you study efficiently! What subject or problem shall we tackle next?")

        val botMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = "assistant",
            content = aiReply,
            timestamp = System.currentTimeMillis()
        )
        _chatMessages.update { it + botMsg }
        addXp(10)
        return aiReply
    }

    fun completeOnboarding(year: String, subjectsList: List<String>, goalsList: List<String>, studyHours: String) {
        _userProfile.update {
            it.copy(
                collegeYear = year,
                goals = goalsList,
                preferredStudyHours = studyHours,
                isOnboarded = true
            )
        }
        val defaultColors = listOf("#6366F1", "#06B6D4", "#8B5CF6", "#10B981", "#F59E0B")
        val newSubs = subjectsList.mapIndexed { idx, name ->
            Subject(
                id = UUID.randomUUID().toString(),
                name = name,
                code = "SUB${101 + idx}",
                colorHex = defaultColors.getOrElse(idx % defaultColors.size) { "#6366F1" }
            )
        }
        if (newSubs.isNotEmpty()) {
            _subjects.value = newSubs
        }
    }

    private fun syncTasksToFirestore() {
        val uid = auth.currentUser?.uid ?: return
        val db = firestore ?: return
        scope.launch {
            try {
                // Background cloud sync
                val userDoc = db.collection("users").document(uid)
                userDoc.set(mapOf("lastSyncedAt" to System.currentTimeMillis(), "tasksCount" to _tasks.value.size))
            } catch (e: Exception) {
                Log.d("StudyFlowRepo", "Firestore silent sync: ${e.message}")
            }
        }
    }
}
