package com.example.moviefinderapps

// Import necessary Room components
import androidx.room.Database
import androidx.room.RoomDatabase

// This annotation defines a Room database with a list of entities and a version number
// In this case, the database includes only one entity: Movie, and it is at version 1
@Database(entities = [Movie::class], version = 1)
abstract class MovieFinderAppsDatabase : RoomDatabase() {
    // Abstract method to access the DAO (Data Access Object) for Movie entity
    // Room will automatically generate the implementation at compile time
    abstract fun movieDao(): MovieDao
}
