package dev.techullurgy.movieticketbooking.domain.utils

enum class ErrorCodes(val message: String) {
    MOVIE_NOT_EXISTS("The requested movie not exists in our database"),
    DATABASE_ERROR("The unexpected database error occurs"),
    SHOW_BOOKING_ALREADY_OPEN("The requested show tickets are already open"),
    BOOKING_NOT_YET_OPEN_FOR_SHOW("The Booking is not yet open for the particular show"),
    SEAT_ALREADY_BOOKED("The Requested seat is already booked for the selected show"),
    SEAT_ALREADY_CREATED("The Requested seat is already available"),
}