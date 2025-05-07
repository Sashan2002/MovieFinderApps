package com.example.moviefinderapps

// Import necessary Room annotations for defining DAO operations
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Annotates this interface as a Data Access Object (DAO) for the Movie entity
@Dao
interface MovieDao {
    // Retrieves all movies stored in the 'movie' table
    @Query("SELECT * FROM movie")
    suspend fun getAll(): List<Movie>

    // Inserts multiple movies into the database.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg movie: Movie)

    // Inserts a single movie into the database
    @Insert
    suspend fun insertMovie(movie: Movie)

    // Deletes a specific movie from the database
    @Delete
    suspend fun deleteMovie(movie: Movie)

    // Finds movies where the title contains the provided text (case-sensitive by default)
    @Query("SELECT * FROM movie WHERE title LIKE '%' || :title || '%'")
    suspend fun findByTitle(title: String): List<Movie>

    // Finds movies by actor name, ignoring case sensitivity (COLLATE NOCASE)
    @Query("SELECT * FROM movie WHERE actors LIKE '%' || :actorName || '%' COLLATE NOCASE")
    suspend fun findByActor(actorName: String): List<Movie>

    // Deletes all entries from the movie table
    @Query("DELETE FROM movie")
    suspend fun deleteAll()
}