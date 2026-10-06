package com.deandev.mernavi
import android.content.ContentValues
import android.content.Context

class DatabaseSeeder(context: Context) {
    private val roomsDao = roomsDao(context)
    private val coursesDao = coursesDao(context)
    fun seedRooms(){
        for (floor in 1..4){
            for(roomNum in 1..10){
                val roomNumberString = "$floor${"%02d".format(roomNum)}"
                roomsDao.addRoom(roomNumberString, "Main")
            }
        }
    }

    fun seedCourses(){
        val networking1 = coursesDao.addCourse("ITE322", "Networking 1", "ITE", "Default")
    }

}