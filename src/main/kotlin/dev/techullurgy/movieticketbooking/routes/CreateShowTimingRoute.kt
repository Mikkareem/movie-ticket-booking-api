package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.usecases.CreateShowTimingUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Route.createShowTimingRoute() {
    val createShowTimingUseCase by inject<CreateShowTimingUseCase>()

    post {
        val theatreId = call.parameters["theatre"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(
                    message = CreateShowFailureResponse("Theatre id is invalid"),
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }
        } ?: run {
            call.respond(
                message = CreateShowFailureResponse("Theatre id is missing"),
                status = HttpStatusCode.BadRequest
            )
            return@post
        }

        val screenId = call.parameters["screen"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(
                    message = CreateShowFailureResponse("Screen id is invalid"),
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }
        } ?: run {
            call.respond(
                message = CreateShowFailureResponse("Screen id is missing"),
                status = HttpStatusCode.BadRequest
            )
            return@post
        }

        val time = call.parameters["time"]?.let {
            try {
                val timeComponents = it.split(":")
                if(timeComponents.size != 2) {
                    throw Exception()
                }

                val hour = timeComponents[0].toInt()
                val minute = timeComponents[1].toInt()

                if(hour > 23 || minute > 59 || hour < 0 || minute < 0) {
                    throw Exception()
                }
                LocalTime(hour, minute)
            } catch (e: Exception) {
                call.respond(
                    message = CreateShowFailureResponse("Show time is invalid"),
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }
        } ?: run {
            call.respond(
                message = CreateShowFailureResponse("Show time is missing"),
                status = HttpStatusCode.BadRequest
            )
            return@post
        }

        val result = createShowTimingUseCase(theatreId, screenId, time)
        if(result is ServiceResult.Failure) {
            call.respond(message = CreateShowFailureResponse(result.errorCode.message), status = HttpStatusCode.BadRequest)
        } else {
            call.respond(message = (result as ServiceResult.Success).data, status = HttpStatusCode.OK)
        }
    }
}

@Serializable
private data class CreateShowFailureResponse(
    val message: String
)