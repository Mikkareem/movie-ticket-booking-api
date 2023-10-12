package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.usecases.CancelBookingUseCase
import dev.techullurgy.movieticketbooking.domain.usecases.GenerateTicketUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.lang.NumberFormatException

fun Route.paymentFailureRoute() {
    val cancelBookingUseCase by inject<CancelBookingUseCase>()

    post {
        val bookingId = call.parameters["booking"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = PaymentFailureValidationResponse("Booking id is not valid"), status = HttpStatusCode.BadRequest)
                return@post
            }
        } ?: run {
            call.respond(message = PaymentFailureValidationResponse("Booking id is missing"), status = HttpStatusCode.BadRequest)
            return@post
        }

        val ticketResult = cancelBookingUseCase(bookingId)
        if(ticketResult is ServiceResult.Failure) {
            call.respond(message = PaymentFailureValidationResponse(ticketResult.errorCode.message), status = HttpStatusCode.BadRequest)
        } else {
            val isCancelled = (ticketResult as ServiceResult.Success).data
            call.respond(message = isCancelled, status = HttpStatusCode.BadRequest)
        }
    }
}

private data class PaymentFailureValidationResponse(
    val message: String,
    val success: Boolean = false
)