package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.models.TheatreFullDetail
import dev.techullurgy.movieticketbooking.domain.usecases.GetBookableShowListFromTheatreUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject
import java.lang.NumberFormatException

fun Route.getBookableShowListFromTheatreRoute() {
    val getBookableShowListFromTheatreUseCase by inject<GetBookableShowListFromTheatreUseCase>()

    get {
        val theatreId = call.parameters["theatre"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = GetBookableShowListFromTheatreFailureResponse("Theatre id is not valid"), status = HttpStatusCode.BadRequest)
                return@get
            }
        } ?: run {
            call.respond(message = GetBookableShowListFromTheatreFailureResponse("Theatre id is missing"), status = HttpStatusCode.BadRequest)
            return@get
        }

        val movieId = call.parameters["movie"]?.let {
            try {
                it.toLong()
            } catch (e: NumberFormatException) {
                call.respond(message = GetBookableShowListFromTheatreFailureResponse("Movie id is not valid"), status = HttpStatusCode.BadRequest)
                return@get
            }
        } ?: run {
            call.respond(message = GetBookableShowListFromTheatreFailureResponse("Movie id is missing"), status = HttpStatusCode.BadRequest)
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
                call.respond(message = GetBookableShowListFromTheatreFailureResponse("Order Date is not valid"), status = HttpStatusCode.BadRequest)
                return@get
            }
        } ?: run {
            call.respond(message = GetBookableShowListFromTheatreFailureResponse("Order date is missing"), status = HttpStatusCode.BadRequest)
            return@get
        }

        val result = getBookableShowListFromTheatreUseCase(theatreId, movieId, orderDate)
        if(result is ServiceResult.Failure) {
            call.respond(message = GetBookableShowListFromTheatreFailureResponse(result.errorCode.message), status = HttpStatusCode.BadRequest)
        } else {
            call.respond(message = GetBookableShowListFromTheatreSuccessResponse((result as ServiceResult.Success).data), status = HttpStatusCode.OK)
        }
    }
}

@Serializable
private data class GetBookableShowListFromTheatreSuccessResponse(
    val data: TheatreFullDetail,
    val success: Boolean = true
)


@Serializable
private data class GetBookableShowListFromTheatreFailureResponse(
    val message: String,
    val success: Boolean = false
)