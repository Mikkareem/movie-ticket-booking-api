package dev.techullurgy.movieticketbooking.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ScreenFullDetail(
    val screen: Screen,
    val movieId: Long,
    val bookableShowFullDetails: List<BookableShowFullDetail>
)
