package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.usecases.OpenTicketsForTheShowUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import java.lang.NumberFormatException

fun Route.openTicketsForShowRoute() {
    val openTicketsForTheShowUseCase by inject<OpenTicketsForTheShowUseCase>()

    post {
        val theatreId = call.parameters["theatre"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = OpenTicketsForShowFailureResponse("Theatre id is not valid"), status = HttpStatusCode.BadRequest)
                return@post
            }
        } ?: run {
            call.respond(message = OpenTicketsForShowFailureResponse("Theatre id is missing"), status = HttpStatusCode.BadRequest)
            return@post
        }

        val screenId = call.parameters["screen"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = OpenTicketsForShowFailureResponse("Screen id is not valid"), status = HttpStatusCode.BadRequest)
                return@post
            }
        } ?: run {
            call.respond(message = OpenTicketsForShowFailureResponse("Screen id is missing"), status = HttpStatusCode.BadRequest)
            return@post
        }

        val showId = call.parameters["show"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = OpenTicketsForShowFailureResponse("Show id is not valid"), status = HttpStatusCode.BadRequest)
                return@post
            }
        } ?: run {
            call.respond(message = OpenTicketsForShowFailureResponse("Show id is missing"), status = HttpStatusCode.BadRequest)
            return@post
        }

        val date = call.parameters["date"]?.let {
            try {
                val dateComponents = it.split("-")
                if(dateComponents.size != 3) throw Exception()

                val day = dateComponents[0].toInt()
                val month = dateComponents[1].toInt()
                val year = dateComponents[2].toInt()

                if(day <= 0 || month <= 0 || year <= 2000 || day > 31 || month > 12) {
                    throw Exception()
                }

                LocalDate(year, month, day)
            } catch (e: Exception) {
                call.respond(message = OpenTicketsForShowFailureResponse("Show Date is not valid"), status = HttpStatusCode.BadRequest)
                return@post
            }
        } ?: run {
            call.respond(message = OpenTicketsForShowFailureResponse("Show Date is missing"), status = HttpStatusCode.BadRequest)
            return@post
        }

        val result = openTicketsForTheShowUseCase(theatreId, screenId, showId, date)
        if(result is ServiceResult.Failure) {
            call.respond(message = OpenTicketsForShowFailureResponse(result.errorCode.message), status = HttpStatusCode.BadRequest)
        } else {
            call.respond(message = OpenTicketsForShowSuccessResponse(), status = HttpStatusCode.OK)
        }
    }
}

@Serializable
private data class OpenTicketsForShowFailureResponse(
    val message: String,
    val success: Boolean = false
)

@Serializable
private data class OpenTicketsForShowSuccessResponse(
    val success: Boolean = true
)