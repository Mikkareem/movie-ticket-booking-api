package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.usecases.CreateScreenUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Route.createScreenRoute() {
    val createScreenUseCase by inject<CreateScreenUseCase>()

    post {
        val theatreId = call.parameters["theatre"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(
                    message = CreateScreenFailureResponse("Theatre id is invalid"),
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }
        } ?: run {
            call.respond(
                message = CreateScreenFailureResponse("Theatre id is missing"),
                status = HttpStatusCode.BadRequest
            )
            return@post
        }

        val name = call.parameters["name"] ?: run {
            call.respond(
                message = CreateScreenFailureResponse("Screen name is missing"),
                status = HttpStatusCode.BadRequest
            )
            return@post
        }

        val noOfRows = call.parameters["rows"]?.let {
            try {
                it.toInt()
            } catch (e: NumberFormatException) {
                call.respond(
                    message = CreateScreenFailureResponse("No. of Rows is invalid"),
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }
        } ?: run {
            call.respond(
                message = CreateScreenFailureResponse("No. of Rows is missing"),
                status = HttpStatusCode.BadRequest
            )
            return@post
        }

        val noOfCols = call.parameters["cols"]?.let {
            try {
                it.toInt()
            } catch (e: NumberFormatException) {
                call.respond(
                    message = CreateScreenFailureResponse("No. of Cols is invalid"),
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }
        } ?: run {
            call.respond(
                message = CreateScreenFailureResponse("No. of Cols is missing"),
                status = HttpStatusCode.BadRequest
            )
            return@post
        }

        val result = createScreenUseCase(theatreId, name, noOfRows, noOfCols)
        if(result is ServiceResult.Failure) {
            call.respond(
                message = CreateScreenFailureResponse(message = result.errorCode.message),
                status = HttpStatusCode.BadRequest
            )
        } else {
            call.respond(
                message = CreateScreenSuccessResponse(screenId = (result as ServiceResult.Success).data),
                status = HttpStatusCode.OK
            )
        }
    }
}

@Serializable
private data class CreateScreenFailureResponse(
    val message: String,
    val success: Boolean = false
)

@Serializable
private data class CreateScreenSuccessResponse(
    val screenId: Long,
    val success: Boolean = true
)