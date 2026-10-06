package com.deandev.mernavi

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun RegisterScreen(
    onNavigateToLogin: () -> Unit = {},
    onRegisterClick: (studentId: String, surname: String, firstName: String, middleName: String, password: String) -> Unit = { _, _, _, _, _ -> }
) {
    // Force Light Mode theme settings across the entire composable hierarchy
    MaterialTheme(
        colorScheme = lightColorScheme(
            background = Color.White,
            surface = Color(0xFFECECEC),
            onSurface = Color.Black,
            primary = Color(0xFF0052CC),
            onPrimary = Color.White
        )
    ) {
        var studentId by remember { mutableStateOf("") }
        var surname by remember { mutableStateOf("") }
        var firstName by remember { mutableStateOf("") }
        var middleName by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        val scrollState = rememberScrollState()

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

            // 3. Scrollable Center Container (Logo + Card)
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .align(Alignment.Center)
                    .padding(vertical = 32.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Merlions Pin Logo above Card
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "Merlions Logo",
                    modifier = Modifier
                        .size(85.dp)
                        .padding(bottom = 10.dp),
                    contentScale = ContentScale.Fit
                )

                // Main Registration Form Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFECECEC) // Light gray card surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title
                        Text(
                            text = "Create Account",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Unified Input Colors for Light Mode
                        val textFieldColors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF8F9FA),
                            unfocusedContainerColor = Color(0xFFF8F9FA),
                            focusedBorderColor = Color(0xFF0052CC),
                            unfocusedBorderColor = Color(0xFFCCCCCC),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color(0xFF0052CC),
                            focusedLabelColor = Color(0xFF0052CC),
                            unfocusedLabelColor = Color.Gray
                        )

                        // 1. Student ID No. Field
                        OutlinedTextField(
                            value = studentId,
                            onValueChange = { studentId = it },
                            label = { Text("Student ID No.") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 2. Surname Field
                        OutlinedTextField(
                            value = surname,
                            onValueChange = { surname = it },
                            label = { Text("Surname") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 3. First Name (Given) Field
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("First Name (Given)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 4. Middle Name Field
                        OutlinedTextField(
                            value = middleName,
                            onValueChange = { middleName = it },
                            label = { Text("Middle Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 5. Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 6. Confirm Password Field
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Pill-shaped Blue Button
                        Button(
                            onClick = {
                                onRegisterClick(
                                    studentId,
                                    surname,
                                    firstName,
                                    middleName,
                                    password
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0052CC)
                            )
                        ) {
                            Text(
                                text = "Register",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Bottom Sign In Link
                        TextButton(onClick = onNavigateToLogin) {
                            Text(
                                text = "Already have account? Sign in",
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