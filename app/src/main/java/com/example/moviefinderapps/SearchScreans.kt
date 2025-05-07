package com.example.moviefinderapps


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.viewmodel.compose.viewModel



@Composable
fun AddMovieScreen(navController: NavController, movieDao: MovieDao) {
    var title by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var rate by rememberSaveable { mutableStateOf("") }
    var released by rememberSaveable { mutableStateOf("") }
    var runtime by rememberSaveable { mutableStateOf("") }
    var genre by rememberSaveable { mutableStateOf("") }
    var director by rememberSaveable { mutableStateOf("") }
    var writer by rememberSaveable { mutableStateOf("") }
    var actors by rememberSaveable { mutableStateOf("") }
    var plot by rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

            ) {
            Text("Enter Movie Details",  style = MaterialTheme.typography.headlineLarge,color = MaterialTheme.colorScheme.primary)


            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") })
            OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("Year") })
            OutlinedTextField(value = rate, onValueChange = { rate = it }, label = { Text("Rate") })
            OutlinedTextField(
                value = released,
                onValueChange = { released = it },
                label = { Text("Released") })
            OutlinedTextField(
                value = runtime,
                onValueChange = { runtime = it },
                label = { Text("Runtime") })
            OutlinedTextField(
                value = genre,
                onValueChange = { genre = it },
                label = { Text("Genre") })
            OutlinedTextField(
                value = director,
                onValueChange = { director = it },
                label = { Text("Director") })
            OutlinedTextField(
                value = writer,
                onValueChange = { writer = it },
                label = { Text("Writer") })
            OutlinedTextField(
                value = actors,
                onValueChange = { actors = it },
                label = { Text("Actors") })
            OutlinedTextField(value = plot, onValueChange = { plot = it }, label = { Text("Plot") })


            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    val movie = Movie(
                        title = title,
                        year = year,
                        rate = rate,
                        released = released,
                        runtime = runtime,
                        genre = genre,
                        director = director,
                        writer = writer,
                        actors = actors,
                        plot = plot

                    )

                    scope.launch {
                        movieDao.insertMovie(movie)

                        snackbarHostState.showSnackbar("Successfully added")
                    }
                }) {
                    Text("Save Movie")
                }

                // View All Movies button
                Button(onClick = {
                    navController.navigate("view_all_movies")
                }) {
                    Text("View All Movies")
                }
                Button(onClick = {
                    // Clear all fields
                    title = ""
                    year = ""
                    rate = ""
                    released = ""
                    runtime = ""
                    genre = ""
                    director = ""
                    writer = ""
                    actors = ""
                    plot = ""
                    //viewModel.clearForm()
                }) {
                    Text("Clear")
                }
            }
        }
    }
}


