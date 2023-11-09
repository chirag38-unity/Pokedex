package com.example.pokedex.ui.screens.pokemondetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import com.example.pokedex.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.pokedex.dataclasses.local.PokemonListItem
import com.example.pokedex.dataclasses.remote.responses.Pokemon
import com.example.pokedex.dataclasses.remote.responses.Type
import com.example.pokedex.util.Resource
import com.example.pokedex.util.parseStatToAbbr
import com.example.pokedex.util.parseStatToColor
import com.example.pokedex.util.parseTypeToColor
import com.example.pokedex.viewmodels.PokemonDetailViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import kotlin.math.round

@Destination
@Composable
fun PokemonDetailScreen(
    navigator : DestinationsNavigator,
    pokemon: PokemonListItem,

    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    val topPadding : Dp = 20.dp
    val pokemonImageSize : Dp = 200.dp
    val pokemonInfo = produceState<Resource<Pokemon>>(initialValue = Resource.Loading() ) {
        value = viewModel.getPokemonInfo(pokemon.name)
    }.value
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(pokemon.dominantColor))
            .padding(bottom = 16.dp)
    ) {

        PokemonDetailTopSection(
            navigator = navigator,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.2f)
                .align(Alignment.TopCenter))
        PokemonDetailStateWrapper(
            pokemonInfo = pokemonInfo,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topPadding + pokemonImageSize / 2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            loadingModifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
                .padding(
                    top = topPadding + pokemonImageSize / 2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ))
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            if(pokemonInfo is Resource.Success) {
                SubcomposeAsyncImage(model = ImageRequest.Builder(LocalContext.current)
                    .data(pokemonInfo.data?.sprites?.front_default)
                    .crossfade(true)
                    .build(),
                    contentDescription = pokemonInfo.data?.name,
                    modifier = Modifier
                        .size(pokemonImageSize)
                        .offset(y = topPadding)
                )
            }
        }
    }
}

@Composable
fun PokemonDetailTopSection(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier.background(
            Brush.verticalGradient(
                listOf(Color.Black, Color.Transparent)
            )
        )
    ) {
        Icon(imageVector = Icons.Default.ArrowBack ,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(36.dp)
                .offset(16.dp, 16.dp)
                .clickable {
                    navigator.popBackStack()
                })
    }
}

@Composable
fun PokemonDetailStateWrapper(
    pokemonInfo : Resource<Pokemon>,
    modifier: Modifier = Modifier,
    loadingModifier : Modifier = Modifier
) {

    when(pokemonInfo) {
        is Resource.Success -> {
            PokemonDetailSection(
                pokemonInfo = pokemonInfo.data!!,
                modifier = modifier.offset( y = (-20).dp))
        }
        is Resource.Error -> {
            Text(text = pokemonInfo.message!!,
                color = Color.Red,
                modifier = modifier)
        }
        is Resource.Loading -> {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = loadingModifier
            )
        }
    }

}

@Composable
fun PokemonDetailSection(
    pokemonInfo: Pokemon,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .offset(y = 100.dp)
            .verticalScroll(scrollState)
    ) {
        Text(text = "#${pokemonInfo.id} ${pokemonInfo.name.capitalize(Locale.ROOT)}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.onSurface)
        PokemonTypeSection(types = pokemonInfo.types)
        PokemonDetailDataSection(pokemonWeight = pokemonInfo.weight,
            pokemonHeight = pokemonInfo.height)
        Spacer(modifier = Modifier.height(8.dp))
        PokemonBaseStats(pokemonInfo = pokemonInfo)
    }

}

@Composable
fun PokemonTypeSection(
    types : List<Type>
) {

    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
    ) {
        for ( type in types) {
            Box (
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .clip(CircleShape)
                    .background(parseTypeToColor(type))
                    .height(35.dp)
            ) {
                Text(
                    text = type.type.name.capitalize(Locale.ROOT),
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun PokemonDetailDataSection(
    pokemonWeight : Int,
    pokemonHeight : Int,
    sectionHeight : Dp = 80.dp
) {

    val pokemonWeightInKgs = remember {
        round(pokemonWeight * 100f ) / 1000f
    }
    val pokemonHeightInMts = remember {
        round(pokemonHeight * 100f ) / 1000f
    }

    Row (
        modifier = Modifier.fillMaxWidth()
    ) {
        PokemonDetailDataItem(dataValue = pokemonWeightInKgs, dataUnit = "Kgs",
            dataIcon = painterResource(id = R.drawable.ic_weight ),
            modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier
            .size(1.dp, sectionHeight)
            .background(Color.LightGray))
        PokemonDetailDataItem(dataValue = pokemonHeightInMts, dataUnit = "Mts",
            dataIcon = painterResource(id = R.drawable.ic_height ),
            modifier = Modifier.weight(1f))
    }

}

@Composable
fun PokemonDetailDataItem(
    dataValue : Float,
    dataUnit : String,
    dataIcon : Painter,
    modifier: Modifier = Modifier
) {

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Icon(painter = dataIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "$dataValue $dataUnit",
            color = MaterialTheme.colorScheme.onSurface)
    }
    
}

@Composable
fun PokemonStat(
    statName : String,
    statValue : Int,
    statMaxvalue : Int,
    statColor : Color,
    height : Dp = 28.dp,
    animDuration : Int = 1000,
    animDelay : Int = 0
) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val currPercent = animateFloatAsState(
        targetValue = if(animationPlayed) {
            statValue / statMaxvalue .toFloat()
        } else 0f, label = "StatAnimator",
        animationSpec = tween(animDuration, animDelay)
    )
    LaunchedEffect(key1 = true ) {
        animationPlayed = true
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(
                if (isSystemInDarkTheme()) {
                    Color(0xFF505050)
                } else {
                    Color.LightGray
                }
            )
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(currPercent.value)
                .clip(CircleShape)
                .background(statColor)
                .padding(horizontal = 8.dp)
        ) {
            Text(text = statName, fontWeight = FontWeight.Bold)
            Text(text = (currPercent.value * statMaxvalue).toString(), fontWeight = FontWeight.Bold)
        }
    }

}

@Composable
fun PokemonBaseStats(
    pokemonInfo: Pokemon,
    animDelay : Int = 100
) {
    val maxBaseStat = remember {
        pokemonInfo.stats.maxOf { it.base_stat }
    }
    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Base Stats...",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        for ( i in pokemonInfo.stats.indices) {
            val stat = pokemonInfo.stats[i]
            PokemonStat(statName = parseStatToAbbr(stat),
                statValue = stat.base_stat,
                statMaxvalue = maxBaseStat ,
                statColor = parseStatToColor(stat),
                animDelay = i * animDelay)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}