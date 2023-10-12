package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.BookingsDao
import dev.techullurgy.movieticketbooking.data.daos.SeatsDao
import dev.techullurgy.movieticketbooking.data.models.BookingStatus
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult

class GenerateTicketUseCase(
    private val bookingsDao: BookingsDao,
    private val seatsDao: SeatsDao
) {
    suspend operator fun invoke(bookingId: Long): ServiceResult<Long> {
        val bookableShowResult = bookingsDao.getBookableShowIdForBooking(bookingId)
        if (bookableShowResult is ServiceResult.Failure) {
            return ServiceResult.Failure(bookableShowResult.errorCode)
        }
        val customerResult = bookingsDao.getCustomerFromBooking(bookingId)
        if (customerResult is ServiceResult.Failure) {
            return ServiceResult.Failure(customerResult.errorCode)
        }
        val seatsResult = bookingsDao.getSeatsFromBooking(bookingId)
        if (seatsResult is ServiceResult.Failure) {
            return ServiceResult.Failure(seatsResult.errorCode)
        }

        val bookableShowId = (bookableShowResult as ServiceResult.Success).data
        val customerId = (customerResult as ServiceResult.Success).data
        val seats = (seatsResult as ServiceResult.Success).data

        val ticketResult = seatsDao.bookSeats(bookableShowId = bookableShowId, customerId = customerId, defaultSeatIds = seats)
        if (ticketResult is ServiceResult.Failure) {
            bookingsDao.updateStatus(bookingId, BookingStatus.FAILED)
            return ServiceResult.Failure(ticketResult.errorCode)
        }

        bookingsDao.updateStatus(bookingId, BookingStatus.SUCCESS)
        return ticketResult
    }
}