package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.models.Theatre
import dev.techullurgy.movieticketbooking.domain.usecases.GetTheatresListForMovieUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import java.lang.NumberFormatException

fun Route.getTheatresListForMovieRoute() {
    val getTheatresListForMovieUseCase by inject<GetTheatresListForMovieUseCase>()

    get {
        val movieId = call.parameters["movie"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = GetTheatresListForMovieFailureResponse("Movie id is not valid"), status = HttpStatusCode.BadRequest)
                return@get
            }
        } ?: run {
            call.respond(message = GetTheatresListForMovieFailureResponse("Movie id is missing"), status = HttpStatusCode.BadRequest)
            return@get
        }

        val result = getTheatresListForMovieUseCase(movieId)
        if(result is ServiceResult.Failure) {
            call.respond(message = GetTheatresListForMovieFailureResponse(result.errorCode.message), status = HttpStatusCode.BadRequest)
        } else {
            call.respond(
                message = GetTheatresListForMovieSuccessResponse(
                    theatres = (result as ServiceResult.Success).data,
                    movieId = movieId
                ),
                status = HttpStatusCode.OK
            )
        }
    }
}

@Serializable
private data class GetTheatresListForMovieSuccessResponse(
    val theatres: List<Theatre>,
    val movieId: Long,
    val success: Boolean = true
)

@Serializable
private data class GetTheatresListForMovieFailureResponse(
    val message: String,
    val success: Boolean = false
)