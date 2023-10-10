package dev.techullurgy.movieticketbooking.domain.utils.ext

import dev.techullurgy.movieticketbooking.domain.models.Seat

fun Iterable<Seat>.containsSeat(seat: Seat): Boolean {
    return any { it.seatRow == seat.seatRow && it.seatColumn == seat.seatColumn }
}