package com.deandev.mernavi
import android.content.ContentValues
import android.content.Context
import java.security.MessageDigest
class userDao(context: Context) {
    object SecurityUtils {
        fun hashPassword(password: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
    private val dbHelper = DatabaseHelper(context)

    //Register user
    fun registerUser(student_id: String, lastname: String, firstname: String, middlename:String, rawPassword: String): Boolean{
        val db = dbHelper.writableDatabase
        val hashedPassword = SecurityUtils.hashPassword(rawPassword)

        val values = ContentValues().apply{
            put(DatabaseHelper.COLUMN_STUDENT_ID, student_id.trim().lowercase())
            put(DatabaseHelper.COLUMN_LASTNAME, lastname.trim().lowercase() )
            put(DatabaseHelper.COLUMN_FIRSTNAME, firstname.trim().lowercase() )
            put(DatabaseHelper.COLUMN_MIDDLENAME, middlename.trim().lowercase())
            put(DatabaseHelper.COLUMN_PASSWORD, hashedPassword)
        }
        val result = db.insert(DatabaseHelper.TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    //Authenticate user

    fun authenticateUser(student_id: String, rawPassword:String): Boolean{
        val db =dbHelper.readableDatabase
        val hashedPassword = SecurityUtils.hashPassword(rawPassword)
        val query = """
            SELECT ${DatabaseHelper.COLUMN_USER_ID} 
            FROM ${DatabaseHelper.TABLE_USERS} 
            WHERE ${DatabaseHelper.COLUMN_STUDENT_ID} = ? AND ${DatabaseHelper.COLUMN_PASSWORD} = ?
        """.trimIndent()

        // SelectionArgs sanitize parameters against SQL Injection
        val cursor = db.rawQuery(query, arrayOf(student_id.trim().lowercase(), hashedPassword))

        val isAuthenticated = cursor.count > 0
        cursor.close()
        db.close()

        //Authentication state
        return isAuthenticated
    }
}