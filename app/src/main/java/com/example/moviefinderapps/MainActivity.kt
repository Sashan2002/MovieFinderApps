package com.example.moviefinderapps



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.moviefinderapps.ui.theme.MovieFinderAppsTheme

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object SearchMovies : Screen("search_movies")
    object SearchActors : Screen("search_actors")
    object SearchOMDb : Screen("search_omdb")
}

lateinit var db: MovieFinderAppsDatabase
lateinit var movieDao: MovieDao

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Room database
        db = Room.databaseBuilder(
            applicationContext,
            MovieFinderAppsDatabase::class.java,
            "movies.db"
        )
            .fallbackToDestructiveMigration()
            .build()

        movieDao = db.movieDao()

        setContent {
            MovieFinderAppsTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Main.route
                ) {
                    composable(Screen.Main.route) {
                        MainScreen(navController)
                    }
                    composable("add_movie") {
                        AddMovieScreen(navController,movieDao)
                    }

                    composable(Screen.SearchMovies.route) {
                        SearchMoviesScreen()
                    }
                    composable(Screen.SearchActors.route) {
                        SearchActorsScreen()
                    }
                    composable(Screen.SearchOMDb.route) {
                        SearchOMDbScreen()
                    }
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
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Button 1: Add Movies to DB
            Button(
                onClick = {
                    // Add hardcoded movies to the database
                    //addMoviesToDatabase(scope)
                    navController.navigate("add_movie")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Add Movies to DB")
            }

            // Button 2: Search for Movies
            Button(
                onClick = { navController.navigate(Screen.SearchMovies.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Search for Movies")
            }

            // Button 3: Search for Actors
            Button(
                onClick = { navController.navigate(Screen.SearchActors.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Search for Actors")
            }

            // Button 4: Search for Movies by Title (from OMDb)
            Button(
                onClick = { navController.navigate(Screen.SearchOMDb.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Search OMDb for Movies")
            }
            Button(onClick = {
                navController.navigate("view_all_movies")
            }) {
                Text("View All Movies")
            }

        }
    }
}