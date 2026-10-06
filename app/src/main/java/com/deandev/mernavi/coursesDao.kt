package com.deandev.mernavi

import android.content.ContentValues
import android.content.Context

// Top-level data class updated with classClassification
data class Course(
    val courseId: Int,
    val classCode: String,
    val courseTitle: String,
    val classClassification: String,
    val professorName: String
)

class coursesDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // ==========================================
    // 1. CREATE
    // ==========================================
    fun addCourse(
        classCode: String,
        courseTitle: String,
        classClassification: String,
        professorName: String
    ): Boolean {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CLASS_CODE, classCode.trim().uppercase())
            put(DatabaseHelper.COLUMN_COURSE_TITLE, courseTitle.trim())
            put(DatabaseHelper.COLUMN_CLASS_CLASSIFICATION, classClassification.trim())
            put(DatabaseHelper.COLUMN_PROFESSOR_NAME, professorName.trim())
        }

        val result = db.insert(DatabaseHelper.TABLE_COURSES, null, values)
        db.close()
        return result != -1L
    }

    // ==========================================
    // 2. READ (Get All Courses)
    // ==========================================
    fun getAllCourses(): List<Course> {
        val coursesList = mutableListOf<Course>()
        val db = dbHelper.readableDatabase

        val query = """
            SELECT 
                ${DatabaseHelper.COLUMN_COURSE_ID},
                ${DatabaseHelper.COLUMN_CLASS_CODE},
                ${DatabaseHelper.COLUMN_COURSE_TITLE},
                ${DatabaseHelper.COLUMN_CLASS_CLASSIFICATION},
                ${DatabaseHelper.COLUMN_PROFESSOR_NAME}
            FROM ${DatabaseHelper.TABLE_COURSES}
        """.trimIndent()

        db.rawQuery(query, null).use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COURSE_ID)
            val codeIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CLASS_CODE)
            val titleIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COURSE_TITLE)
            val classifIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CLASS_CLASSIFICATION)
            val profIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFESSOR_NAME)

            while (cursor.moveToNext()) {
                val course = Course(
                    courseId = cursor.getInt(idIndex),
                    classCode = cursor.getString(codeIndex),
                    courseTitle = cursor.getString(titleIndex),
                    classClassification = cursor.getString(classifIndex),
                    professorName = cursor.getString(profIndex)
                )
                coursesList.add(course)
            }
        }

        db.close()
        return coursesList
    }

    // ==========================================
    // 2b. READ (Get Single Course by ID)
    // ==========================================
    fun getCourseById(courseId: Int): Course? {
        val db = dbHelper.readableDatabase
        var course: Course? = null

        val query = """
            SELECT 
                ${DatabaseHelper.COLUMN_COURSE_ID},
                ${DatabaseHelper.COLUMN_CLASS_CODE},
                ${DatabaseHelper.COLUMN_COURSE_TITLE},
                ${DatabaseHelper.COLUMN_CLASS_CLASSIFICATION},
                ${DatabaseHelper.COLUMN_PROFESSOR_NAME}
            FROM ${DatabaseHelper.TABLE_COURSES} 
            WHERE ${DatabaseHelper.COLUMN_COURSE_ID} = ?
        """.trimIndent()

        db.rawQuery(query, arrayOf(courseId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                course = Course(
                    courseId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COURSE_ID)),
                    classCode = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CLASS_CODE)),
                    courseTitle = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COURSE_TITLE)),
                    classClassification = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CLASS_CLASSIFICATION)),
                    professorName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFESSOR_NAME))
                )
            }
        }

        db.close()
        return course
    }

    // ==========================================
    // 3. UPDATE
    // ==========================================
    fun updateCourse(course: Course): Boolean {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CLASS_CODE, course.classCode.trim().uppercase())
            put(DatabaseHelper.COLUMN_COURSE_TITLE, course.courseTitle.trim())
            put(DatabaseHelper.COLUMN_CLASS_CLASSIFICATION, course.classClassification.trim())
            put(DatabaseHelper.COLUMN_PROFESSOR_NAME, course.professorName.trim())
        }

        val rowsUpdated = db.update(
            DatabaseHelper.TABLE_COURSES,
            values,
            "${DatabaseHelper.COLUMN_COURSE_ID} = ?",
            arrayOf(course.courseId.toString())
        )

        db.close()
        return rowsUpdated > 0
    }

    // ==========================================
    // 4. DELETE
    // ==========================================
    fun deleteCourseById(courseId: Int): Boolean {
        val db = dbHelper.writableDatabase

        val rowsDeleted = db.delete(
            DatabaseHelper.TABLE_COURSES,
            "${DatabaseHelper.COLUMN_COURSE_ID} = ?",
            arrayOf(courseId.toString())
        )

        db.close()
        return rowsDeleted > 0
    }
}