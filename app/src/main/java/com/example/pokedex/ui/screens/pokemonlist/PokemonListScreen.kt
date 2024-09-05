package com.example.pokedex.ui.screens.pokemonlist

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.example.pokedex.ui.rememberWindowInfo
import com.example.pokedex.ui.screens.destinations.PokemonDetailScreenDestination
import com.example.pokedex.ui.theme.RobotoCondensed
import com.example.pokedex.viewmodels.PokedexListEntry
import com.example.pokedex.viewmodels.PokedexListViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.Locale

@OptIn(ExperimentalSharedTransitionApi::class)
@Destination(start = true)
@Composable
fun SharedTransitionScope.PokemonListScreen(
    navigator : DestinationsNavigator,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: PokedexListViewModel = hiltViewModel()
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {

        val windowInfo = rememberWindowInfo()
        val searchText = rememberSaveable {
            mutableStateOf("")
        }
        val scrollState = rememberLazyGridState()

        if(windowInfo.screenHeight > windowInfo.screenWidth) {
            PortraitListScreen(navigator, viewModel, searchText, scrollState, animatedVisibilityScope)
        } else {
            LandscapeListScreen(navigator = navigator, viewModel = viewModel, searchText, scrollState, animatedVisibilityScope)
        }

    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PortraitListScreen(
    navigator: DestinationsNavigator,
    viewModel: PokedexListViewModel,
    searchText: MutableState<String>,
    scrollState: LazyGridState,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Column {
        Spacer(modifier = Modifier.height(20.dp))
        Image(painter = painterResource(id = R.drawable.pokemon_theme_image ),
            contentDescription = "Pokemon Theme Logo",
            modifier = Modifier
                .fillMaxWidth(.4f)
                .align(CenterHorizontally))
        SearchBar(
            hint = "What are you looking for...",
            searchText = searchText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ){
            viewModel.searchPokedexList(it)
        }
        Spacer(modifier = Modifier.height(16.dp))
        PokemonListVertical(navigator = navigator, scrollState, animatedVisibilityScope)
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.LandscapeListScreen(
    navigator: DestinationsNavigator,
    viewModel: PokedexListViewModel,
    searchText: MutableState<String>,
    scrollState: LazyGridState,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Column {
        Spacer(modifier = Modifier.height(12.dp))
        Row ( ) {
            Image(painter = painterResource(id = R.drawable.pokemon_theme_image ),
                contentDescription = "Pokemon Theme Logo",
                modifier = Modifier
                    .padding(top = 15.dp, start = 15.dp)
                    .fillMaxWidth(.1f))
            SearchBar(
                hint = "What are you looking for...",
                searchText = searchText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ){
                viewModel.searchPokedexList(it)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        PokemonListHorizontal(navigator = navigator, scrollState, animatedVisibilityScope)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchText: MutableState<String>,
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier){
        TextField(
            value = searchText.value,
            onValueChange = {
                searchText.value = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            placeholder = {
                Text(text = hint, color = Color.Gray)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search"
                )
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                }
            ),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .focusable()
                .focusRequester(focusRequester)
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PokedexEntry(
    entry : PokedexListEntry,
    navigator : DestinationsNavigator,
    animatedVisibilityScope: AnimatedVisibilityScope,
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
            .padding(5.dp)
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
                            name = entry.pokemonName.toLowerCase(Locale.ROOT) ?: "",
                            imageUrl = entry.imageUrl
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
                    .sharedElement(
                        state = rememberSharedContentState(key = entry.imageUrl),
                        animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = 1000)
                        }
                    )
                    .size(120.dp)
                    .align(CenterHorizontally),
            )
            Text(
                text = entry.pokemonName,
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .sharedElement(
                        state = rememberSharedContentState(key = entry.pokemonName),
                        animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = 1000)
                        }
                    )
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PokemonListVertical(
    navigator: DestinationsNavigator,
    scrollState: LazyGridState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: PokedexListViewModel = hiltViewModel()
) {
    val pokemonList by remember { viewModel.pokemonList }
    val endReach by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }
    val isSearching by remember { viewModel.isSearching }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.firstVisibleItemIndex + scrollState.layoutInfo.visibleItemsInfo.size }
            .collect { visibleItemCount ->
                if (visibleItemCount >= pokemonList.size && !endReach && !isLoading && !isSearching) {
                    viewModel.loadPokedexPaginated()
                }
            }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        state = scrollState
    ) {

        items(pokemonList, key = { entry -> entry.pokemonName }) { entry ->
            PokedexEntry(
                entry = entry,
                navigator = navigator,
                animatedVisibilityScope,
                modifier = Modifier.animateItemPlacement(animationSpec = tween(durationMillis = 300))
            )
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PokemonListHorizontal(
    navigator: DestinationsNavigator,
    scrollState: LazyGridState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: PokedexListViewModel = hiltViewModel()
) {

    val pokemonList by remember { viewModel.pokemonList }
    val endReach by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }
    val isSearching by remember { viewModel.isSearching }


    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.firstVisibleItemIndex + scrollState.layoutInfo.visibleItemsInfo.size }
            .collect { visibleItemCount ->
                if (visibleItemCount >= pokemonList.size && !endReach && !isLoading && !isSearching) {
                    viewModel.loadPokedexPaginated()
                }
            }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        state = scrollState
    ) {

        items(pokemonList, key = { entry -> entry.pokemonName }) { entry ->
            PokedexEntry(
                entry = entry,
                navigator = navigator,
                animatedVisibilityScope,
                modifier = Modifier.animateItemPlacement(animationSpec = tween(durationMillis = 300))
            )
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