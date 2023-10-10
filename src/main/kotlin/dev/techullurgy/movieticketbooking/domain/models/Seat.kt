package dev.techullurgy.movieticketbooking.domain.models

import dev.techullurgy.movieticketbooking.data.models.SeatCategory
import kotlinx.serialization.Serializable

@Serializable
data class Seat(
    val id: Long = -1,
    val seatRow: Int,
    val seatColumn: Int,
    val seatPrice: Double,
    val seatCategory: SeatCategory,
    val seatQualifier: String
)
