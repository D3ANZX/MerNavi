package com.deandev.mernavi

import android.content.ContentValues
import android.content.Context

// Data class defined outside DAO for easy UI access
data class Schedule(
    val scheduleId: Int,
    val fkCourseId: Int,
    val fkUserId: Int,
    val fkRoomId: Int,
    val startTime: String,
    val endTime: String,
    val day: String
)

class schedulesDao(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // ==========================================
    // 1. CREATE
    // ==========================================
    fun addSchedule(
        fkCourseId: Int,
        fkUserId: Int,
        fkRoomId: Int,
        startTime: String,
        endTime: String,
        day: String
    ): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.FK_COLUMN_USER_ID, fkUserId)
            put(DatabaseHelper.FK_COLUMN_COURSE_ID, fkCourseId)
            put(DatabaseHelper.FK_COLUMN_ROOM_ID, fkRoomId)
            put(DatabaseHelper.COLUMN_START_TIME, startTime.trim())
            put(DatabaseHelper.COLUMN_END_TIME, endTime.trim())
            put(DatabaseHelper.COLUMN_DAY, day.trim())
        }
        val result = db.insert(DatabaseHelper.TABLE_SCHEDULES, null, values)
        db.close()
        return result != -1L
    }

    // ==========================================
    // 2. READ (Get All Schedules by User ID)
    // ==========================================
    fun getAllSchedulesByUserId(userId: Int): List<Schedule> {
        val scheduleList = mutableListOf<Schedule>()
        val db = dbHelper.readableDatabase

        val query = """
            SELECT 
                ${DatabaseHelper.COLUMN_SCHEDULE_ID},
                ${DatabaseHelper.FK_COLUMN_COURSE_ID},
                ${DatabaseHelper.FK_COLUMN_USER_ID},
                ${DatabaseHelper.FK_COLUMN_ROOM_ID},
                ${DatabaseHelper.COLUMN_START_TIME},
                ${DatabaseHelper.COLUMN_END_TIME},
                ${DatabaseHelper.COLUMN_DAY}
            FROM ${DatabaseHelper.TABLE_SCHEDULES}
            WHERE ${DatabaseHelper.FK_COLUMN_USER_ID} = ?
        """.trimIndent()

        db.rawQuery(query, arrayOf(userId.toString())).use { cursor ->
            val scheduleIdIdx = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCHEDULE_ID)
            val courseIdIdx = cursor.getColumnIndexOrThrow(DatabaseHelper.FK_COLUMN_COURSE_ID)
            val userIdIdx = cursor.getColumnIndexOrThrow(DatabaseHelper.FK_COLUMN_USER_ID)
            val roomIdIdx = cursor.getColumnIndexOrThrow(DatabaseHelper.FK_COLUMN_ROOM_ID)
            val startTimeIdx = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_START_TIME)
            val endTimeIdx = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_END_TIME)
            val dayIdx = cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DAY)

            while (cursor.moveToNext()) {
                val schedule = Schedule(
                    scheduleId = cursor.getInt(scheduleIdIdx),
                    fkCourseId = cursor.getInt(courseIdIdx),
                    fkUserId = cursor.getInt(userIdIdx),
                    fkRoomId = cursor.getInt(roomIdIdx),
                    startTime = cursor.getString(startTimeIdx),
                    endTime = cursor.getString(endTimeIdx),
                    day = cursor.getString(dayIdx)
                )
                scheduleList.add(schedule)
            }
        }

        db.close()
        return scheduleList
    }

    // ==========================================
    // 2b. READ (Get Single Schedule by ID)
    // ==========================================
    fun getScheduleById(scheduleId: Int): Schedule? {
        val db = dbHelper.readableDatabase
        var schedule: Schedule? = null

        val query = """
            SELECT * FROM ${DatabaseHelper.TABLE_SCHEDULES}
            WHERE ${DatabaseHelper.COLUMN_SCHEDULE_ID} = ?
        """.trimIndent()

        db.rawQuery(query, arrayOf(scheduleId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                schedule = Schedule(
                    scheduleId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCHEDULE_ID)),
                    fkCourseId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.FK_COLUMN_COURSE_ID)),
                    fkUserId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.FK_COLUMN_USER_ID)),
                    fkRoomId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.FK_COLUMN_ROOM_ID)),
                    startTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_START_TIME)),
                    endTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_END_TIME)),
                    day = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DAY))
                )
            }
        }

        db.close()
        return schedule
    }

    // ==========================================
    // 3. UPDATE
    // ==========================================
    fun updateSchedule(
        scheduleId: Int,
        fkCourseId: Int,
        fkRoomId: Int,
        startTime: String,
        endTime: String,
        day: String
    ): Boolean {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.FK_COLUMN_COURSE_ID, fkCourseId)
            put(DatabaseHelper.FK_COLUMN_ROOM_ID, fkRoomId)
            put(DatabaseHelper.COLUMN_START_TIME, startTime.trim())
            put(DatabaseHelper.COLUMN_END_TIME, endTime.trim())
            put(DatabaseHelper.COLUMN_DAY, day.trim())
        }

        val rowsUpdated = db.update(
            DatabaseHelper.TABLE_SCHEDULES,
            values,
            "${DatabaseHelper.COLUMN_SCHEDULE_ID} = ?",
            arrayOf(scheduleId.toString())
        )

        db.close()
        return rowsUpdated > 0
    }

    // ==========================================
    // 4. DELETE
    // ==========================================
    fun deleteScheduleById(scheduleId: Int): Boolean {
        val db = dbHelper.writableDatabase

        val rowsDeleted = db.delete(
            DatabaseHelper.TABLE_SCHEDULES,
            "${DatabaseHelper.COLUMN_SCHEDULE_ID} = ?",
            arrayOf(scheduleId.toString())
        )

        db.close()
        return rowsDeleted > 0
    }
}