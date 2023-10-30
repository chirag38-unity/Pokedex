package com.example.pokedex.ui.screens.pokemonlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.pokedex.R
import com.example.pokedex.dataclasses.local.PokemonListItem
import com.example.pokedex.ui.screens.destinations.PokemonDetailScreenDestination
import com.example.pokedex.ui.theme.RobotoCondensed
import com.example.pokedex.viewmodels.PokedexListEntry
import com.example.pokedex.viewmodels.PokedexListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.Locale

@Destination(start = true)
@Composable
fun PokemonListScreen(
    navigator : DestinationsNavigator,
    viewModel: PokedexListViewModel = hiltViewModel()
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Image(painter = painterResource(id = R.drawable.pokemon_theme_image ),
                contentDescription = "Pokemon Theme Logo",
                modifier = Modifier
                    .fillMaxWidth(.4f)
                    .align(CenterHorizontally))
            SearchBar(hint = "What are you looking for...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                viewModel.searchPokedexList(it)
            }
            Spacer(modifier = Modifier.height(16.dp))
            PokemonList(navigator = navigator)
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint : String = "",
    onSearch : (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }
    
    Box(modifier = modifier){
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused && text.isEmpty()
                }
        )
        if(isHintDisplayed){
            Text(text = hint, color = Color.Gray,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp))
        }
    }
}

@Composable
fun PokedexEntry(
    entry : PokedexListEntry,
    navigator : DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: PokedexListViewModel = hiltViewModel()
) {
    val defaultDominantColor = MaterialTheme.colorScheme.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }
    
    Box(
        contentAlignment = Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor, defaultDominantColor
                    )
                )
            )
            .clickable {
                navigator.navigate(
                    PokemonDetailScreenDestination(
                        PokemonListItem(
                            dominantColor = dominantColor.toArgb(),
                            name = entry.pokemonName.toLowerCase(Locale.ROOT) ?: ""
                        )
                    )
                )
            }
    ) {
        Column {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                .data(entry.imageUrl)
                .crossfade(true)
                .build(),
                onSuccess = {
                    viewModel.calDominantColor(it.result.drawable) {
                        dominantColor = it
                    }
                },
                loading = {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.scale(.5f)
                    )
                },
                contentDescription = "${entry.pokemonName} image" ,
                modifier = Modifier
                    .size(120.dp)
                    .align(CenterHorizontally),
            )
            Text(text = entry.pokemonName,
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun PokedexRow(
    rowIndex : Int,
    entries : List<PokedexListEntry>,
    navigator: DestinationsNavigator
) {
    Column {
        Row {
            PokedexEntry(entry = entries[rowIndex *2],
                navigator = navigator,
                modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))
            if(entries.size >= rowIndex * 2 + 2) {
                PokedexEntry(entry = entries[rowIndex * 2 + 1],
                    navigator = navigator,
                    modifier = Modifier.weight(1f))
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun PokemonList(
    navigator: DestinationsNavigator,
    viewModel: PokedexListViewModel = hiltViewModel()
) {
    val pokemonList by remember { viewModel.pokemonList }
    val endReach by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }
    val isSearching by remember { viewModel.isSearching }

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        val itemCount = if(pokemonList.size % 2 == 0) {
            pokemonList.size / 2
        } else {
            pokemonList.size / 2 + 1
        }
        items(itemCount) {
            if(it >= itemCount - 1 && !endReach && !isLoading && !isSearching) {
                viewModel.loadPokedexPaginated()
            }
            PokedexRow(rowIndex = it, entries = pokemonList, navigator = navigator)
        }
    }
    Box(
        contentAlignment = Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if(isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        if(loadError.isNotEmpty()){
            RetrySection(error = loadError) {
                viewModel.loadPokedexPaginated()
            }
        }
    }
}

@Composable
fun PokeballAnimation() {
    val compositionResult : LottieCompositionResult = rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.pokeball_animation) )
    val animationProgress by animateLottieCompositionAsState(
        composition = compositionResult.value,
        isPlaying = true,
        iterations = LottieConstants.IterateForever)

    LottieAnimation(composition = compositionResult.value, progress = animationProgress,
        modifier = Modifier.size(160.dp))
}

@Composable
fun RetrySection(
    error : String,
    onRetry : () -> Unit
) {
    Column {
        PokeballAnimation()
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = "Retry loading...")
        }
    }
}