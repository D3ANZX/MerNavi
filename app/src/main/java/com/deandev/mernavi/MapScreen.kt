package com.deandev.mernavi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
private val SurfaceGray = Color(0xFFF8F9FA)
private val CardBorderGray = Color(0xFFE5E7EB)

@Composable
fun MapScreen() {
    // Force Light Mode Theme
    MaterialTheme(
        colorScheme = lightColorScheme(
            background = Color(0xFFFBFBFB),
            surface = Color.White,
            primary = PrimaryGreen,
            onPrimary = Color.White,
            onSurface = Color.Black
        )
    ) {
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        // --- Floor Level State ---
        val levels = remember { listOf("L1", "L2", "L3", "L4") }
        var currentLevelIndex by remember { mutableIntStateOf(0) }
        val currentLevel = levels[currentLevelIndex]

        // Map Image Resolver (Swap R.drawable.udm_bg with your actual floor map images)
        val currentMapDrawable = when (currentLevel) {
            "L1" -> R.drawable.udm_bg // TODO: Replace with R.drawable.map_l1
            "L2" -> R.drawable.udm_bg // TODO: Replace with R.drawable.map_l2
            "L3" -> R.drawable.udm_bg // TODO: Replace with R.drawable.map_l3
            "L4" -> R.drawable.udm_bg // TODO: Replace with R.drawable.map_l4
            else -> R.drawable.udm_bg
        }

        // --- Dialog States ---
        var showAddScheduleDialog by remember { mutableStateOf(false) }
        var showAddCourseDialog by remember { mutableStateOf(false) }

        // --- Search & Filter States ---
        var searchText by remember { mutableStateOf("") }
        var selectedCategory by remember { mutableStateOf("All") }
        val categories = listOf("All", "Classrooms", "Labs", "Study Hubs")

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = Color.White,
                    modifier = Modifier.width(300.dp)
                ) {
                    SidebarDrawerContent()
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
                    // 1. TOP SEARCH BAR & CATEGORY FILTERS
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // Search Card
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
                                    placeholder = { Text("Search rooms,", color = Color.Gray, fontSize = 15.sp) },
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

                        // Filter Chips Row
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
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

                    // 2. FLOOR PLAN MAP CONTAINER
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 8.dp)
                    ) {
                        // Map Graphic
                        Image(
                            painter = painterResource(id = currentMapDrawable),
                            contentDescription = "Floor Map",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentScale = ContentScale.Fit
                        )

                        // Floor Level Switcher Controls (Right side floating overlay)
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
                                // Up Floor Button
                                IconButton(
                                    onClick = {
                                        if (currentLevelIndex < levels.size - 1) currentLevelIndex++
                                    },
                                    enabled = currentLevelIndex < levels.size - 1
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Next Level",
                                        tint = if (currentLevelIndex < levels.size - 1) Color.Black else Color.LightGray
                                    )
                                }

                                // Level Selector Trigger Button
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

                                // Down Floor Button
                                IconButton(
                                    onClick = {
                                        if (currentLevelIndex > 0) currentLevelIndex--
                                    },
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

                        // Floating Action Buttons (+ Schedule & Course)
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 16.dp, bottom = 12.dp),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // + Schedule Button
                            Button(
                                onClick = { showAddScheduleDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
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

                            // Course Button
                            Button(
                                onClick = { showAddCourseDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentYellowBg),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AccentYellowBorder),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Bookmark,
                                    contentDescription = null,
                                    tint = AccentOrangeText,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Course", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AccentOrangeText)
                            }
                        }
                    }

                    // 3. BOTTOM OVERVIEW SHEET
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
                            // Drag Handle Indicator
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
                                    Text(
                                        text = "Today's Overview",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "Your schedule today",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }

                                // Live Route Badge
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = LightGreenBg,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Sensors,
                                            contentDescription = null,
                                            tint = PrimaryGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Live Route", fontSize = 13.sp, color = PrimaryGreen, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "No classes today! Take a break.",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- DIALOGS ---
        if (showAddScheduleDialog) {
            AddScheduleDialog(onDismiss = { showAddScheduleDialog = false })
        }

        if (showAddCourseDialog) {
            AddCourseDialog(onDismiss = { showAddCourseDialog = false })
        }
    }
}

// ==========================================
// 1. SIDEBAR NAVIGATION DRAWER CONTENT
// ==========================================
@Composable
fun SidebarDrawerContent() {
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
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
                        text = "Florentino Dean Gas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                    Text(
                        text = "24-22-099",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section Title
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Weekly Class Schedule",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
        }

        HorizontalDivider(Modifier, thickness = 1.dp, color = Color(0xFFEEEEEE))

        // Collapsible Weekly Schedule List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(days) { day ->
                val isExpanded = expandedDay == day

                Column {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isExpanded) LightGreenBg else Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedDay = if (isExpanded) null else day }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = day,
                                fontSize = 15.sp,
                                fontWeight = if (isExpanded) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isExpanded) PrimaryGreen else Color.Black
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = if (isExpanded) PrimaryGreen else Color.Gray
                            )
                        }
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Text(
                            text = "No classes scheduled",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. ADD CLASS SCHEDULE DIALOG POPUP
// ==========================================
@Composable
fun AddScheduleDialog(onDismiss: () -> Unit) {
    var courseCodeLink by remember { mutableStateOf("") }
    var roomLocation by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var dayOfWeek by remember { mutableStateOf("Monday") }

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
                // Top Green Icon Header
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.Transparent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Add Class Schedule",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(20.dp))

                val dialogTextFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = CardBorderGray,
                    focusedLabelColor = PrimaryGreen,
                    unfocusedLabelColor = Color.Gray
                )

                // Course Code Link Dropdown
                OutlinedTextField(
                    value = courseCodeLink,
                    onValueChange = { courseCodeLink = it },
                    placeholder = { Text("Course Code Link", color = Color.Gray) },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = dialogTextFieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Room / Location Input
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

                // Start / End Time Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        placeholder = { Text("Start", color = Color.Gray) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = dialogTextFieldColors
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        placeholder = { Text("End", color = Color.Gray) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = dialogTextFieldColors
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Day of Week Dropdown
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

                // Action Buttons
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
                        onClick = { onDismiss() },
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

// ==========================================
// 3. ADD NEW COURSE DIALOG POPUP
// ==========================================
@Composable
fun AddCourseDialog(onDismiss: () -> Unit) {
    var courseName by remember { mutableStateOf("") }
    var courseCode by remember { mutableStateOf("") }
    var instructor by remember { mutableStateOf("") }

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
                // Top Green Book Icon Header
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(PrimaryGreen, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Book,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Add New Course",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(20.dp))

                val dialogTextFieldColors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = CardBorderGray,
                    focusedLabelColor = PrimaryGreen,
                    unfocusedLabelColor = Color.Gray
                )

                // Course Name
                OutlinedTextField(
                    value = courseName,
                    onValueChange = { courseName = it },
                    placeholder = { Text("Course Name", color = Color.Gray) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = dialogTextFieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Course Code
                OutlinedTextField(
                    value = courseCode,
                    onValueChange = { courseCode = it },
                    placeholder = { Text("Course Code", color = Color.Gray) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = dialogTextFieldColors
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Instructor / Professor
                OutlinedTextField(
                    value = instructor,
                    onValueChange = { instructor = it },
                    placeholder = { Text("Instructor / Professor", color = Color.Gray) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = dialogTextFieldColors
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
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
                        onClick = { onDismiss() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                        shape = CircleShape,
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text("Save Course", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}