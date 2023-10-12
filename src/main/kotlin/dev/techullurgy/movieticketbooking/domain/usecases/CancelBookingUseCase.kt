package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.BookingsDao
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult

class CancelBookingUseCase(
    private val bookingsDao: BookingsDao
) {
    suspend operator fun invoke(bookingId: Long): ServiceResult<Boolean> {
        return bookingsDao.cancelBooking(bookingId)
    }
}