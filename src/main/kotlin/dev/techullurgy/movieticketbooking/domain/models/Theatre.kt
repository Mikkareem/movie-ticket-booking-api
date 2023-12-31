package dev.techullurgy.movieticketbooking.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Theatre(
    val id: Long = -1,
    val name: String,
    val address: String,
    val city: String,
    val state: String
)
