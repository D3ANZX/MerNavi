package com.deandev.mernavi

import android.content.ContentValues
import android.content.Context
import java.security.MessageDigest

data class UserData(
    val studentId: String,
    val firstName: String,
    val lastName: String,
    val middleName: String
) {
    val fullName: String
        get() = "${firstName.replaceFirstChar { it.uppercase() }} ${lastName.replaceFirstChar { it.uppercase() }}"
}

class UserDao(context: Context) {

    object SecurityUtils {
        fun hashPassword(password: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }

    private val dbHelper = DatabaseHelper(context)

    // Register new user
    fun registerUser(
        student_id: String,
        lastname: String,
        firstname: String,
        middlename: String,
        rawPassword: String
    ): Boolean {
        val db = dbHelper.writableDatabase
        val hashedPassword = SecurityUtils.hashPassword(rawPassword)

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_STUDENT_ID, student_id)
            put(DatabaseHelper.COLUMN_LASTNAME, lastname)
            put(DatabaseHelper.COLUMN_FIRSTNAME, firstname)
            put(DatabaseHelper.COLUMN_MIDDLENAME, middlename)
            put(DatabaseHelper.COLUMN_PASSWORD, hashedPassword)
        }
        val result = db.insert(DatabaseHelper.TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    // Authenticate user credentials
    fun authenticateUser(student_id: String, rawPassword: String): Boolean {
        val db = dbHelper.readableDatabase
        val hashedPassword = SecurityUtils.hashPassword(rawPassword)
        val query = """
            SELECT ${DatabaseHelper.COLUMN_USER_ID} 
            FROM ${DatabaseHelper.TABLE_USERS} 
            WHERE ${DatabaseHelper.COLUMN_STUDENT_ID} = ? AND ${DatabaseHelper.COLUMN_PASSWORD} = ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(student_id.trim().lowercase(), hashedPassword))
        val isAuthenticated = cursor.count > 0
        cursor.close()
        db.close()

        return isAuthenticated
    }

    // Retrieve user profile information after authentication
    fun getUserByStudentId(student_id: String): UserData? {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT ${DatabaseHelper.COLUMN_STUDENT_ID}, ${DatabaseHelper.COLUMN_FIRSTNAME}, ${DatabaseHelper.COLUMN_LASTNAME}, ${DatabaseHelper.COLUMN_MIDDLENAME}
            FROM ${DatabaseHelper.TABLE_USERS}
            WHERE ${DatabaseHelper.COLUMN_STUDENT_ID} = ?
        """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(student_id.trim().lowercase()))
        var userData: UserData? = null

        if (cursor.moveToFirst()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STUDENT_ID))
            val firstName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FIRSTNAME))
            val lastName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LASTNAME))
            val middleName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MIDDLENAME))
            userData = UserData(id, firstName, lastName, middleName)
        }
        cursor.close()
        db.close()

        return userData
    }
}