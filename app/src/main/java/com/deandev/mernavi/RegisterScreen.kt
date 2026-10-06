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
        var validationError by remember { mutableStateOf("") }

        val scrollState = rememberScrollState()

        Box(modifier = Modifier.fillMaxSize()) {

            Image(
                painter = painterResource(id = R.drawable.udm_bg),
                contentDescription = "Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .align(Alignment.Center)
                    .padding(vertical = 32.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "Merlions Logo",
                    modifier = Modifier
                        .size(85.dp)
                        .padding(bottom = 10.dp),
                    contentScale = ContentScale.Fit
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFECECEC)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Create Account",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (validationError.isNotEmpty()) {
                            Text(
                                text = validationError,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

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

                        // Input fields
                        OutlinedTextField(
                            value = studentId,
                            onValueChange = { studentId = it; validationError = "" },
                            label = { Text("Student ID No.") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = surname,
                            onValueChange = { surname = it; validationError = "" },
                            label = { Text("Surname") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it; validationError = "" },
                            label = { Text("First Name (Given)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = middleName,
                            onValueChange = { middleName = it; validationError = "" },
                            label = { Text("Middle Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; validationError = "" },
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it; validationError = "" },
                            label = { Text("Confirm Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                when {
                                    studentId.isBlank() || surname.isBlank() || firstName.isBlank() || password.isBlank() -> {
                                        validationError = "Please fill in all required fields"
                                    }
                                    password != confirmPassword -> {
                                        validationError = "Passwords do not match"
                                    }
                                    else -> {
                                        onRegisterClick(
                                            studentId.trim(),
                                            surname.trim(),
                                            firstName.trim(),
                                            middleName.trim(),
                                            password
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0052CC))
                        ) {
                            Text(
                                text = "Register",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

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