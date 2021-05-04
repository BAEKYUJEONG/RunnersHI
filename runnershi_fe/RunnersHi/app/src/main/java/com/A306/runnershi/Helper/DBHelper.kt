package com.A306.runnershi.Helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

val DATABASE_NAME = "RunnersHiDB"
val TABLE_NAME = "User"

class DBHelper(context: Context) : SQLiteOpenHelper(context, "RunnersHiDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

}