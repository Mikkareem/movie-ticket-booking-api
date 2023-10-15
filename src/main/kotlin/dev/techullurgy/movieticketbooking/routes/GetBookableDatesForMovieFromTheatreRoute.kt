package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.usecases.GetBookableDatesForMovieFromTheatreUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import java.lang.NumberFormatException

fun Route.getBookableDatesForMovieFromTheatreRoute() {
    val getBookableDatesForMovieFromTheatreUseCase by inject<GetBookableDatesForMovieFromTheatreUseCase>()

    get {
        val theatreId = call.parameters["theatre"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = GetBookableDatesForMovieFromTheatreFailureResponse("Theatre id is not valid"), status = HttpStatusCode.BadRequest)
                return@get
            }
        } ?: run {
            call.respond(message = GetBookableDatesForMovieFromTheatreFailureResponse("Theatre id is missing"), status = HttpStatusCode.BadRequest)
            return@get
        }

        val movieId = call.parameters["movie"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = GetBookableDatesForMovieFromTheatreFailureResponse("Movie id is not valid"), status = HttpStatusCode.BadRequest)
                return@get
            }
        } ?: run {
            call.respond(message = GetBookableDatesForMovieFromTheatreFailureResponse("Movie id is missing"), status = HttpStatusCode.BadRequest)
            return@get
        }

        val result = getBookableDatesForMovieFromTheatreUseCase(movieId, theatreId)
        if(result is ServiceResult.Failure) {
            call.respond(message = GetBookableDatesForMovieFromTheatreFailureResponse(result.errorCode.message), status = HttpStatusCode.BadRequest)
        } else {
            val dates = (result as ServiceResult.Success).data
            call.respond(message = GetBookableDatesForMovieFromTheatreSuccessResponse(dates), status = HttpStatusCode.OK)
        }
    }
}

@Serializable
private data class GetBookableDatesForMovieFromTheatreFailureResponse(
    val message: String,
    val success: Boolean = false
)

@Serializable
private data class GetBookableDatesForMovieFromTheatreSuccessResponse(
    val dates: Set<LocalDate>,
    val success: Boolean = true
)