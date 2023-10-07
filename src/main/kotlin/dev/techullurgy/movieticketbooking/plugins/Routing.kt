package dev.techullurgy.movieticketbooking.plugins

import dev.techullurgy.movieticketbooking.routes.movieSearchByNameRoute
import dev.techullurgy.movieticketbooking.routes.recommendedMoviesRoute
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting() {
    routing {
        route("/recommended_movies" /*GET*/) { recommendedMoviesRoute() }
        route("/search/MOVIE/{name}") { movieSearchByNameRoute() }
        route("/search/THEATRE/{name}") {}
        route("/theatre_list/{movie}" /*GET*/) {}
        route("/show_list/{movie}/{theatre}" /*GET*/) {}
        route("/seat_details/{movie}/{theatre}/{show}/{date}" /*GET*/) {}
        route("/book_ticket/{movie}/{theatre}/{show}/{date}/{seat_list}" /*POST*/) {}
        route("/my_tickets/{user_id}" /*GET*/) {}
        route("/ticket/{ticket_id}" /*GET*/) {}

        route("/replace") {
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