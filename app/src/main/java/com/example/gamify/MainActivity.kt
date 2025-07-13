package com.example.gamify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gamify.ui.theme.GamifyTheme

var score by mutableIntStateOf(0)

data class Task(val name: String, var done: Boolean = false)
data class CounterItem(val name: String, var count: Int = 0)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GamifyTheme {
                var selectedItem by remember { mutableIntStateOf(0) }
                val titles = listOf("Daily Quests", "Quests", "Map", "Counter", "Personalize")

                val dailyTasks = remember { mutableStateListOf<Task>() }
                val specialTasks = remember { mutableStateListOf<Task>() }
                val highLevelTasks = remember { mutableStateListOf<Task>() }
                val counters = remember { mutableStateListOf<CounterItem>() }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = titles[selectedItem],
                                    color = White,
                                    fontSize = 32.sp
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.DarkGray
                            )
                        )
                    },
                    bottomBar = {
                        BottomNavigationBar(
                            selectedIndex = selectedItem,
                            onItemSelected = { selectedItem = it }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .background(Color.DarkGray)
                            .fillMaxSize()
                    ) {
                        when (selectedItem) {
                            0 -> DailyQuestsScreen(dailyTasks)
                            1 -> AlarmList(specialTasks, highLevelTasks)
                            2 -> MapScreen()
                            3 -> CounterScreen(counters)
                            4 -> PersonalizeScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    NavigationBar(containerColor = Color(0xFF2C2C2C)) {
        val items = listOf("D", "Q", "M", "C", "P")
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                icon = {
                    Text(
                        text = item,
                        color = if (selectedIndex == index) White else Color.LightGray,
                        fontSize = 20.sp
                    )
                }
            )
        }
    }
}

@Composable
fun DailyQuestsScreen(tasks: MutableList<Task>) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val doneToday = remember { mutableStateListOf<String>() }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFBF40BF))
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = "Daily Quests",
                        color = White,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                    )

                    tasks.filter { it.name !in doneToday }.forEach { task ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            var checked by remember { mutableStateOf(false) }
                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    checked = it
                                    if (it) {
                                        doneToday.add(task.name)
                                        score += 2
                                    }
                                }
                            )
                            Text(
                                text = task.name,
                                color = White,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = { showDeleteDialog = true },
                containerColor = Color.Red,
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text("❌", fontSize = 20.sp, color = White)
            }

            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color.Green,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Text("+", fontSize = 24.sp, color = White)
            }
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onAddTask = { name ->
                tasks.add(Task(name))
                showAddDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Daily Task") },
            text = {
                if (tasks.isEmpty()) {
                    Text("No tasks to delete.")
                } else {
                    Column {
                        tasks.forEach { task ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(task.name, modifier = Modifier.weight(1f))
                                Button(
                                    onClick = {
                                        tasks.remove(task)
                                        showDeleteDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) {
                                    Text("Delete", color = White)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun AlarmList(
    specialTasks: MutableList<Task>,
    highLevelTasks: MutableList<Task>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        QuestCard(title = "Special", color = Color(0xFFB11FB1), scoreValue = 5, tasks = specialTasks)
        QuestCard(title = "High Level", color = Color(0xFF921692), scoreValue = 10, tasks = highLevelTasks)
    }
}

@Composable
fun QuestCard(title: String, color: Color, scoreValue: Int, tasks: MutableList<Task>) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = title,
                fontSize = 20.sp,
                color = White,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(White.copy(alpha = 0.2f))
                    .padding(8.dp)
            ) {
                Column {
                    tasks.filter { !it.done }.forEach { task ->
                        var checked by remember { mutableStateOf(false) }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    checked = it
                                    if (it) {
                                        task.done = true
                                        score += scoreValue
                                    }
                                }
                            )
                            Text(
                                task.name,
                                color = White,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { tasks.remove(task) },
                                modifier = Modifier.size(25.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("❌", fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
                            }
                        }
                    }
                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("+", fontSize = 20.sp)
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddTaskDialog(
            onDismiss = { showDialog = false },
            onAddTask = { name ->
                tasks.add(Task(name))
                showDialog = false
            }
        )
    }
}

@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onAddTask: (String) -> Unit) {
    var taskName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                if (taskName.isNotBlank()) {
                    onAddTask(taskName)
                }
            }) { Text("Add") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("New Task") },
        text = {
            OutlinedTextField(
                value = taskName,
                onValueChange = { taskName = it },
                label = { Text("Task Name") }
            )
        }
    )
}

@Composable
fun CounterScreen(counters: MutableList<CounterItem>) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFBF40BF))
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = "Counters",
                        color = White,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                    )

                    counters.forEachIndexed { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Button(
                                onClick = {
                                    item.count--
                                    counters[index] = item
                                },
                                modifier = Modifier.size(30.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("-", color = White)
                            }

                            Text(
                                item.name,
                                color = White,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                            )

                            Button(
                                onClick = {
                                    item.count++
                                    counters[index] = item
                                },
                                modifier = Modifier.size(30.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("+", color = White)
                            }
                        }

                        Text(
                            "Count: ${item.count}",
                            color = Color.Yellow,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(start = 8.dp, bottom = 8.dp)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            FloatingActionButton(
                onClick = { showDeleteDialog = true },
                containerColor = Color.Red,
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text("❌", fontSize = 20.sp, color = White)
            }

            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color.Green,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Text("+", fontSize = 24.sp, color = White)
            }
        }
    }

    if (showAddDialog) {
        AddCounterDialog(
            onDismiss = { showAddDialog = false },
            onAddCounter = { name ->
                counters.add(CounterItem(name))
                showAddDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Counter") },
            text = {
                if (counters.isEmpty()) {
                    Text("No counters to delete.")
                } else {
                    Column {
                        counters.forEach { counter ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(counter.name, modifier = Modifier.weight(1f))
                                Button(
                                    onClick = {
                                        counters.remove(counter)
                                        showDeleteDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) {
                                    Text("Delete", color = White)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun AddCounterDialog(onDismiss: () -> Unit, onAddCounter: (String) -> Unit) {
    var counterName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                if (counterName.isNotBlank()) {
                    onAddCounter(counterName)
                }
            }) { Text("Add") }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("New Counter") },
        text = {
            OutlinedTextField(
                value = counterName,
                onValueChange = { counterName = it },
                label = { Text("Counter Name") }
            )
        }
    )
}

@Composable
fun MapScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Map screen", color = White, fontSize = 24.sp)
            Spacer(Modifier.height(16.dp))
            Text("Score: $score", color = Color.Yellow, fontSize = 22.sp)
        }
    }
}

@Composable
fun PersonalizeScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Personalize screen", color = White, fontSize = 24.sp)
    }
}
