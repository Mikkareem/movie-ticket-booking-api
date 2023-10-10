package dev.techullurgy.movieticketbooking.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Screen(
    val id: Long = -1,
    val name: String,
    val rows: Int,
    val cols: Int
)
