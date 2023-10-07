package dev.techullurgy.movieticketbooking.domain.models

import dev.techullurgy.movieticketbooking.data.models.SeatCategory

data class Seat(
    val seatRow: Int,
    val seatColumn: Int,
    val seatPrice: Double,
    val seatCategory: SeatCategory,
    val seatQualifier: String
)
