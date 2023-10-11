package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.SeatsDao
import dev.techullurgy.movieticketbooking.data.daos.TheatresDao
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import kotlinx.datetime.LocalDate

class BookTicketUseCase(
    private val theatresDao: TheatresDao,
    private val seatsDao: SeatsDao
) {
    suspend operator fun invoke(
        customerId: Long,
        theatreId: Long,
        screenId: Long,
        movieId: Long,
        showId: Long,
        date: LocalDate,
        seats: Set<Long>
    ): ServiceResult<Long> {
        val bookableShowIdResult = theatresDao.getBookableShowIdFor(theatreId, screenId, movieId, showId, date)
        if (bookableShowIdResult is ServiceResult.Failure) {
            return ServiceResult.Failure(bookableShowIdResult.errorCode)
        }
        val bookableShowId = (bookableShowIdResult as ServiceResult.Success).data

        seats.forEach { seat ->
            val result = seatsDao.isSeatAvailableForBooking(theatreId, screenId, bookableShowId, seat)
            if(result is ServiceResult.Failure) {
                return ServiceResult.Failure(result.errorCode)
            }
        }
        val seatsStr = seats.joinToString(",") { "-${it}-" }

        // TODO: Needs to add Payment Process with booking id, and amount
        return theatresDao.registerBooking(bookableShowId = bookableShowId, customer = customerId, seats = seatsStr)
    }
}