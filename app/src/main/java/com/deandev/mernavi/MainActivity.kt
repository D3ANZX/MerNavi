package com.deandev.mernavi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.deandev.mernavi.ui.theme.MerNaviTheme


object Routes {
    const val LOGIN = "login_screen"
    const val REGISTER = "register_screen"
    const val MAP = "map_screen/{studentId}"

    fun buildMapRoute(studentId: String): String = "map_screen/$studentId"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MerNaviTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val userDao = remember { UserDao(context) }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        // 1. Login Screen Route
        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onLoginClick = { studentId, password ->
                    val isAuthenticated = userDao.authenticateUser(studentId, password)
                    if (isAuthenticated) {
                        Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                        navController.navigate(Routes.buildMapRoute(studentId)) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Invalid Student ID or Password", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }

        // 2. Register Screen Route
        composable(Routes.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onRegisterClick = { studentId, surname, firstName, middleName, password ->
                    val success = userDao.registerUser(
                        student_id = studentId,
                        lastname = surname,
                        firstname = firstName,
                        middlename = middleName,
                        rawPassword = password
                    )

                    if (success) {
                        Toast.makeText(context, "Registration Successful! Please Log In.", Toast.LENGTH_LONG).show()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Registration Failed. Student ID might already exist.", Toast.LENGTH_LONG).show()
                    }
                }
            )
        }

        // 3. Map Dashboard Screen Route
        composable(
            route = Routes.MAP,
            arguments = listOf(navArgument("studentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val studentIdArg = backStackEntry.arguments?.getString("studentId") ?: ""
            val userData = remember(studentIdArg) { userDao.getUserByStudentId(studentIdArg) }

            MapScreen(
                studentId = userData?.studentId ?: studentIdArg,
                userFullName = userData?.fullName ?: "Student User"
            )
        }
    }
}