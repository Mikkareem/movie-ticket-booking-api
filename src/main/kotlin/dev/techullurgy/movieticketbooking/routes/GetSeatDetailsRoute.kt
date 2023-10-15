package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.models.SeatWithStatus
import dev.techullurgy.movieticketbooking.domain.usecases.GetSeatDetailsForShowUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import java.lang.NumberFormatException

fun Route.getSeatDetailsRoute() {
    val getSeatDetailsForShow by inject<GetSeatDetailsForShowUseCase>()

    get {
        val theatreId = call.parameters["theatre"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = GetSeatDetailsFailureResponse("Theatre id is not valid"), status = HttpStatusCode.BadRequest)
                return@get
            }
        } ?: run {
            call.respond(message = GetSeatDetailsFailureResponse("Theatre id is missing"), status = HttpStatusCode.BadRequest)
            return@get
        }

        val screenId = call.parameters["screen"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = GetSeatDetailsFailureResponse("Screen id is not valid"), status = HttpStatusCode.BadRequest)
                return@get
            }
        } ?: run {
            call.respond(message = GetSeatDetailsFailureResponse("Screen id is missing"), status = HttpStatusCode.BadRequest)
            return@get
        }

        val showId = call.parameters["show"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = GetSeatDetailsFailureResponse("Show id is not valid"), status = HttpStatusCode.BadRequest)
                return@get
            }
        } ?: run {
            call.respond(message = GetSeatDetailsFailureResponse("Show id is missing"), status = HttpStatusCode.BadRequest)
            return@get
        }

        val orderDate = call.parameters["date"]?.let {
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
                call.respond(message = GetSeatDetailsFailureResponse("Order Date is not valid"), status = HttpStatusCode.BadRequest)
                return@get
            }
        } ?: run {
            call.respond(message = GetSeatDetailsFailureResponse("Order date is missing"), status = HttpStatusCode.BadRequest)
            return@get
        }

        val seatDetailsResult = getSeatDetailsForShow(
            theatreId = theatreId, screenId = screenId, bookableShowId = showId, orderDate = orderDate
        )

        if (seatDetailsResult is ServiceResult.Failure) {
            call.respond(message = GetSeatDetailsFailureResponse(seatDetailsResult.errorCode.message), status = HttpStatusCode.BadRequest)
            return@get
        }

        val response = GetSeatDetailsSuccessResponse(
            theatreId = theatreId,
            screenId = screenId,
            showId = showId,
            orderDate = orderDate,
            seats = (seatDetailsResult as ServiceResult.Success).data
        )
        call.respond(message = response, status = HttpStatusCode.OK)
    }
}

@Serializable
private data class GetSeatDetailsSuccessResponse(
    val theatreId: Long,
    val screenId: Long,
    val showId: Long,
    val orderDate: LocalDate,
    val seats: List<SeatWithStatus>,
    val success: Boolean = true
)

@Serializable
private data class GetSeatDetailsFailureResponse(
    val message: String,
    val success: Boolean = false
)