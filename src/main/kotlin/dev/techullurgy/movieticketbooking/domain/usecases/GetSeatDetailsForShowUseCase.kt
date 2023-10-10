package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.SeatsDao
import dev.techullurgy.movieticketbooking.domain.models.Seat
import dev.techullurgy.movieticketbooking.domain.models.SeatStatus
import dev.techullurgy.movieticketbooking.domain.models.SeatWithStatus
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import dev.techullurgy.movieticketbooking.domain.utils.ext.containsSeat
import kotlinx.datetime.LocalDate

class GetSeatDetailsForShowUseCase(
    private val seatsDao: SeatsDao
) {
    suspend operator fun invoke(theatreId: Long, screenId: Long, bookableShowId: Long, orderDate: LocalDate): ServiceResult<List<SeatWithStatus>> {
        val result1 = seatsDao.getAllSeatsForTheShow(theatreId, screenId, bookableShowId, orderDate)
        if(result1 is ServiceResult.Failure) return ServiceResult.Failure(result1.errorCode)
        val allSeats = (result1 as ServiceResult.Success).data

        val result2 = seatsDao.getAvailableSeatsForTheShow(theatreId, screenId, bookableShowId, orderDate)
        if(result2 is ServiceResult.Failure) return ServiceResult.Failure(result2.errorCode)
        val availableSeats = (result2 as ServiceResult.Success).data

        val bookedSeats = allSeats.filterNot { availableSeats.containsSeat(it) }

        val result = mutableListOf<SeatWithStatus>().apply {
            addAll(bookedSeats.map { SeatWithStatus(it, SeatStatus.BOOKED) })
            addAll(availableSeats.map { SeatWithStatus(it, SeatStatus.AVAILABLE) })
        }.toList()

        return ServiceResult.Success(result)
    }
}