package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TaskCardItem
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.StudyFlowViewModel

@Composable
fun PlannerScreen(
    viewModel: StudyFlowViewModel,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val isAiPlanning by viewModel.isAiPlanning.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabLabels = listOf("Today", "Tomorrow", "This Week")

    var showAddTaskDialog by remember { mutableStateOf(false) }

    // Dialog state
    var taskTitle by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var taskSubject by remember { mutableStateOf(subjects.firstOrNull()?.name ?: "C Programming") }
    var taskPriority by remember { mutableStateOf("MEDIUM") }
    var taskDuration by remember { mutableStateOf("30") }
    var taskTime by remember { mutableStateOf("5:00 PM") }

    val filteredTasks = when (selectedTabIndex) {
        0 -> tasks.filter { it.scheduledDate == "Today" }
        1 -> tasks.filter { it.scheduledDate == "Tomorrow" || it.deadline == "Tomorrow" }
        else -> tasks
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = BrandPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_task_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Title & Plan Week Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Smart Planner",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${filteredTasks.count { !it.isCompleted }} pending tasks",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { viewModel.planMyDay() },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isAiPlanning,
                    modifier = Modifier.testTag("smart_plan_button")
                ) {
                    if (isAiPlanning) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("✨ Plan My Day", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tabs: Today / Tomorrow / This Week
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                tabLabels.forEachIndexed { index, label ->
                    SegmentedButton(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = tabLabels.size)
                    ) {
                        Text(label, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = BrandSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Your schedule is clear ✨",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tap '+ Add Task' or let AI plan your day!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = { viewModel.planMyDay() },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate Smart Schedule")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskCardItem(
                            task = task,
                            onToggleCompletion = { viewModel.repository.toggleTaskCompletion(task.id) },
                            onBreakdown = { viewModel.breakdownTask(task) },
                            onStartFocus = {
                                viewModel.selectFocusTask(task.title)
                                onNavigate(AppScreen.FOCUS)
                            },
                            onDelete = { viewModel.repository.deleteTask(task.id) },
                            onToggleSubtask = { stId -> viewModel.repository.toggleSubtask(task.id, stId) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Add Task Dialog
    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("Add Study Task") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        label = { Text("Task Title *") },
                        placeholder = { Text("e.g. Complete Chemistry Report") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("task_title_input")
                    )

                    OutlinedTextField(
                        value = taskSubject,
                        onValueChange = { taskSubject = it },
                        label = { Text("Subject") },
                        placeholder = { Text("e.g. C Programming, Chemistry") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = taskDescription,
                        onValueChange = { taskDescription = it },
                        label = { Text("Notes / Description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = taskDuration,
                            onValueChange = { taskDuration = it },
                            label = { Text("Duration (min)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = taskTime,
                            onValueChange = { taskTime = it },
                            label = { Text("Time") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Priority:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        listOf("LOW", "MEDIUM", "HIGH").forEach { p ->
                            Button(
                                onClick = { taskPriority = p },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (taskPriority == p) BrandPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (taskPriority == p) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(p, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (taskTitle.isNotBlank()) {
                            viewModel.repository.addTask(
                                title = taskTitle,
                                subjectName = taskSubject,
                                description = taskDescription,
                                estimatedMinutes = taskDuration.toIntOrNull() ?: 30,
                                priority = taskPriority,
                                scheduledTime = taskTime
                            )
                            showAddTaskDialog = false
                            taskTitle = ""
                            taskDescription = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    modifier = Modifier.testTag("save_task_btn")
                ) {
                    Text("Save Task")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