// Search Movies Screen - Requirement 3 & 4
@Composable
fun SearchMoviesScreen() {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var movieDetails by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    // This is used to handle device rotation and save state
    val viewModel = remember { SearchViewModel() }

    // Restore state if available
    LaunchedEffect(Unit) {
        if (viewModel.searchQuery.isNotEmpty()) {
            searchQuery = viewModel.searchQuery
        }
        if (viewModel.movieDetails.isNotEmpty()) {
            movieDetails = viewModel.movieDetails
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Text(
                text = "Search for Movies",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    // Update ViewModel to maintain state on rotation
                    viewModel.searchQuery = it
                },
                label = { Text("Enter Movie Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            movieDetails = searchMovieByTitle(searchQuery)
                            // Update ViewModel to maintain state on rotation
                            viewModel.movieDetails = movieDetails
                            isLoading = false

                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Retrieve Movie")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (movieDetails.isNotEmpty() && movieDetails != "Movie not found") {
                            scope.launch {
                                val movie = parseMovieString(movieDetails)
                                movieDao.insertMovie(movie)
                                // Show snackbar or toast here
                                snackbarHostState.showSnackbar("Successfully added")
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = movieDetails.isNotEmpty() && movieDetails != "Movie not found"
                ) {
                    Text("Save movie to Database")
                }
            }

            if (isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    CircularProgressIndicator()
                }
            } else if (movieDetails.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = movieDetails,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

}

// Search Actors Screen - Requirement 5
@Composable
fun SearchActorsScreen() {
    var actorSearchQuery by rememberSaveable { mutableStateOf("") }
    var searchResults by rememberSaveable { mutableStateOf(listOf<Movie>()) }
    var isSearching by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    // This is used to handle device rotation and save state
    val viewModel = remember { ActorSearchViewModel() }

    // Restore state if available
    LaunchedEffect(Unit) {
        if (viewModel.searchQuery.isNotEmpty()) {
            actorSearchQuery = viewModel.searchQuery
        }
        if (viewModel.searchResults.isNotEmpty()) {
            searchResults = viewModel.searchResults
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Search for Actors",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = actorSearchQuery,
            onValueChange = {
                actorSearchQuery = it
                // Update ViewModel to maintain state on rotation
                viewModel.searchQuery = it
            },
            label = { Text("Enter Actor Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                scope.launch {
                    isSearching = true
                    val results = withContext(Dispatchers.IO) {
                        movieDao.findByActor(actorSearchQuery)
                    }
                    searchResults = results
                    // Update ViewModel to maintain state on rotation
                    viewModel.searchResults = results
                    isSearching = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Search")
        }

        if (isSearching) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                CircularProgressIndicator()
            }
        } else if (searchResults.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(searchResults) { movie ->
                    MovieCard(movie)
                }
            }
        } else if (!isSearching && actorSearchQuery.isNotEmpty()) {
            Text(
                text = "No movies found with actor: $actorSearchQuery",
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

// Search OMDb for Movies Screen - Requirement 7
@Composable
fun SearchOMDbScreen() {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var searchResults by rememberSaveable { mutableStateOf(listOf<Movie>()) }
    var isSearching by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    // This is used to handle device rotation and save state
    val viewModel = remember { OmdbSearchViewModel() }

    // Restore state if available
    LaunchedEffect(Unit) {
        if (viewModel.searchQuery.isNotEmpty()) {
            searchQuery = viewModel.searchQuery
        }
        if (viewModel.searchResults.isNotEmpty()) {
            searchResults = viewModel.searchResults
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Search OMDb for Movies",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                // Update ViewModel to maintain state on rotation
                viewModel.searchQuery = it
            },
            label = { Text("Enter Movie Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                scope.launch {
                    isSearching = true
                    val results = searchMoviesByTitleSubstring(searchQuery)
                    searchResults = results
                    // Update ViewModel to maintain state on rotation
                    viewModel.searchResults = results
                    isSearching = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Search")
        }

        if (isSearching) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                CircularProgressIndicator()
            }
        } else if (searchResults.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(searchResults) { movie ->
                    MovieCard(movie)
                }
            }
        } else if (!isSearching && searchQuery.isNotEmpty()) {
            Text(
                text = "No movies found with title: $searchQuery",
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

// Movie Card Component to display movie details
@Composable
fun MovieCard(movie: Movie) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = movie.title ?: "N/A",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Year: ${movie.year ?: "N/A"}")
            Text("Rated: ${movie.rate ?: "N/A"}")
            Text("Released: ${movie.released ?: "N/A"}")
            Text("Runtime: ${movie.runtime ?: "N/A"}")
            Text("Genre: ${movie.genre ?: "N/A"}")
            Text("Director: ${movie.director ?: "N/A"}")
            Text("Writer: ${movie.writer ?: "N/A"}")
            Text("Actors: ${movie.actors ?: "N/A"}")
            Text("Plot: ${movie.plot ?: "N/A"}")
        }
    }
}

@Composable
fun ViewAllMoviesScreen(movieDao: MovieDao) {
    val scope = rememberCoroutineScope()
    var movies by rememberSaveable { mutableStateOf(listOf<Movie>()) }

    LaunchedEffect(Unit) {
        scope.launch {
            movies = movieDao.getAll()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("All Saved Movies",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        if (movies.isEmpty()) {
            Text("No movies found in the database.")
        } else {
            LazyColumn {
                items(movies) { movie ->
                    MovieCard(movie)
                }
            }
        }
    }
}


// ViewModels to maintain state during rotation
class SearchViewModel {
    var searchQuery: String = ""
    var movieDetails: String = ""
}

class ActorSearchViewModel {
    var searchQuery: String = ""
    var searchResults: List<Movie> = emptyList()
}

class OmdbSearchViewModel {
    var searchQuery: String = ""
    var searchResults: List<Movie> = emptyList()
}