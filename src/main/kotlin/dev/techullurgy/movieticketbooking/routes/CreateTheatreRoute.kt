package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.usecases.CreateTheatreUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Route.createTheatreRoute() {
    val createTheatreUseCase by inject<CreateTheatreUseCase>()

    post {
        val name = call.parameters["name"] ?: run {
            call.respond(
                message = CreateTheatreFailureResponse("Theatre name is missing"), status = HttpStatusCode.BadRequest
            )
            return@post
        }
        val address = call.parameters["address"] ?: run {
            call.respond(
                message = CreateTheatreFailureResponse("Address name is missing"), status = HttpStatusCode.BadRequest
            )
            return@post
        }
        val city = call.parameters["city"] ?: run {
            call.respond(
                message = CreateTheatreFailureResponse("City name is missing"), status = HttpStatusCode.BadRequest
            )
            return@post
        }
        val state = call.parameters["state"] ?: run {
            call.respond(
                message = CreateTheatreFailureResponse("State name is missing"), status = HttpStatusCode.BadRequest
            )
            return@post
        }

        val result = createTheatreUseCase(name, address, city, state)
        if (result is ServiceResult.Failure) {
            call.respond(
                message = CreateTheatreFailureResponse(result.errorCode.message), status = HttpStatusCode.BadRequest
            )
            return@post
        }

        call.respond(
            message = CreateTheatreSuccessResponse(
                theatreId = (result as ServiceResult.Success).data, theatreName = name
            ),
            status = HttpStatusCode.OK
        )
    }
}

@Serializable
private data class CreateTheatreSuccessResponse(
    val theatreId: Long, val theatreName: String
)

@Serializable
private data class CreateTheatreFailureResponse(
    val error: String
)