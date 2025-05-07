package com.example.moviefinderapps

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
    val scrollState = rememberScrollState()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Movie Finder",
                fontSize = 50.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 28.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.movie3),
                contentDescription = "App Banner",
                modifier = Modifier
                    .height(300.dp)
                    .padding(vertical = 40.dp)
            )

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

                    MovieActionButton("Add Movies to DB", Icons.Default.Add) {
                        navController.navigate("add_movie")
                    }
                    MovieActionButton("Search for Movies", Icons.Default.Search) {
                        navController.navigate(Screen.SearchMovies.route)
                    }
                    MovieActionButton("Search for Actors", Icons.Default.Person) {
                        navController.navigate(Screen.SearchActors.route)
                    }
                    MovieActionButton("Search OMDb for Movies", Icons.Default.Public) {
                        navController.navigate(Screen.SearchOMDb.route)
                    }
                    MovieActionButton("View All Movies", Icons.Default.List) {
                        navController.navigate("view_all_movies")
                    }

                }
            }
        }
    }
}

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
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

