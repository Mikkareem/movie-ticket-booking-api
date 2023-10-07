package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.usecases.GetMovieByName
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.movieSearchByNameRoute() {
    val getMovieByName by inject<GetMovieByName>()

    get {
        val query = call.parameters["name"]!!
        when(val result = getMovieByName.invoke(query.lowercase())) {
            is ServiceResult.Success -> {
                call.respond(message = result.data, status = HttpStatusCode.OK)
            }
            is ServiceResult.Failure -> {
                call.respond(message = "Bad Request", status = HttpStatusCode.BadRequest)
            }
        }
    }
}