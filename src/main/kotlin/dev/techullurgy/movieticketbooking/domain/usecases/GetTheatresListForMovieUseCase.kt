package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.TheatresDao
import dev.techullurgy.movieticketbooking.domain.models.Theatre
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult

class GetTheatresListForMovieUseCase(
    private val theatresDao: TheatresDao
) {
    suspend operator fun invoke(movieId: Long): ServiceResult<List<Theatre>> {
        return theatresDao.getTheatreListForMovieFromToday(movieId)
    }
}