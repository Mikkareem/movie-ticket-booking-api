package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.models.SeatWithStatus
import dev.techullurgy.movieticketbooking.domain.usecases.GetSeatDetailsForShowUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDate
import org.koin.ktor.ext.inject
import java.lang.NumberFormatException

fun Route.getSeatDetailsRoute() {
    val getSeatDetailsForShow by inject<GetSeatDetailsForShowUseCase>()

    get {
        val theatreId = call.parameters["theatreId"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = "Theatre id is not valid", status = HttpStatusCode.BadRequest)
                return@get
            }
        } ?: run {
            call.respond(message = "Theatre id is missing", status = HttpStatusCode.BadRequest)
            return@get
        }

        val screenId = call.parameters["screenId"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = "Screen id is not valid", status = HttpStatusCode.BadRequest)
                return@get
            }
        } ?: run {
            call.respond(message = "Screen id is missing", status = HttpStatusCode.BadRequest)
            return@get
        }

        val showId = call.parameters["showId"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = "Show id is not valid", status = HttpStatusCode.BadRequest)
                return@get
            }
        } ?: run {
            call.respond(message = "Show id is missing", status = HttpStatusCode.BadRequest)
            return@get
        }

        val orderDate = call.parameters["orderDate"]?.let { LocalDate.parse(it) } ?: run {
            call.respond(message = "Order date is missing", status = HttpStatusCode.BadRequest)
            return@get
        }

        val seatDetailsResult = getSeatDetailsForShow(
            theatreId = theatreId, screenId = screenId, bookableShowId = showId, orderDate = orderDate
        )

        if (seatDetailsResult is ServiceResult.Failure) {
            call.respond(message = seatDetailsResult.errorCode.message, status = HttpStatusCode.BadRequest)
            return@get
        }

        val response = GetSeatDetailsResponse(
            theatreId = theatreId,
            screenId = screenId,
            showId = showId,
            orderDate = orderDate,
            seats = (seatDetailsResult as ServiceResult.Success).data
        )
        call.respond(message = response, status = HttpStatusCode.OK)
    }
}

private data class GetSeatDetailsResponse(
    val theatreId: Long, val screenId: Long, val showId: Long, val orderDate: LocalDate, val seats: List<SeatWithStatus>
)