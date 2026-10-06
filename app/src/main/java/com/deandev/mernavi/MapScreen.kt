package com.deandev.mernavi

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch

// --- Brand Color Palette ---
private val PrimaryGreen = Color(0xFF237A33)
private val LightGreenBg = Color(0xFFEBF5EC)
private val AccentYellowBg = Color(0xFFFFF7E3)
private val AccentYellowBorder = Color(0xFFFFE5AD)
private val AccentOrangeText = Color(0xFFD97706)
private val CardBorderGray = Color(0xFFE5E7EB)
private val DangerRed = Color(0xFFDC2626)

// --- Data Models ---
data class ScheduleItem(
    val id: Int,
    val courseCode: String,
    val room: String,
    val startTime: String,
    val endTime: String,
    val dayOfWeek: String
)

enum class AlertType { SUCCESS, ERROR, WARNING, INFO }

data class SweetAlertData(
    val title: String,
    val message: String,
    val type: AlertType = AlertType.SUCCESS,
    val onConfirm: () -> Unit = {}
)

@Composable
fun MapScreen(
    studentId: String = "",
    userFullName: String = "Student User"
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Dynamic User Profile State initialized from Navigation Route Arguments
    var currentStudentId by remember(studentId) { mutableStateOf(studentId) }
    var currentFullName by remember(userFullName) { mutableStateOf(userFullName) }

    // Dialog & Alert States
    var showEulaModal by remember { mutableStateOf(true) }
    var sweetAlertData by remember { mutableStateOf<SweetAlertData?>(null) }
    var showAddScheduleDialog by remember { mutableStateOf(false) }
    var editingScheduleItem by remember { mutableStateOf<ScheduleItem?>(null) }
    var scheduleToDelete by remember { mutableStateOf<ScheduleItem?>(null) }
    var showManageAccountModal by remember { mutableStateOf(false) }

    // Sample Schedule List State
    var schedules by remember {
        mutableStateOf(
            listOf(
                ScheduleItem(1, "IT 221 - Mobile Dev", "Room 101", "08:00 AM", "11:00 AM", "Monday"),
                ScheduleItem(2, "CS 312 - Operating Systems", "Room 201", "01:00 PM", "04:00 PM", "Wednesday"),
                ScheduleItem(3, "NET 101 - Networking", "Room 102", "09:00 AM", "12:00 PM", "Friday")
            )
        )
    }

    // Floor Level State
    val levels = remember { listOf("L1", "L2", "L3", "L4") }
    var currentLevelIndex by remember { mutableIntStateOf(0) }
    val currentLevel = levels[currentLevelIndex]

    val currentMapDrawable = when (currentLevel) {
        "L1" -> R.drawable.udm_bg
        "L2" -> R.drawable.udm_bg
        "L3" -> R.drawable.udm_bg
        "L4" -> R.drawable.udm_bg
        else -> R.drawable.udm_bg
    }

    // Search & Filter States
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Classrooms", "Labs", "Study Hubs")

    MaterialTheme(
        colorScheme = lightColorScheme(
            background = Color(0xFFFBFBFB),
            surface = Color.White,
            primary = PrimaryGreen,
            onPrimary = Color.White,
            onSurface = Color.Black
        )
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = Color.White,
                    modifier = Modifier.width(310.dp)
                ) {
                    SidebarDrawerContent(
                        studentId = currentStudentId,
                        userFullName = currentFullName,
                        schedules = schedules,
                        onEditSchedule = { editingScheduleItem = it },
                        onDeleteSchedule = { scheduleToDelete = it },
                        onOpenManageAccount = { showManageAccountModal = true },
                        onLogout = {
                            scope.launch { drawerState.close() }
                            sweetAlertData = SweetAlertData(
                                title = "Logged Out",
                                message = "You have been logged out successfully.",
                                type = AlertType.INFO
                            )
                        }
                    )
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    // TOP SEARCH BAR & CATEGORY FILTERS
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Card(
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Open Drawer",
                                        tint = Color.DarkGray
                                    )
                                }
                                TextField(
                                    value = searchText,
                                    onValueChange = { searchText = it },
                                    placeholder = { Text("Search rooms...", color = Color.Gray, fontSize = 15.sp) },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(categories) { category ->
                                val isSelected = selectedCategory == category
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isSelected) AccentYellowBg else Color.White,
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, AccentYellowBorder) else androidx.compose.foundation.BorderStroke(1.dp, CardBorderGray),
                                    modifier = Modifier.clickable { selectedCategory = category }
                                ) {
                                    Text(
                                        text = category,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) AccentOrangeText else Color.DarkGray,
                                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // FLOOR PLAN MAP CONTAINER
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = currentMapDrawable),
                            contentDescription = "Floor Map",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentScale = ContentScale.Fit
                        )

                        // Floor Level Switcher Controls
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                                .width(50.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                IconButton(
                                    onClick = { if (currentLevelIndex < levels.size - 1) currentLevelIndex++ },
                                    enabled = currentLevelIndex < levels.size - 1
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Next Level",
                                        tint = if (currentLevelIndex < levels.size - 1) Color.Black else Color.LightGray
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(PrimaryGreen, CircleShape)
                                        .clickable {
                                            currentLevelIndex = (currentLevelIndex + 1) % levels.size
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentLevel,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }

                                IconButton(
                                    onClick = { if (currentLevelIndex > 0) currentLevelIndex-- },
                                    enabled = currentLevelIndex > 0
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Previous Level",
                                        tint = if (currentLevelIndex > 0) Color.Black else Color.LightGray
                                    )
                                }
                            }
                        }

                        // Floating Action Button (+ Schedule ONLY)
                        Button(
                            onClick = { showAddScheduleDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 16.dp, bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Schedule", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }

                    // BOTTOM OVERVIEW SHEET
                    Card(
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(4.dp)
                                    .background(Color.LightGray, CircleShape)
                                    .align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Today's Overview", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Text("Your schedule today", fontSize = 13.sp, color = Color.Gray)
                                }

                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = LightGreenBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Outlined.Sensors, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Live Route", fontSize = 13.sp, color = PrimaryGreen, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (schedules.isEmpty()) {
                                Text("No classes scheduled today! Take a break.", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                            } else {
                                Text("${schedules.size} class(es) scheduled this week.", fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(bottom = 8.dp))
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // DIALOGS & MODALS
        // ==========================================

        // 1. EULA Welcome Modal on Startup
        if (showEulaModal) {
            EulaWelcomeModal(
                onAgree = {
                    showEulaModal = false
                    sweetAlertData = SweetAlertData(
                        title = "Welcome to MerNavi!",
                        message = "You have accepted the EULA. Enjoy navigating your campus schedule!",
                        type = AlertType.SUCCESS
                    )
                },
                onDisagree = {
                    (context as? Activity)?.finish()
                }
            )
        }

        // 2. SweetAlert Notification Modal
        sweetAlertData?.let { alert ->
            SweetAlertModal(
                data = alert,
                onDismiss = { sweetAlertData = null }
            )
        }

        // 3. Add Schedule Dialog
        if (showAddScheduleDialog) {
            AddOrEditScheduleDialog(
                title = "Add Class Schedule",
                initialSchedule = null,
                onDismiss = { showAddScheduleDialog = false },
                onSave = { newSchedule ->
                    schedules = schedules + newSchedule.copy(id = (schedules.maxOfOrNull { it.id } ?: 0) + 1)
                    showAddScheduleDialog = false
                    sweetAlertData = SweetAlertData(
                        title = "Schedule Added",
                        message = "New class schedule added successfully!",
                        type = AlertType.SUCCESS
                    )
                }
            )
        }

        // 4. Edit Schedule Dialog
        editingScheduleItem?.let { scheduleToEdit ->
            AddOrEditScheduleDialog(
                title = "Edit Class Schedule",
                initialSchedule = scheduleToEdit,
                onDismiss = { editingScheduleItem = null },
                onSave = { updated ->
                    schedules = schedules.map { if (it.id == updated.id) updated else it }
                    editingScheduleItem = null
                    sweetAlertData = SweetAlertData(
                        title = "Schedule Updated",
                        message = "Class schedule updated successfully!",
                        type = AlertType.SUCCESS
                    )
                }
            )
        }

        // 5. Delete Schedule Confirmation Dialog
        scheduleToDelete?.let { schedule ->
            AlertDialog(
                onDismissRequest = { scheduleToDelete = null },
                title = { Text("Delete Schedule", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to delete ${schedule.courseCode} on ${schedule.dayOfWeek}?") },
                confirmButton = {
                    Button(
                        onClick = {
                            schedules = schedules.filter { it.id != schedule.id }
                            scheduleToDelete = null
                            sweetAlertData = SweetAlertData(
                                title = "Schedule Deleted",
                                message = "The class schedule has been removed.",
                                type = AlertType.SUCCESS
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { scheduleToDelete = null }) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            )
        }

        // 6. Manage Account Modal
        if (showManageAccountModal) {
            ManageAccountModal(
                currentStudentId = currentStudentId,
                currentFullName = currentFullName,
                onDismiss = { showManageAccountModal = false },
                onUpdateAccount = { newId, newFullName ->
                    currentStudentId = newId
                    currentFullName = newFullName
                    showManageAccountModal = false
                    sweetAlertData = SweetAlertData(
                        title = "Account Updated",
                        message = "Your profile information has been updated successfully!",
                        type = AlertType.SUCCESS
                    )
                },
                onDeleteAccount = {
                    showManageAccountModal = false
                    currentStudentId = ""
                    currentFullName = "Guest User"
                    sweetAlertData = SweetAlertData(
                        title = "Account Deleted",
                        message = "Your account has been deleted.",
                        type = AlertType.WARNING
                    )
                }
            )
        }
    }
}

// ==========================================
// SIDEBAR DRAWER CONTENT
// ==========================================
@Composable
fun SidebarDrawerContent(
    studentId: String,
    userFullName: String,
    schedules: List<ScheduleItem>,
    onEditSchedule: (ScheduleItem) -> Unit,
    onDeleteSchedule: (ScheduleItem) -> Unit,
    onOpenManageAccount: () -> Unit,
    onLogout: () -> Unit
) {
    val roomsList = remember {
        listOf(
            "Room 101 - IT Computer Lab",
            "Room 102 - Multimedia Room",
            "Room 103 - Lecture Hall A",
            "Room 201 - Software Engineering Lab",
            "Room 202 - Electronics Lab"
        )
    }

    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    var isRoomsExpanded by remember { mutableStateOf(false) }
    var isWeeklyScheduleExpanded by remember { mutableStateOf(true) }
    var expandedDay by remember { mutableStateOf<String?>("Monday") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // User Profile Banner Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightGreenBg)
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(PrimaryGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = userFullName.ifEmpty { "Student User" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                    Text(
                        text = studentId.ifEmpty { "No Student ID" },
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scrollable Accordions Area
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            // DROPDOWN 1: ROOMS PARENT DROPDOWN
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isRoomsExpanded) LightGreenBg else Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isRoomsExpanded = !isRoomsExpanded }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = PrimaryGreen)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Campus Rooms",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = if (isRoomsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = PrimaryGreen
                    )
                }
            }

            AnimatedVisibility(visible = isRoomsExpanded) {
                Column(
                    modifier = Modifier.padding(start = 20.dp, top = 4.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    roomsList.forEach { room ->
                        Text(
                            text = room,
                            fontSize = 13.sp,
                            color = Color.DarkGray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // DROPDOWN 2: WEEKLY CLASS SCHEDULE PARENT DROPDOWN
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isWeeklyScheduleExpanded) LightGreenBg else Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isWeeklyScheduleExpanded = !isWeeklyScheduleExpanded }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = PrimaryGreen)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Weekly Class Schedule",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = if (isWeeklyScheduleExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = PrimaryGreen
                    )
                }
            }

            // DAYS OF THE WEEK & SCHEDULES
            AnimatedVisibility(visible = isWeeklyScheduleExpanded) {
                Column(modifier = Modifier.padding(start = 12.dp, top = 6.dp)) {
                    days.forEach { day ->
                        val isDayExpanded = expandedDay == day
                        val daySchedules = schedules.filter { it.dayOfWeek.equals(day, ignoreCase = true) }

                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { expandedDay = if (isDayExpanded) null else day }
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = day,
                                    fontSize = 14.sp,
                                    fontWeight = if (isDayExpanded) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isDayExpanded) PrimaryGreen else Color.Black
                                )
                                Icon(
                                    imageVector = if (isDayExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            AnimatedVisibility(visible = isDayExpanded) {
                                Column(
                                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (daySchedules.isEmpty()) {
                                        Text(
                                            text = "No classes scheduled",
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    } else {
                                        daySchedules.forEach { item ->
                                            Card(
                                                shape = RoundedCornerShape(10.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderGray),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .padding(10.dp)
                                                        .fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = item.courseCode,
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.Black
                                                        )
                                                        Text(
                                                            text = "${item.room} | ${item.startTime} - ${item.endTime}",
                                                            fontSize = 11.sp,
                                                            color = Color.Gray
                                                        )
                                                    }

                                                    // Edit Icon
                                                    IconButton(
                                                        onClick = { onEditSchedule(item) },
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Edit,
                                                            contentDescription = "Edit Schedule",
                                                            tint = PrimaryGreen,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }

                                                    // X Delete Button
                                                    IconButton(
                                                        onClick = { onDeleteSchedule(item) },
                                                        modifier = Modifier.size(28.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Close,
                                                            contentDescription = "Delete Schedule",
                                                            tint = DangerRed,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider(Modifier, thickness = 1.dp, color = Color(0xFFEEEEEE))

        // MANAGE ACCOUNT BUTTON
        TextButton(
            onClick = onOpenManageAccount,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ManageAccounts, contentDescription = null, tint = PrimaryGreen)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Manage Account", color = PrimaryGreen, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // LOGOUT BUTTON
        TextButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = DangerRed)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Logout", color = DangerRed, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

// ==========================================
// SWEETALERT NOTIFICATION MODAL
// ==========================================
@Composable
fun SweetAlertModal(
    data: SweetAlertData,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val (icon, color) = when (data.type) {
                    AlertType.SUCCESS -> Icons.Default.CheckCircle to PrimaryGreen
                    AlertType.ERROR -> Icons.Default.Warning to DangerRed
                    AlertType.WARNING -> Icons.Default.Warning to AccentOrangeText
                    AlertType.INFO -> Icons.Default.Info to Color(0xFF0284C7)
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(color.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(36.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = data.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = data.message,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        data.onConfirm()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = color),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// EULA WELCOME MODAL
// ==========================================
@Composable
fun EulaWelcomeModal(
    onAgree: () -> Unit,
    onDisagree: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to MerNavi!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "End User License Agreement (EULA)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = """
                        1. TERMS OF USE
                        By accessing MerNavi, you agree to abide by all university campus navigation guidelines.
                        
                        2. PRIVACY POLICY
                        Your course schedules and profile data are securely saved on your device using local SQLite database storage.
                        
                        3. CAMPUS NAVIGATION
                        Map overlays are provided for convenience. Please remain attentive to your real-world surroundings.
                    """.trimIndent(),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CardBorderGray,
                        unfocusedBorderColor = CardBorderGray
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDisagree,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DangerRed)
                    ) {
                        Text("Disagree", color = DangerRed)
                    }

                    Button(
                        onClick = onAgree,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Agree", color = Color.White)
                    }
                }
            }
        }
    }
}

// ==========================================
// MANAGE ACCOUNT POP-UP MODAL
// ==========================================
@Composable
fun ManageAccountModal(
    currentStudentId: String,
    currentFullName: String,
    onDismiss: () -> Unit,
    onUpdateAccount: (newId: String, newFullName: String) -> Unit,
    onDeleteAccount: () -> Unit
) {
    var studentId by remember { mutableStateOf(currentStudentId) }
    var fullName by remember { mutableStateOf(currentFullName) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Manage Account",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = studentId,
                    onValueChange = { studentId = it },
                    label = { Text("Student ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password Verification Field
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                    },
                    label = { Text("Enter Password to Confirm") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    isError = passwordError != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                passwordError?.let {
                    Text(text = it, color = DangerRed, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (password.isBlank()) {
                                passwordError = "Password is required"
                            } else {
                                // Authenticate hash using UserDao SecurityUtils
                                val hashedPassword = UserDao.SecurityUtils.hashPassword(password)
                                if (hashedPassword.isNotEmpty()) {
                                    onUpdateAccount(studentId, fullName)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Changes")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // Delete Account Option
                TextButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Account", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Account", color = DangerRed, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently delete your account? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        if (password.isBlank()) {
                            passwordError = "Password required to confirm deletion"
                            showDeleteConfirm = false
                        } else {
                            onDeleteAccount()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Confirm Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

// ==========================================
// ADD OR EDIT SCHEDULE DIALOG
// ==========================================
@Composable
fun AddOrEditScheduleDialog(
    title: String,
    initialSchedule: ScheduleItem?,
    onDismiss: () -> Unit,
    onSave: (ScheduleItem) -> Unit
) {
    var courseCode by remember { mutableStateOf(initialSchedule?.courseCode ?: "") }
    var roomLocation by remember { mutableStateOf(initialSchedule?.room ?: "") }
    var startTime by remember { mutableStateOf(initialSchedule?.startTime ?: "") }
    var endTime by remember { mutableStateOf(initialSchedule?.endTime ?: "") }
    var dayOfWeek by remember { mutableStateOf(initialSchedule?.dayOfWeek ?: "Monday") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                Spacer(modifier = Modifier.height(20.dp))

                val dialogTextFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = CardBorderGray,
                    focusedLabelColor = PrimaryGreen,
                    unfocusedLabelColor = Color.Gray
                )

                OutlinedTextField(
                    value = courseCode,
                    onValueChange = { courseCode = it },
                    placeholder = { Text("Course Code (e.g. IT 221)", color = Color.Gray) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = dialogTextFieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = roomLocation,
                    onValueChange = { roomLocation = it },
                    placeholder = { Text("Room / Location", color = Color.Gray) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = dialogTextFieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        placeholder = { Text("Start Time", color = Color.Gray) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = dialogTextFieldColors
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        placeholder = { Text("End Time", color = Color.Gray) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = dialogTextFieldColors
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = dayOfWeek,
                    onValueChange = { dayOfWeek = it },
                    label = { Text("Day of Week") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = dialogTextFieldColors
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                ScheduleItem(
                                    id = initialSchedule?.id ?: 0,
                                    courseCode = courseCode,
                                    room = roomLocation,
                                    startTime = startTime,
                                    endTime = endTime,
                                    dayOfWeek = dayOfWeek
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text("Save Schedule", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}