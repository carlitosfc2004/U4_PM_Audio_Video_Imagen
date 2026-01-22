package com.example.imagen_audio_video

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.imagen_audio_video.ui.theme.Imagen_Audio_VideoTheme

// 1. Data class for animals
data class Animal(val name: String, val imageRes: Int, val soundRes: Int)

// 2. Navigation routes
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detail : Screen("detail/{animalName}") {
        fun createRoute(animalName: String) = "detail/$animalName"
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Imagen_Audio_VideoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. Navigation setup
                    val navController = rememberNavController()
                    AppNavigator(navController = navController)
                }
            }
        }
    }
}

@Composable
fun AppNavigator(navController: NavHostController) {
    // Animal data
    val animals = listOf(
        Animal("Elefante", R.drawable.elefante, R.raw.sonido_elefante),
        Animal("Perro", R.drawable.perro, R.raw.sonido_perro),
        Animal("Gato", R.drawable.gato, R.raw.sonido_gato),
        Animal("Caballo", R.drawable.caballo, R.raw.sonido_caballo)
    )

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            // 4. Home screen with animal grid
            HomeScreen(navController = navController, animals = animals)
        }
        composable(Screen.Detail.route) { backStackEntry ->
            val animalName = backStackEntry.arguments?.getString("animalName")
            val animal = animals.find { it.name == animalName }
            if (animal != null) {
                // 5. Detail screen for the selected animal
                DetailScreen(animal = animal)
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController, animals: List<Animal>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(animals) { animal ->
            Image(
                painter = painterResource(id = animal.imageRes),
                contentDescription = animal.name,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable {
                        navController.navigate(Screen.Detail.createRoute(animal.name))
                    },
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun DetailScreen(animal: Animal) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = animal.imageRes),
            contentDescription = animal.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(context, animal.soundRes).apply {
                start()
            }
        }) {
            Text(text = "Reproducir sonido")
        }
    }
}
