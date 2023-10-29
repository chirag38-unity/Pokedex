package com.example.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.pokedex.dataclasses.local.PokemonListItem
import com.example.pokedex.ui.theme.PokedexTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedexTheme {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }
}

@Destination
@Composable
fun PokemonDetailScreen(
    navigator : DestinationsNavigator,
    pokemon: PokemonListItem
) {
    Text(text = "Pokemon Detail Screen")
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PokedexTheme {

    }
}