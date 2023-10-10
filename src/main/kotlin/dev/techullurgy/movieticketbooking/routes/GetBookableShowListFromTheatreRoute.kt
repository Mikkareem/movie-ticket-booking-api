package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.usecases.GetBookableShowListFromTheatreUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.lang.NumberFormatException

fun Route.getBookableShowListFromTheatre() {
    val getBookableShowListFromTheatreUseCase by inject<GetBookableShowListFromTheatreUseCase>()

    get {
        val theatreId = call.parameters["theatre"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = "Theatre id is not valid", status = HttpStatusCode.BadRequest)
                return@get
            }
        } ?: run {
            call.respond(message = "Theatre id is missing", status = HttpStatusCode.BadRequest)
            return@get
        }

        val movieId = call.parameters["movie"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = "Movie id is not valid", status = HttpStatusCode.BadRequest)
                return@get
            }
        } ?: run {
            call.respond(message = "Movie id is missing", status = HttpStatusCode.BadRequest)
            return@get
        }

        val result = getBookableShowListFromTheatreUseCase(theatreId, movieId)
        if(result is ServiceResult.Failure) {
            call.respond(message = result.errorCode.message, status = HttpStatusCode.BadRequest)
        } else {
            call.respond(message = (result as ServiceResult.Success).data, status = HttpStatusCode.OK)
        }
    }
}