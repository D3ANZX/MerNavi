package com.deandev.mernavi
import android.content.ContentValues
import android.content.Context
class roomsDao(context: Context) {
    data class Rooms(
        val roomId: Int,
        val roomNo: String,
        val buildingName: String
    )
    private val dbHelper = DatabaseHelper(context)
    fun addRoom(roomNo: String, buildingName: String){
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_ROOM_NO, roomNo.trim())
            put(DatabaseHelper.COLUMN_BUILDING, buildingName)
        }
    }

}