package dev.techullurgy.movieticketbooking.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class SeatWithStatus(
    val seat: Seat,
    val status: SeatStatus
)
