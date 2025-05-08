package com.example.moviefinderapps

// Import required libraries for coroutine handling and networking
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


// Constant for OMDb API key – replace this with your own API key for production use
const val API_KEY = "1d60369b"

// Function to add hardcoded movies to the database
fun addMoviesToDatabase(scope: CoroutineScope) {
    scope.launch(Dispatchers.IO) {
        // Clear existing movies
        movieDao.deleteAll()

        // Add hardcoded movies from the specification
        val moviesData = listOf(
            Movie(
                title = "The Shawshank Redemption",
                year = "1994",
                rate = "R",
                released = "14 Oct 1994",
                runtime = "142 min",
                genre = "Drama",
                director = "Frank Darabont",
                writer = "Stephen King, Frank Darabont",
                actors = "Tim Robbins, Morgan Freeman, Bob Gunton",
                plot = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency."
            ),
            Movie(
                title = "Batman: The Dark Knight Returns, Part 1",
                year = "2012",
                rate = "PG-13",
                released = "25 Sep 2012",
                runtime = "76 min",
                genre = "Animation, Action, Crime, Drama, Thriller",
                director = "Jay Oliva",
                writer = "Bob Kane (character created by: Batman), Frank Miller (comic book), Klaus Janson (comic book), Bob Goodman",
                actors = "Peter Weller, Ariel Winter, David Selby, Wade Williams",
                plot = "Batman has not been seen for ten years. A new breed of criminal ravages Gotham City, forcing 55-year-old Bruce Wayne back into the cape and cowl. But, does he still have what it takes to fight crime in a new era?"
            ),
            Movie(
                title = "The Lord of the Rings: The Return of the King",
                year = "2003",
                rate = "PG-13",
                released = "17 Dec 2003",
                runtime = "201 min",
                genre = "Action, Adventure, Drama",
                director = "Peter Jackson",
                writer = "J.R.R. Tolkien, Fran Walsh, Philippa Boyens",
                actors = "Elijah Wood, Viggo Mortensen, Ian McKellen",
                plot = "Gandalf and Aragorn lead the World of Men against Sauron's army to draw his gaze from Frodo and Sam as they approach Mount Doom with the One Ring."
            ),
            Movie(
                title = "Inception",
                year = "2010",
                rate = "PG-13",
                released = "16 Jul 2010",
                runtime = "148 min",
                genre = "Action, Adventure, Sci-Fi",
                director = "Christopher Nolan",
                writer = "Christopher Nolan",
                actors = "Leonardo DiCaprio, Joseph Gordon-Levitt, Elliot Page",
                plot = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., but his tragic past may doom the project and his team to disaster."
            ),
            Movie(
                title = "The Matrix",
                year = "1999",
                rate = "R",
                released = "31 Mar 1999",
                runtime = "136 min",
                genre = "Action, Sci-Fi",
                director = "Lana Wachowski, Lilly Wachowski",
                writer = "Lilly Wachowski, Lana Wachowski",
                actors = "Keanu Reeves, Laurence Fishburne, Carrie-Anne Moss",
                plot = "When a beautiful stranger leads computer hacker Neo to a forbidding underworld, he discovers the shocking truth--the life he knows is the elaborate deception of an evil cyber-intelligence."
            ),

        )

        // Insert all movies into the database
        for (movie in moviesData) {
            movieDao.insertMovie(movie)

        }
    }
}

// Function to search a single movie by exact title
// Uses OMDb API and returns a formatted string of movie details
suspend fun searchMovieByTitle(title: String): String {
    return withContext(Dispatchers.IO) {
        // URL encode the title for safe HTTP request
        val encodedTitle = URLEncoder.encode(title, "UTF-8")
        val urlString = "https://www.omdbapi.com/?t=$encodedTitle&apikey=$API_KEY"
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection

        try {
            // Read the response from the API
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?

            // Read each line from the response
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }

            // Close reader after reading is complete
            reader.close()
            // Parse the response JSON into a formatted string
            parseMovieJson(response.toString())
        } catch (e: Exception) {
            "Error: ${e.message}" // Handle any exception during network call
        } finally {
            connection.disconnect()  // Ensure connection is closed
        }
    }
}

// Function to search for multiple movies by a title substring
// Calls OMDb API's "s=" search, then fetches full data for each result
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

            // Check if response was successful
            if (jsonObject.getString("Response") == "True") {
                val searchResults = jsonObject.getJSONArray("Search")

                // For each search result, get complete movie details
                for (i in 0 until searchResults.length()) {
                    val movie = searchResults.getJSONObject(i)

                    // For each movie in search results, get full details
                    val movieTitle = movie.getString("Title")
                    val fullDetails = getMovieDetails(movieTitle)

                    // Add to list only if details are successfully fetched
                    if (fullDetails != null) {
                        movies.add(fullDetails)
                    }
                }
            }

            movies // Return the list of movie objects
        } catch (e: Exception) {
            emptyList()  // Return empty list on error
        } finally {
            connection.disconnect()
        }
    }
}

// Function to fetch complete details of a movie by title
// Returns a Movie object or null if not found or failed
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

            // If the response was successful, extract movie details
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
                null // Return null if there is an error during parsing or network call
            }
        } catch (e: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }
}

// Helper function to convert raw JSON string to readable movie details
// Returns a formatted string with key movie fields
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

            // Build a formatted string with movie details
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
            "Movie not found"  // If the API response indicates failure
        }
    } catch (e: Exception) {
        "Error parsing movie data: ${e.message}"  // Handle JSON parsing exceptions
    }
}

// Function to parse a formatted string and convert to a Movie object
// Assumes input string contains fields in "Key: Value" format
fun parseMovieString(movieString: String): Movie {
    // Convert string lines into key-value pairs
    val lines = movieString.lines()
        .filter { it.contains(": ") }
        .associate {
            val parts = it.split(": ", limit = 2)
            parts[0].trim() to parts[1].trim()
        }

    // Construct and return Movie object using extracted values
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