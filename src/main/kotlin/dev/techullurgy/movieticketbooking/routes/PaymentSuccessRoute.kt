package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.usecases.GenerateTicketUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.lang.NumberFormatException

fun Route.paymentSuccessRoute() {
    val generateTicketUseCase by inject<GenerateTicketUseCase>()

    post {
        val bookingId = call.parameters["booking"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = PaymentValidationResponse("Booking id is not valid"), status = HttpStatusCode.BadRequest)
                return@post
            }
        } ?: run {
            call.respond(message = PaymentValidationResponse("Booking id is missing"), status = HttpStatusCode.BadRequest)
            return@post
        }

        val ticketResult = generateTicketUseCase(bookingId)
        if(ticketResult is ServiceResult.Failure) {
            call.respond(message = PaymentValidationResponse(ticketResult.errorCode.message), status = HttpStatusCode.BadRequest)
        } else {
            val ticket = (ticketResult as ServiceResult.Success).data
            call.respond(message = PaymentSuccessResponse(ticket), status = HttpStatusCode.BadRequest)
        }
    }
}

private data class PaymentValidationResponse(
    val message: String,
    val success: Boolean = false
)

private data class PaymentSuccessResponse(
    val ticketId: Long,
    val success: Boolean = true
)