package com.example.pokedex.dataclasses.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PokemonListItem(
    val dominantColor : Int,
    val name : String,
    val imageUrl : String
) : Parcelable
