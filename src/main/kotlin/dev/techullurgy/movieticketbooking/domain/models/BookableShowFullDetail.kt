package dev.techullurgy.movieticketbooking.domain.models

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class BookableShowFullDetail(
    val showId: Long,
    val showDate: LocalDate,
    val showTime: LocalTime
)
