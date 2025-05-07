package com.example.moviefinderapps

// Required Android and Jetpack Compose imports
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.moviefinderapps.ui.theme.MovieFinderAppsTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

// Define app navigation routes using a sealed class
sealed class Screen(val route: String) {
    object Main : Screen("main")  // Main screen route
    object SearchMovies : Screen("search_movies")  // Local DB search
    object SearchActors : Screen("search_actors")  // Search movies by actor
    object SearchOMDb : Screen("search_omdb")  // OMDb API search
}

// Declare Room database and DAO globally for access across Composables
lateinit var db: MovieFinderAppsDatabase
lateinit var movieDao: MovieDao

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Room database named "movies.db"
        db = Room.databaseBuilder(
            applicationContext,
            MovieFinderAppsDatabase::class.java,
            "movies.db"
        )
            .fallbackToDestructiveMigration()  // Destroys and rebuilds DB on version change
            .build()

        // Access the MovieDao for performing DB operations
        movieDao = db.movieDao()

        // Set the UI content using Jetpack Compose
        setContent {
            MovieFinderAppsTheme {
                // Create a navigation controller for screen transitions
                val navController = rememberNavController()

                // Define navigation routes for each screen using NavHost
                NavHost(
                    navController = navController,
                    startDestination = Screen.Main.route
                ) {
                    // Main screen: entry point with all buttons
                    composable(Screen.Main.route) {
                        MainScreen(navController)
                    }
                    // Add movies from hardcoded data (simulating movies.txt)
                    composable("add_movie") {
                        AddMovieScreen(navController,movieDao)
                    }
                    // Search movies from local Room DB
                    composable(Screen.SearchMovies.route) {
                        SearchMoviesScreen()
                    }
                    // Search by actor name in local DB
                    composable(Screen.SearchActors.route) {
                        SearchActorsScreen()
                    }
                    // Search movies using OMDb API
                    composable(Screen.SearchOMDb.route) {
                        SearchOMDbScreen()
                    }
                    // Optional: View all movies in DB (commented out in UI)
                    composable("view_all_movies") {
                        ViewAllMoviesScreen(movieDao)
                    }

                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: androidx.navigation.NavHostController) {
    val scrollState = rememberScrollState()  // Enable vertical scrolling for long content

    // Surface is the root layout that fills the screen
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Column layout for vertical stacking
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)  // Enable scrolling
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // App title
            Text(
                text = "Movie Finder",
                fontSize = 50.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 28.dp)
            )
            // App banner image
            Image(
                painter = painterResource(id = R.drawable.movie3),
                contentDescription = "App Banner",
                modifier = Modifier
                    .height(300.dp)
                    .padding(vertical = 40.dp)
            )
            // Card container holding navigation buttons
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Button to load hardcoded movies into Room DB
                    MovieActionButton("Add Movies to DB", Icons.Default.Add) {
                        navController.navigate("add_movie")
                    }
                    // Button to search movies from local database
                    MovieActionButton("Search for Movies", Icons.Default.Search) {
                        navController.navigate(Screen.SearchMovies.route)
                    }
                    // Button to search movies by actor
                    MovieActionButton("Search for Actors", Icons.Default.Person) {
                        navController.navigate(Screen.SearchActors.route)
                    }
                    // Button to search movie details from OMDb API
                    MovieActionButton("Search OMDb for Movies", Icons.Default.Public) {
                        navController.navigate(Screen.SearchOMDb.route)
                    }
                    //show all movies
//                    MovieActionButton("View All Movies", Icons.Default.List) {
//                        navController.navigate("view_all_movies")
//                    }

                }
            }
        }
    }
}

// Reusable Composable for main action buttons
@Composable
fun MovieActionButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        // Icon and label inside the button
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

