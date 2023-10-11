package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.usecases.UpdateMovieInScreenUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import java.lang.NumberFormatException

fun Route.updateMovieFromScreenRoute() {
    val updateMovieInScreenUseCase by inject<UpdateMovieInScreenUseCase>()

    put {
        val theatreId = call.parameters["theatre"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = UpdateMovieFromScreenFailureResponse("Theatre id is not valid"), status = HttpStatusCode.BadRequest)
                return@put
            }
        } ?: run {
            call.respond(message = UpdateMovieFromScreenFailureResponse("Theatre id is missing"), status = HttpStatusCode.BadRequest)
            return@put
        }

        val screenId = call.parameters["screen"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = UpdateMovieFromScreenFailureResponse("Screen id is not valid"), status = HttpStatusCode.BadRequest)
                return@put
            }
        } ?: run {
            call.respond(message = UpdateMovieFromScreenFailureResponse("Screen id is missing"), status = HttpStatusCode.BadRequest)
            return@put
        }

        val movieId = call.parameters["movie"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = UpdateMovieFromScreenFailureResponse("Movie id is not valid"), status = HttpStatusCode.BadRequest)
                return@put
            }
        } ?: run {
            call.respond(message = UpdateMovieFromScreenFailureResponse("Movie id is missing"), status = HttpStatusCode.BadRequest)
            return@put
        }

        val result = updateMovieInScreenUseCase(theatreId, screenId, movieId)
        if(result is ServiceResult.Failure) {
            call.respond(message = UpdateMovieFromScreenFailureResponse(result.errorCode.message), status = HttpStatusCode.BadRequest)
        } else {
            call.respond(message = UpdateMovieFromScreenSuccessResponse((result as ServiceResult.Success).data), status = HttpStatusCode.OK)
        }
    }
}

@Serializable
private data class UpdateMovieFromScreenSuccessResponse(
    val success: Boolean = true
)

@Serializable
private data class UpdateMovieFromScreenFailureResponse(
    val message: String,
    val success: Boolean = false
)