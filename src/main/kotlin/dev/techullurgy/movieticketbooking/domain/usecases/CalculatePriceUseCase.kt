package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.SeatsDao
import dev.techullurgy.movieticketbooking.data.daos.TheatresDao
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import kotlinx.datetime.LocalDate

class CalculatePriceUseCase(
    private val theatresDao: TheatresDao,
    private val seatsDao: SeatsDao
) {
    suspend operator fun invoke(
        theatreId: Long,
        screenId: Long,
        movieId: Long,
        showId: Long,
        date: LocalDate,
        seats: Set<Long>
    ): ServiceResult<Double> {
        val bookableShowIdResult = theatresDao.getBookableShowIdFor(theatreId, screenId, movieId, showId, date)
        if (bookableShowIdResult is ServiceResult.Failure) {
            return ServiceResult.Failure(bookableShowIdResult.errorCode)
        }
        val bookableShowId = (bookableShowIdResult as ServiceResult.Success).data
        return seatsDao.calculatePrice(bookableShowId, seats)
    }
}