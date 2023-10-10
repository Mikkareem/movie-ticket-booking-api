package dev.techullurgy.movieticketbooking.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class TheatreFullDetail(
    val theatre: Theatre,
    val screenDetails: List<ScreenFullDetail>
)
