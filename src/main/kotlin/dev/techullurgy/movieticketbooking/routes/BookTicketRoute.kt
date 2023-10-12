package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.usecases.BookTicketUseCase
import dev.techullurgy.movieticketbooking.domain.usecases.CalculatePriceUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Route.bookTicketRoute() {
    val bookTicketUseCase by inject<BookTicketUseCase>()
    val calculatePriceUseCase by inject<CalculatePriceUseCase>()

    post {
        val theatreId = call.parameters["theatre"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(
                    message = BookTicketFailureResponse("Theatre id is invalid"),
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }
        } ?: run {
            call.respond(
                message = BookTicketFailureResponse("Theatre id is missing"),
                status = HttpStatusCode.BadRequest
            )
            return@post
        }

        val screenId = call.parameters["screen"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(
                    message = BookTicketFailureResponse("Screen id is invalid"),
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }
        } ?: run {
            call.respond(
                message = BookTicketFailureResponse("Screen id is missing"),
                status = HttpStatusCode.BadRequest
            )
            return@post
        }

        val showId = call.parameters["show"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(
                    message = BookTicketFailureResponse("Show id is invalid"),
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }
        } ?: run {
            call.respond(
                message = BookTicketFailureResponse("Show id is missing"),
                status = HttpStatusCode.BadRequest
            )
            return@post
        }

        val movieId = call.parameters["movie"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(
                    message = BookTicketFailureResponse("Movie id is invalid"),
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }
        } ?: run {
            call.respond(
                message = BookTicketFailureResponse("Movie id is missing"),
                status = HttpStatusCode.BadRequest
            )
            return@post
        }

        val customerId = call.parameters["customer"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(
                    message = BookTicketFailureResponse("Customer id is invalid"),
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }
        } ?: run {
            call.respond(
                message = BookTicketFailureResponse("Customer id is missing"),
                status = HttpStatusCode.BadRequest
            )
            return@post
        }

        val date = call.parameters["date"]?.let {
            try {
                val dateComponents = it.split("-")
                if (dateComponents.size != 3) throw Exception()

                val day = dateComponents[0].toInt()
                val month = dateComponents[1].toInt()
                val year = dateComponents[2].toInt()

                if (day <= 0 || month <= 0 || year <= 2000 || day > 31 || month > 12) {
                    throw Exception()
                }

                LocalDate(year, month, day)
            } catch (e: Exception) {
                call.respond(
                    message = BookTicketFailureResponse("Show Date is not valid"),
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }
        } ?: run {
            call.respond(
                message = BookTicketFailureResponse("Show Date is missing"),
                status = HttpStatusCode.BadRequest
            )
            return@post
        }

        val seatIds = call.parameters["seats"]?.let {
            try {
                val seatList = it.split(",")
                val seatsSet = HashSet<Long>()
                seatList.forEach { seat ->
                    seatsSet.add(seat.trim().toLong())
                }
                seatsSet
            } catch (e: Exception) {
                call.respond(
                    message = BookTicketFailureResponse("Seat Ids are not valid"),
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }
        } ?: run {
            call.respond(message = BookTicketFailureResponse("Seat Ids are missing"), status = HttpStatusCode.BadRequest)
            return@post
        }

        val bookTicketResult = bookTicketUseCase(
            theatreId = theatreId,
            screenId = screenId,
            showId = showId,
            movieId = movieId,
            customerId = customerId,
            date = date,
            seats = seatIds
        )

        if(bookTicketResult is ServiceResult.Failure) {
            call.respond(message = BookTicketFailureResponse(bookTicketResult.errorCode.message), status = HttpStatusCode.BadRequest)
            return@post
        }

        val bookingId = (bookTicketResult as ServiceResult.Success).data

        val calculatePriceResult = calculatePriceUseCase(
            theatreId = theatreId,
            screenId = screenId,
            showId = showId,
            movieId = movieId,
            date = date,
            seats = seatIds
        )

        if(calculatePriceResult is ServiceResult.Failure) {
            call.respond(message = BookTicketFailureResponse(calculatePriceResult.errorCode.message), status = HttpStatusCode.BadRequest)
            return@post
        }

        val totalPrice = (calculatePriceResult as ServiceResult.Success).data

        val needPaymentResponse = NeedPaymentResponse(
            bookingId = bookingId,
            totalPrice = totalPrice,
            paymentSuccessUrl = "/payment_success/$bookingId",
            paymentFailedUrl = "/payment_failed/$bookingId"
        )

        call.respond(message = needPaymentResponse, status = HttpStatusCode.OK)
    }
}

@Serializable
private data class BookTicketFailureResponse(
    val message: String,
    val success: Boolean = false
)

@Serializable
private data class NeedPaymentResponse(
    val bookingId: Long,
    val totalPrice: Double,
    val paymentSuccessUrl: String,
    val paymentFailedUrl: String
)