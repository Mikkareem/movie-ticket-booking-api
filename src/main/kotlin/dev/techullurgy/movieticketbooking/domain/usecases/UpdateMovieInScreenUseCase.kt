package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.TheatresDao
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult

class UpdateMovieInScreenUseCase(
    private val theatresDao: TheatresDao
) {
    suspend operator fun invoke(theatreId: Long, screenId: Long, movieId: Long): ServiceResult<Boolean> {
        return theatresDao.updateMovieFromScreen(theatreId, screenId, movieId)
    }
}