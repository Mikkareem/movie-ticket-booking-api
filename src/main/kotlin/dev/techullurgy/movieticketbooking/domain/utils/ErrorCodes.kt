package dev.techullurgy.movieticketbooking.domain.utils

enum class ErrorCodes(val message: String) {
    MOVIE_NOT_EXISTS("The requested movie not exists in our database"),
    DATABASE_ERROR("The unexpected database error occurs"),
    SHOW_BOOKING_ALREADY_OPEN("The requested show tickets are already open"),
    BOOKING_NOT_YET_OPEN_FOR_SHOW("The Booking is not yet open for the particular show")
}