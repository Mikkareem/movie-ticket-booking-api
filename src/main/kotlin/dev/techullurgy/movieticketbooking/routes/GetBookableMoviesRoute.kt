package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.models.Movie
import dev.techullurgy.movieticketbooking.domain.usecases.GetBookableMoviesUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Route.getBookableMoviesRoute() {
    val getBookableMoviesUseCase by inject<GetBookableMoviesUseCase>()

    get {
        val result = getBookableMoviesUseCase()
        if(result is ServiceResult.Failure) {
            call.respond(message = GetBookableMoviesFailureResponse(result.errorCode.message), status = HttpStatusCode.BadRequest)
        } else {
            call.respond(message = (result as ServiceResult.Success).data, status = HttpStatusCode.OK)
        }
    }
}

@Serializable
private data class GetBookableMoviesSuccessResponse(
    val movies: List<Movie>,
    val success: Boolean = true
)

private data class GetBookableMoviesFailureResponse(
    val message: String,
    val success: Boolean = false
)