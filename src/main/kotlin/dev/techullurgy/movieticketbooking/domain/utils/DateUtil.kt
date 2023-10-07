package dev.techullurgy.movieticketbooking.domain.utils

import kotlinx.datetime.*

fun today(): LocalDate = currentInstant().date
fun currentTime(): LocalTime = currentInstant().time

fun currentInstant(): LocalDateTime {
    val instant: Instant = Clock.System.now()
    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}