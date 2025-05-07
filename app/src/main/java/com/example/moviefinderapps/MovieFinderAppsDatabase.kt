package com.example.moviefinderapps

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Movie::class], version = 1)
abstract class MovieFinderAppsDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
