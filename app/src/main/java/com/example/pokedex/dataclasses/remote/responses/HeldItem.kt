package com.example.pokedex.dataclasses.remote.responses

data class HeldItem(
    val item: Item,
    val version_details: List<VersionDetail>
)