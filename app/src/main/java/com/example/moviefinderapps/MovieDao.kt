package com.example.moviefinderapps

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
    @Query("SELECT * FROM movie")
    suspend fun getAll(): List<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg movie: Movie)

    @Insert
    suspend fun insertMovie(movie: Movie)

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("SELECT * FROM movie WHERE title LIKE '%' || :title || '%'")
    suspend fun findByTitle(title: String): List<Movie>

    @Query("SELECT * FROM movie WHERE actors LIKE '%' || :actorName || '%' COLLATE NOCASE")
    suspend fun findByActor(actorName: String): List<Movie>

    @Query("DELETE FROM movie")
    suspend fun deleteAll()
}