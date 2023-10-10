package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.TheatresDao
import dev.techullurgy.movieticketbooking.data.models.SeatCategory
import dev.techullurgy.movieticketbooking.domain.models.Seat
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult

class CreateSeatsUseCase(
    private val theatresDao: TheatresDao
) {
    suspend operator fun invoke(theatreId: Long, screenId: Long): ServiceResult<Boolean> {
        val result = theatresDao.getScreenByIdFromTheatre(theatreId, screenId)

        if (result is ServiceResult.Failure) {
            return ServiceResult.Failure(result.errorCode)
        }
        val screen = (result as ServiceResult.Success).data
        val rows = screen.rows
        val cols = screen.cols

        val seats = mutableListOf<Seat>().apply {
            for (i in 0..<rows) {
                if (i == 4 || i == 16) continue
                for (j in 0..<cols) {
                    if (j == 4 || j == 20) continue

                    val seatCategory = if (i <= 4) {
                        SeatCategory.FIRST_CLASS
                    } else if (i <= 16) {
                        SeatCategory.SECOND_CLASS
                    } else {
                        SeatCategory.BALCONY
                    }

                    val seatPrice = when (seatCategory) {
                        SeatCategory.FIRST_CLASS -> 200.20
                        SeatCategory.SECOND_CLASS -> 245.60
                        SeatCategory.BALCONY -> 300.10
                    }

                    val seatQualifier = "R${i}C${j}"

                    add(
                        Seat(
                            seatRow = i,
                            seatColumn = j,
                            seatPrice = seatPrice,
                            seatQualifier = seatQualifier,
                            seatCategory = seatCategory
                        )
                    )
                }
            }
        }.toList()

        println(seats)

        return theatresDao.addSeats(theatreId, screenId, seats)
    }
}