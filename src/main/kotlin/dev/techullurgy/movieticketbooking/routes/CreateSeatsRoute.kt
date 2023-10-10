package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.usecases.CreateSeatsUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Route.createSeatsRoute() {
    val createSeatsUseCase by inject<CreateSeatsUseCase>()

    post {
        val theatreId = call.parameters["theatre"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(
                    message = CreateSeatsFailureResponse("Theatre id is invalid"),
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }
        } ?: run {
            call.respond(
                message = CreateSeatsFailureResponse("Theatre id is missing"),
                status = HttpStatusCode.BadRequest
            )
            return@post
        }

        val screenId = call.parameters["screen"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(
                    message = CreateSeatsFailureResponse("Screen id is invalid"),
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }
        } ?: run {
            call.respond(
                message = CreateSeatsFailureResponse("Screen id is missing"),
                status = HttpStatusCode.BadRequest
            )
            return@post
        }

        val result = createSeatsUseCase(theatreId, screenId)
        if (result is ServiceResult.Failure) {
            call.respond(
                message = CreateSeatsFailureResponse(message = result.errorCode.message),
                status = HttpStatusCode.BadRequest
            )
        } else {
            call.respond(
                message = CreateSeatsSuccessResponse(success = (result as ServiceResult.Success).data),
                status = HttpStatusCode.OK
            )
        }
    }
}

@Serializable
private data class CreateSeatsFailureResponse(
    val message: String
)

@Serializable
private data class CreateSeatsSuccessResponse(
    val success: Boolean
)