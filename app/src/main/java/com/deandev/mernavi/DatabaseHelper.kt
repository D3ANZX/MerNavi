package com.deandev.mernavi

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "mernav.db"
        private const val DATABASE_VERSION = 1


        //Users table
        const val TABLE_USERS = "user_tbl"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_STUDENT_ID = "student_id"
        const val COLUMN_LASTNAME = "lastname"
        const val COLUMN_FIRSTNAME = "firstname"
        const val COLUMN_MIDDLENAME = "middlename"
        const val COLUMN_PASSWORD = "password"

        //Schedule table
        const val TABLE_SCHEDULES = "schedules_tbl"
        const val COLUMN_SCHEDULE_ID = "schedule_id"
        const val FK_COLUMN_USER_ID = "student_id"
        const val FK_COLUMN_COURSE_ID = "course_id"

        const val FK_COLUMN_ROOM_ID = "room_id"
        const val COLUMN_START_TIME = "start_time"
        const val COLUMN_END_TIME = "end_time"
        const val COLUMN_DAY = "day"

        //Room Table
        const val TABLE_ROOMS = "rooms_tbl"
        const val COLUMN_ROOM_ID = "room_id"
        const val COLUMN_ROOM_NO = "room_no"
        const val COLUMN_BUILDING = "building_name"

        //Course Table
        const val TABLE_COURSES = "courses_tbl"
        const val COLUMN_COURSE_ID = "course_id"
        const val COLUMN_CLASS_CODE = "class_code"
        const val COLUMN_CLASS_CLASSIFICATION = "class_classification"
        const val COLUMN_COURSE_TITLE = "course_title"
        const val COLUMN_PROFESSOR_NAME = "professor_name"

    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_STUDENT_ID TEXT UNIQUE NOT NULL,
                $COLUMN_LASTNAME TEXT UNIQUE NOT NULL,
                $COLUMN_FIRSTNAME TEXT UNIQUE NOT NULL,
                $COLUMN_MIDDLENAME TEXT UNIQUE NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
            
            
        """.trimIndent()

        val createCoursesTable = """
            CREATE TABLE $TABLE_COURSES(
                $COLUMN_COURSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CLASS_CODE TEXT UNIQUE NOT NULL,
                $COLUMN_COURSE_TITLE TEXT UNIQUE NOT NULL,
                $COLUMN_CLASS_CLASSIFICATION TEXT NOT NULL,
                $COLUMN_PROFESSOR_NAME TEXT NOT NULL
            )
        """.trimIndent()

        val createRoomsTable = """
            CREATE TABLE $TABLE_ROOMS(
                $COLUMN_ROOM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ROOM_NO TEXT UNIQUE NOT NULL,
                $COLUMN_BUILDING TEXT NOT NULL
                
            )
        """.trimIndent()

        val createSchedulesTable = """
            CREATE TABLE $TABLE_SCHEDULES(
                $COLUMN_SCHEDULE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $FK_COLUMN_USER_ID INTEGER NOT NULL,
                $FK_COLUMN_COURSE_ID INTEGER NOT NULL,
                $FK_COLUMN_ROOM_ID INT NOT NULL,
                $COLUMN_START_TIME TEXT NOT NULL,
                $COLUMN_END_TIME TEXT NOT NULL,
                $COLUMN_DAY TEXT NOT NULL,
                FOREIGN KEY($FK_COLUMN_COURSE_ID) REFERENCES $TABLE_COURSES($COLUMN_COURSE_ID) ON DELETE CASCADE
            )
        """.trimIndent()


        db.execSQL(createUsersTable)
        db.execSQL(createCoursesTable)
        db.execSQL(createRoomsTable)
        db.execSQL(createSchedulesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }
}
