package com.example.moviefinderapps


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

// API key for OMDb API (you should use your own)
const val API_KEY = "1d60369b"



// Function to search for a movie by title from OMDb API
suspend fun searchMovieByTitle(title: String): String {
    return withContext(Dispatchers.IO) {
        val encodedTitle = URLEncoder.encode(title, "UTF-8")
        val urlString = "https://www.omdbapi.com/?t=$encodedTitle&apikey=$API_KEY"
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection

        try {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }

            reader.close()
            parseMovieJson(response.toString())
        } catch (e: Exception) {
            "Error: ${e.message}"
        } finally {
            connection.disconnect()
        }
    }
}

// Function to search for multiple movies by title substring from OMDb API
suspend fun searchMoviesByTitleSubstring(searchQuery: String): List<Movie> {
    return withContext(Dispatchers.IO) {
        val encodedSearch = URLEncoder.encode(searchQuery, "UTF-8")
        val urlString = "https://www.omdbapi.com/?s=$encodedSearch&apikey=$API_KEY"
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection

        try {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }

            reader.close()

            val jsonObject = JSONObject(response.toString())
            val movies = mutableListOf<Movie>()

            if (jsonObject.getString("Response") == "True") {
                val searchResults = jsonObject.getJSONArray("Search")

                for (i in 0 until searchResults.length()) {
                    val movie = searchResults.getJSONObject(i)

                    // For each movie in search results, get full details
                    val movieTitle = movie.getString("Title")
                    val fullDetails = getMovieDetails(movieTitle)

                    if (fullDetails != null) {
                        movies.add(fullDetails)
                    }
                }
            }

            movies
        } catch (e: Exception) {
            emptyList()
        } finally {
            connection.disconnect()
        }
    }
}

// Function to get full movie details by title
suspend fun getMovieDetails(title: String): Movie? {
    return withContext(Dispatchers.IO) {
        val encodedTitle = URLEncoder.encode(title, "UTF-8")
        val urlString = "https://www.omdbapi.com/?t=$encodedTitle&apikey=$API_KEY"
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection

        try {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }

            reader.close()

            val json = JSONObject(response.toString())

            if (json.getString("Response") == "True") {
                Movie(
                    title = json.optString("Title", "N/A"),
                    year = json.optString("Year", "N/A"),
                    rate = json.optString("Rated", "N/A"),
                    released = json.optString("Released", "N/A"),
                    runtime = json.optString("Runtime", "N/A"),
                    genre = json.optString("Genre", "N/A"),
                    director = json.optString("Director", "N/A"),
                    writer = json.optString("Writer", "N/A"),
                    actors = json.optString("Actors", "N/A"),
                    plot = json.optString("Plot", "N/A")
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }
}

// Function to parse OMDb API JSON response to formatted string
fun parseMovieJson(jsonString: String): String {
    return try {
        val json = JSONObject(jsonString)

        if (json.optString("Response", "False") == "True") {
            val title = json.optString("Title", "N/A")
            val year = json.optString("Year", "N/A")
            val rated = json.optString("Rated", "N/A")
            val released = json.optString("Released", "N/A")
            val runtime = json.optString("Runtime", "N/A")
            val genre = json.optString("Genre", "N/A")
            val director = json.optString("Director", "N/A")
            val writer = json.optString("Writer", "N/A")
            val actors = json.optString("Actors", "N/A")
            val plot = json.optString("Plot", "N/A")

            StringBuilder().apply {
                append("Title: $title\n")
                append("Year: $year\n")
                append("Rated: $rated\n")
                append("Released: $released\n")
                append("Runtime: $runtime\n")
                append("Genre: $genre\n")
                append("Director: $director\n")
                append("Writer: $writer\n")
                append("Actors: $actors\n")
                append("Plot: $plot\n")
            }.toString()
        } else {
            "Movie not found"
        }
    } catch (e: Exception) {
        "Error parsing movie data: ${e.message}"
    }
}

// Function to convert movie details string to Movie object
fun parseMovieString(movieString: String): Movie {
    val lines = movieString.lines()
        .filter { it.contains(": ") }
        .associate {
            val parts = it.split(": ", limit = 2)
            parts[0].trim() to parts[1].trim()
        }

    return Movie(
        title = lines["Title"] ?: "N/A",
        year = lines["Year"] ?: "N/A",
        rate = lines["Rated"] ?: "N/A",
        released = lines["Released"] ?: "N/A",
        runtime = lines["Runtime"] ?: "N/A",
        genre = lines["Genre"] ?: "N/A",
        director = lines["Director"] ?: "N/A",
        writer = lines["Writer"] ?: "N/A",
        actors = lines["Actors"] ?: "N/A",
        plot = lines["Plot"] ?: "N/A"
    )
}