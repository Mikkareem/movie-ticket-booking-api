package dev.techullurgy.movieticketbooking.plugins

import dev.techullurgy.movieticketbooking.routes.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting() {
    routing {
        route("/create/THEATRE/{name}/{address}/{city}/{state}" /*POST*/) { createTheatreRoute() }
        route("/create/SCREEN/{theatre}/{name}/{rows}/{cols}" /*POST*/) { createScreenRoute() }
        route("/create/SEATS/{theatre}/{screen}" /*POST*/) { createSeatsRoute() }
        route("/create/SHOW/{theatre}/{screen}/{time}" /*POST*/) { createShowTimingRoute() }
        route("/update/SCREEN/{theatre}/{screen}/{movie}") { updateMovieFromScreenRoute() }
        route("/recommended_movies" /*GET*/) { recommendedMoviesRoute() }
        route("/bookable_movies" /*GET*/) { getBookableMoviesRoute() }
        route("/search/MOVIE/{name}" /*GET*/) { movieSearchByNameRoute() }
        route("/search/THEATRE/{name}" /*GET*/) {}
        route("find/MOVIE/{movie}") { getMovieByIdRoute() }
        route("/theatre_list/{movie}" /*GET*/) { getTheatresListForMovieRoute() }
        route("/show_list/{movie}/{theatre}" /*GET*/) { getBookableShowListFromTheatreRoute() }
        route("/seat_details/{theatre}/{screen}/{show}/{date}" /*GET*/) { getSeatDetailsRoute() }
        route("/open_tickets/{theatre}/{screen}/{show}/{date}" /*POST*/) { openTicketsForShowRoute() }
        route("/book_ticket" /*POST*/) { bookTicketRoute() }
        route("/my_tickets/{customer_id}" /*GET*/) {}
        route("/ticket/{ticket_id}" /*GET*/) {}
        route("/payment_success/{booking}" /*POST*/) { paymentSuccessRoute() }
        route("/payment_failed/{booking}" /*POST*/) { paymentFailureRoute() }

        route("/test") {
            get {
                val parameters = call.parameters
                if(parameters["prefix"] != null) {
                    call.respond("Super")
                } else {
                    call.respond("Bad")
                }
            }
        }
    }
}