package com.example.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryLight
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.FlameAccent
import com.example.ui.theme.VioletPurple
import com.example.ui.viewmodel.StudyFlowViewModel

data class ToolDefinition(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val promptPrefix: String,
    val placeholder: String
)

@Composable
fun AiToolsScreen(
    viewModel: StudyFlowViewModel,
    modifier: Modifier = Modifier
) {
    val toolResult by viewModel.toolResult.collectAsState()
    val isToolLoading by viewModel.isToolLoading.collectAsState()

    var activeTool by remember { mutableStateOf<ToolDefinition?>(null) }
    var inputQuery by remember { mutableStateOf("") }

    val toolsList = listOf(
        ToolDefinition("1", "Explain Topic", "Beginner-friendly conceptual breakdown", Icons.Default.Lightbulb, BrandPrimary, "Explain this academic topic simply with real-world analogies", "e.g. Pointers in C, Photosynthesis, Bayes Theorem"),
        ToolDefinition("2", "Summarize Notes", "Key bullet points, formulas & highlights", Icons.Default.MenuBook, BrandSecondary, "Summarize these notes into high-impact revision bullets and formulas", "Paste your lecture notes or chapter paragraphs"),
        ToolDefinition("3", "Generate MCQs", "Test yourself with multiple-choice questions", Icons.Default.Quiz, VioletPurple, "Generate 5 multiple choice questions with answers and explanations for", "e.g. Thermodynamics, Data Structures, Indian Constitution"),
        ToolDefinition("4", "Flashcards", "High-yield Q&A cards for active recall", Icons.Default.FlashOn, FlameAccent, "Convert this subject topic into 6 high-yield active recall flashcards (Question & Answer)", "e.g. Cell biology organelle functions"),
        ToolDefinition("5", "Study Plan", "Personalized study timetable & milestone roadmap", Icons.Default.Timeline, EmeraldSuccess, "Create a step-by-step master study plan for", "e.g. Master Linear Algebra in 2 weeks"),
        ToolDefinition("6", "Task Breakdown", "Divide overwhelming assignments into steps", Icons.Default.Task, BrandPrimary, "Deconstruct this assignment into 5 clear chronological micro-steps with time estimates", "e.g. Build an e-commerce database system in SQL"),
        ToolDefinition("7", "Revision Plan", "Spaced repetition strategy before tests", Icons.Default.Speed, VioletPurple, "Create a spaced repetition revision schedule with review intervals for", "e.g. Organic chemistry reactions"),
        ToolDefinition("8", "Explain Code", "Deconstruct complex algorithms line by line", Icons.Default.Code, BrandSecondary, "Explain this code snippet line-by-line in plain English with time complexity", "Paste C, Java, Python or Kotlin code"),
        ToolDefinition("9", "Debug Code", "Find logic bugs, syntax errors & optimize", Icons.Default.AutoAwesome, FlameAccent, "Identify the bugs or edge cases in this code and provide the fixed solution", "Paste buggy code with expected output"),
        ToolDefinition("10", "Practice Questions", "Exam-style analytical problems", Icons.Default.School, BrandPrimary, "Generate 4 realistic university exam practice problems with numericals for", "e.g. Operating System CPU Scheduling algorithms"),
        ToolDefinition("11", "Viva Questions", "Top interview & lab oral examination Q&A", Icons.Default.HelpOutline, EmeraldSuccess, "Generate top 8 viva/oral examination questions that professors ask with concise answers for", "e.g. Digital Electronics logic gates lab"),
        ToolDefinition("12", "Last-Minute Cram", "Ultra high-yield 2-hour survival sheet", Icons.Default.Speed, FlameAccent, "Create a 2-hour high-yield last-minute exam survival sheet with critical definitions for", "e.g. Engineering Economics midterms")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "AI Study Tools ⚡",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "12 dedicated AI accelerators for assignments, coding & exam prep",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(toolsList) { tool ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            activeTool = tool
                            inputQuery = ""
                        }
                        .testTag("tool_card_${tool.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Surface(
                            color = tool.color.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = tool.icon,
                                    contentDescription = null,
                                    tint = tool.color,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = tool.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tool.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }

    // Modal when user taps on a study tool
    if (activeTool != null) {
        val tool = activeTool!!
        AlertDialog(
            onDismissRequest = {
                activeTool = null
                viewModel.clearToolResult()
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(tool.icon, contentDescription = null, tint = tool.color, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(tool.title)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = tool.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = inputQuery,
                        onValueChange = { inputQuery = it },
                        placeholder = { Text(tool.placeholder) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tool_input_field"),
                        minLines = 3,
                        maxLines = 6
                    )

                    if (isToolLoading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = tool.color, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating response...", fontSize = 12.sp)
                        }
                    }

                    if (toolResult != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            LazyColumn(modifier = Modifier.height(200.dp).padding(10.dp)) {
                                item {
                                    Text(
                                        text = toolResult ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.executeStudyTool(tool.promptPrefix, inputQuery)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = tool.color),
                    enabled = inputQuery.isNotBlank() && !isToolLoading,
                    modifier = Modifier.testTag("run_tool_btn")
                ) {
                    Text("Generate")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    activeTool = null
                    viewModel.clearToolResult()
                }) {
                    Text("Close")
                }
            }
        )
    }
}
