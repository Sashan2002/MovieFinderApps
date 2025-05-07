package com.example.moviefinderapps

// Import Room annotations to define a Room Entity and Primary Key
import androidx.room.Entity
import androidx.room.PrimaryKey

// Annotates this data class as a Room Entity representing a table in the SQLite database
@Entity
data class Movie(
    // Marks 'id' as the Primary Key of the table with auto-increment enabled
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val title: String?, // Movie title, can be null
    val year: String?,  // Year the movie was released, can be null
    val rate: String?,  // Movie's rating (e.g., PG-13, R), can be null
    val released: String?, // Official release date, can be null
    val runtime: String?, // Runtime duration of the movie (e.g., "2h 30m"), can be null
    val genre: String?, // Movie genres (e.g., "Action, Drama"), can be null
    val director: String?, // Director name(s), can be null
    val writer: String?, // Writer(s) of the movie, can be null
    val actors: String?, // Actor(s) featured in the movie, can be null
    val plot: String?  // A short plot summary or description, can be null
)