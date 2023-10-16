package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.models.Movie
import dev.techullurgy.movieticketbooking.domain.usecases.GetMovieByIdUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import java.lang.NumberFormatException

fun Route.getMovieByIdRoute() {
    val getMovieByIdUseCase by inject<GetMovieByIdUseCase>()

    get {
        val movieId = call.parameters["movie"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = GetMovieByIdFailureResponse("Movie id is not valid"), status = HttpStatusCode.BadRequest)
                return@get
            }
        } ?: run {
            call.respond(message = GetMovieByIdFailureResponse("Movie id is missing"), status = HttpStatusCode.BadRequest)
            return@get
        }

        val result = getMovieByIdUseCase(movieId)
        if(result is ServiceResult.Failure) {
            call.respond(message = GetMovieByIdFailureResponse(result.errorCode.message), status = HttpStatusCode.BadRequest)
        } else {
            call.respond(message = GetMovieByIdSuccessResponse(movie = (result as ServiceResult.Success).data), status = HttpStatusCode.OK)
        }
    }
}

@Serializable
private data class GetMovieByIdSuccessResponse(
    val movie: Movie,
    val success: Boolean = true
)

@Serializable
private data class GetMovieByIdFailureResponse(
    val message: String,
    val success: Boolean = false
)