package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.TheatresDao
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import kotlinx.datetime.LocalDate

class GetBookableDatesForMovieFromTheatreUseCase(
    private val theatresDao: TheatresDao
) {
    suspend operator fun invoke(movieId: Long, theatreId: Long): ServiceResult<Set<LocalDate>> {
        return theatresDao.getBookableDatesFromTheatreForMovie(theatreId, movieId)
    }
}