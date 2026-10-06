package com.deandev.mernavi

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit = {},
    onLoginClick: (String, String) -> Unit = { _, _ -> }
) {
    // Forces Light Mode color scheme for this entire composable hierarchy
    MaterialTheme(
        colorScheme = lightColorScheme(
            background = Color.White,
            surface = Color(0xFFECECEC),
            onSurface = Color.Black,
            primary = Color(0xFF0052CC)
        )
    ) {
        var studentEmployeeNo by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Box(modifier = Modifier.fillMaxSize()) {

            // 1. Full-screen Background Image
            Image(
                painter = painterResource(id = R.drawable.udm_bg),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 2. Translucent Dark Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            )

            // 3. Center Container (Logo + Card)
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Merlions Pin Logo above the Card
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "Merlions Logo",
                    modifier = Modifier
                        .size(90.dp)
                        .padding(bottom = 12.dp),
                    contentScale = ContentScale.Fit
                )

                // Main Form Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFECECEC)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 28.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title
                        Text(
                            text = "Welcome Back",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Student/Employee No Field
                        OutlinedTextField(
                            value = studentEmployeeNo,
                            onValueChange = { studentEmployeeNo = it },
                            placeholder = { Text("Student/Employee No.", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8F9FA),
                                unfocusedContainerColor = Color(0xFFF8F9FA),
                                focusedBorderColor = Color(0xFF0052CC),
                                unfocusedBorderColor = Color(0xFFCCCCCC),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color(0xFF0052CC)
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Password", color = Color.Gray) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8F9FA),
                                unfocusedContainerColor = Color(0xFFF8F9FA),
                                focusedBorderColor = Color(0xFF0052CC),
                                unfocusedBorderColor = Color(0xFFCCCCCC),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color(0xFF0052CC)
                            )
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        // Pill-shaped Blue Button
                        Button(
                            onClick = { onLoginClick(studentEmployeeNo, password) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0052CC)
                            )
                        ) {
                            Text(
                                text = "Log In",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Register Link
                        TextButton(
                            onClick = onNavigateToRegister
                        ) {
                            Text(
                                text = "Don't have an account? Register",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF0052CC)
                            )
                        }
                    }
                }
            }
        }
    }
}