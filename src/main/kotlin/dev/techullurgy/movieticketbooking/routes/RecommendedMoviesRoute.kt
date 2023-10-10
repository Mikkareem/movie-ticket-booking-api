package dev.techullurgy.movieticketbooking.routes

import dev.techullurgy.movieticketbooking.domain.usecases.GetRecommendedMoviesUseCase
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.recommendedMoviesRoute() {
    val getRecommendedMovies by inject<GetRecommendedMoviesUseCase>()

    get {
        when(val result = getRecommendedMovies()) {
            is ServiceResult.Success -> {
                call.respond(message = result.data, status = HttpStatusCode.OK)
            }
            is ServiceResult.Failure -> {
                call.respond(message = result.errorCode.message, status = HttpStatusCode.OK)
            }
        }
    }
}