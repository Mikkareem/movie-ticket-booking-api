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
        route("/recommended_movies" /*GET*/) { recommendedMoviesRoute() }
        route("/search/MOVIE/{name}" /*GET*/) { movieSearchByNameRoute() }
        route("/search/THEATRE/{name}" /*GET*/) {}
        route("/theatre_list/{movie}" /*GET*/) { getTheatresListForMovieRoute() }
        route("/show_list/{movie}/{theatre}" /*GET*/) { getBookableShowListFromTheatre() }
        route("/seat_details/{movie}/{theatre}/{show}/{date}" /*GET*/) { getSeatDetailsRoute() }
        route("/book_ticket/{movie}/{theatre}/{show}/{date}/{seat_list}" /*POST*/) {}
        route("/my_tickets/{user_id}" /*GET*/) {}
        route("/ticket/{ticket_id}" /*GET*/) {}

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