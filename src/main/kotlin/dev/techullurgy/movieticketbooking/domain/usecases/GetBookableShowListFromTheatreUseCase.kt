package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.TheatresDao
import dev.techullurgy.movieticketbooking.domain.models.TheatreFullDetail
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import kotlinx.datetime.LocalDate

class GetBookableShowListFromTheatreUseCase(
    private val theatresDao: TheatresDao
) {
    suspend operator fun invoke(theatreId: Long, movieId: Long, date: LocalDate): ServiceResult<TheatreFullDetail> {
        return theatresDao.getBookableShowsFromTheatreForMovie(theatreId, movieId, date)
    }
}