package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiClient
import com.example.data.model.Achievement
import com.example.data.model.ChatMessage
import com.example.data.model.ExamItem
import com.example.data.model.FocusSession
import com.example.data.model.Subject
import com.example.data.model.TaskItem
import com.example.data.model.UserProfile
import com.example.data.repository.StudyFlowRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    PLANNER,
    FOCUS,
    EXAMS,
    AI_CHAT,
    AI_TOOLS,
    INSIGHTS,
    SOCIAL,
    SETTINGS
}

class StudyFlowViewModel(application: Application) : AndroidViewModel(application) {
    val repository = StudyFlowRepository(application)

    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val subjects: StateFlow<List<Subject>> = repository.subjects
    val tasks: StateFlow<List<TaskItem>> = repository.tasks
    val exams: StateFlow<List<ExamItem>> = repository.exams
    val focusSessions: StateFlow<List<FocusSession>> = repository.focusSessions
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages
    val achievements: StateFlow<List<Achievement>> = repository.achievements

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Screen navigation stack for proper back handling
    private val backStack = mutableListOf(AppScreen.HOME)

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            backStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun handleBack(): Boolean {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
            _currentScreen.value = backStack.last()
            return true
        }
        return false
    }

    // AI Planning UI State
    private val _isAiPlanning = MutableStateFlow(false)
    val isAiPlanning: StateFlow<Boolean> = _isAiPlanning.asStateFlow()

    private val _aiPlanResult = MutableStateFlow<String?>(null)
    val aiPlanResult: StateFlow<String?> = _aiPlanResult.asStateFlow()

    fun dismissAiPlanResult() {
        _aiPlanResult.value = null
    }

    fun planMyDay() {
        viewModelScope.launch {
            _isAiPlanning.value = true
            val plan = repository.planMyDayWithAi()
            _isAiPlanning.value = false
            _aiPlanResult.value = plan
        }
    }

    fun rescueMyDay() {
        viewModelScope.launch {
            _isAiPlanning.value = true
            val plan = repository.rescueMyDayWithAi()
            _isAiPlanning.value = false
            _aiPlanResult.value = plan
        }
    }

    fun breakdownTask(task: TaskItem) {
        viewModelScope.launch {
            _isAiPlanning.value = true
            repository.breakdownTaskWithAi(task)
            _isAiPlanning.value = false
        }
    }

    // Focus Timer State
    private val _timerSecondsRemaining = MutableStateFlow(25 * 60)
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _focusTaskTitle = MutableStateFlow("Practice C programming loops")
    val focusTaskTitle: StateFlow<String> = _focusTaskTitle.asStateFlow()

    private val _showCelebrationDialog = MutableStateFlow(false)
    val showCelebrationDialog: StateFlow<Boolean> = _showCelebrationDialog.asStateFlow()

    private var timerJob: Job? = null

    fun selectFocusTask(title: String) {
        _focusTaskTitle.value = title
    }

    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_timerSecondsRemaining.value > 0 && _isTimerRunning.value) {
                delay(1000L)
                _timerSecondsRemaining.update { it - 1 }
            }
            if (_timerSecondsRemaining.value == 0) {
                finishTimer()
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        pauseTimer()
        _timerSecondsRemaining.value = 25 * 60
    }

    fun finishTimer() {
        pauseTimer()
        val minutesCompleted = (1500 - _timerSecondsRemaining.value) / 60
        val actualMinutes = if (minutesCompleted < 1) 25 else minutesCompleted
        repository.logFocusSession(_focusTaskTitle.value, actualMinutes)
        _timerSecondsRemaining.value = 25 * 60
        _showCelebrationDialog.value = true
    }

    fun dismissCelebration() {
        _showCelebrationDialog.value = false
    }

    // AI Tools State
    private val _toolQuery = MutableStateFlow("")
    val toolQuery: StateFlow<String> = _toolQuery.asStateFlow()

    private val _toolResult = MutableStateFlow<String?>(null)
    val toolResult: StateFlow<String?> = _toolResult.asStateFlow()

    private val _isToolLoading = MutableStateFlow(false)
    val isToolLoading: StateFlow<Boolean> = _isToolLoading.asStateFlow()

    fun updateToolQuery(text: String) {
        _toolQuery.value = text
    }

    fun executeStudyTool(promptPrefix: String, userInput: String) {
        if (userInput.isBlank()) return
        viewModelScope.launch {
            _isToolLoading.value = true
            val fullPrompt = "$promptPrefix: $userInput"
            val result = GeminiClient.generate(fullPrompt, "You are StudyFlow AI Study Tools Engine. Format with clear markdown, bullet points, and high readability.")
            _toolResult.value = result.getOrDefault("")
            _isToolLoading.value = false
            repository.addXp(20)
        }
    }

    fun clearToolResult() {
        _toolResult.value = null
        _toolQuery.value = ""
    }

    // Chat
    private val _chatInput = MutableStateFlow("")
    val chatInput: StateFlow<String> = _chatInput.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    fun updateChatInput(text: String) {
        _chatInput.value = text
    }

    fun sendChatMessage() {
        val query = _chatInput.value.trim()
        if (query.isEmpty() || _isChatLoading.value) return
        _chatInput.value = ""
        viewModelScope.launch {
            _isChatLoading.value = true
            repository.sendChatMessage(query)
            _isChatLoading.value = false
        }
    }

    fun sendSuggestedPrompt(prompt: String) {
        _chatInput.value = prompt
        sendChatMessage()
    }

    // Social share card modal
    private val _showShareCard = MutableStateFlow(false)
    val showShareCard: StateFlow<Boolean> = _showShareCard.asStateFlow()

    fun toggleShareCard(show: Boolean) {
        _showShareCard.value = show
    }
}
