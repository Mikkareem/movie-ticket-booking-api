package dev.techullurgy.movieticketbooking.domain.utils

enum class ErrorCodes(val message: String) {
    MOVIE_NOT_EXISTS("The requested movie not exists in our database"),
    DATABASE_ERROR("The unexpected database error occurs"),
    SHOW_BOOKING_ALREADY_OPEN("The requested show tickets are already open"),
    BOOKING_NOT_YET_OPEN_FOR_SHOW("The Booking is not yet open for the particular show"),
    SEAT_ALREADY_BOOKED("The Requested seat is already booked for the selected show"),
    SEAT_ALREADY_CREATED("The Requested seat is already created"),
    SCREEN_ALREADY_AVAILABLE("The Requested screen is already available"),
    THEATRE_ALREADY_EXISTS("The Requested theatre already exists"),
    THEATRE_NOT_EXISTS("The Requested Theatre not exists in our database"),
    SCREEN_NOT_EXISTS("The Requested Screen not exists in our database"),
    SEAT_NOT_EXISTS("The Requested Seat not exists in our database"),
    SHOW_TIMING_NOT_EXISTS("The Requested Show Timing not exists in our database"),
    UNABLE_TO_OPEN_TICKETS_FOR_SHOW("Unable to Open the tickets for the selected show"),
    UNABLE_TO_BOOK_THE_TICKET("Unable to Book the ticket for the selected show"),
}