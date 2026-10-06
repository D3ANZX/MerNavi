package com.deandev.mernavi
import android.content.ContentValues
import android.content.Context
class coursesDao(context: Context) {

    data class Course(
        val courseId: Int,
        val classCode: String,
        val courseTitle:String,
        val professorName:String
    )
    private val dbHelper = DatabaseHelper(context)

    fun addCourse(userId: Int, classCode: String, courseTitle:String, professorName:String):Boolean{
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply{
            put(DatabaseHelper.FK_COLUMN_USER_ID, userId)
            put(DatabaseHelper.COLUMN_CLASS_CODE, classCode.lowercase().trim())
            put(DatabaseHelper.COLUMN_COURSE_TITLE, courseTitle.lowercase().trim())
            put(DatabaseHelper.COLUMN_PROFESSOR_NAME, professorName.lowercase().trim())

        }
        val result = db.insert(DatabaseHelper.TABLE_COURSES, null, values)
        db.close()
        return result !=-1L
    }

    fun getAllCourses(): List<Course> {
        val coursesList = mutableListOf<Course>()
        val db = dbHelper.readableDatabase
        val query = """
        SELECT 
            ${DatabaseHelper.COLUMN_COURSE_ID},
            ${DatabaseHelper.COLUMN_CLASS_CODE},
            ${DatabaseHelper.COLUMN_COURSE_TITLE},
            ${DatabaseHelper.COLUMN_PROFESSOR_NAME}
        FROM ${DatabaseHelper.TABLE_COURSES}
    """.trimIndent()

        db.rawQuery(query, null).use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COURSE_ID)
            val codeIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CLASS_CODE)
            val titleIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_COURSE_TITLE)
            val profIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROFESSOR_NAME)

            while (cursor.moveToNext()) {
                val course = Course(
                    courseId = cursor.getInt(idIndex),
                    classCode = cursor.getString(codeIndex),
                    courseTitle = cursor.getString(titleIndex),
                    professorName = cursor.getString(profIndex)
                )
                coursesList.add(course)
            }
        }

        db.close()
        return coursesList
    }

    fun deleteCourseById(courseId: Int):Boolean{
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete(
            DatabaseHelper.TABLE_COURSES,
            "${DatabaseHelper.COLUMN_COURSE_ID} = ?",
            arrayOf(courseId.toString())
        )
        db.close()
        return rowsDeleted > 0
    }

    fun updateCourse(course:Course):Boolean{
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply{
            put(DatabaseHelper.COLUMN_CLASS_CODE, course.classCode.lowercase().trim())
            put(DatabaseHelper.COLUMN_COURSE_TITLE, course.courseTitle.lowercase().trim())
            put(DatabaseHelper.COLUMN_PROFESSOR_NAME, course.professorName.lowercase().trim())
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
}