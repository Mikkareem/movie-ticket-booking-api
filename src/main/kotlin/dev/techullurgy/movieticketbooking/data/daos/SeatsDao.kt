package dev.techullurgy.movieticketbooking.data.daos

import dev.techullurgy.movieticketbooking.data.schema.BookableShows
import dev.techullurgy.movieticketbooking.data.schema.ConfirmedSeats
import dev.techullurgy.movieticketbooking.data.schema.DefaultSeats
import dev.techullurgy.movieticketbooking.domain.models.Seat
import dev.techullurgy.movieticketbooking.domain.utils.ErrorCodes
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import dev.techullurgy.movieticketbooking.plugins.dbQuery
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import java.sql.SQLIntegrityConstraintViolationException

interface SeatsDao {
    suspend fun addSeat(theatreId: Long, screenId: Long, seat: Seat)

    suspend fun bookSeat(bookableShowId: Long, customerId: Long, date: LocalDate, defaultSeatId: Long)

    suspend fun getAvailableSeatsForTheShow(
        theatreId: Long,
        screenId: Long,
        showId: Long,
        date: LocalDate
    ): ServiceResult<List<Seat>>
}

class SeatsDaoImpl : SeatsDao {
    override suspend fun addSeat(theatreId: Long, screenId: Long, seat: Seat) {
        TODO("Not yet implemented")
    }

    override suspend fun bookSeat(bookableShowId: Long, customerId: Long, date: LocalDate, defaultSeatId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getAvailableSeatsForTheShow(
        theatreId: Long,
        screenId: Long,
        showId: Long,
        date: LocalDate
    ): ServiceResult<List<Seat>> {
        return try {
            dbQuery {
                val bookableShowId = BookableShows
                    .slice(BookableShows.id).select {
                        (BookableShows.showId eq showId) and (BookableShows.screenId eq screenId) and
                                (BookableShows.theatreId eq theatreId) and (BookableShows.showDate eq date)
                    }.map { it[BookableShows.id] }.firstOrNull()
                bookableShowId?.let {
                    val bookedSeats = ConfirmedSeats.slice(ConfirmedSeats.seatId)
                        .select { ConfirmedSeats.showId eq it }.map { it[ConfirmedSeats.seatId] }
                    val availableSeats = DefaultSeats.select {
                        (DefaultSeats.theatreId eq theatreId) and (DefaultSeats.screenId eq screenId) and
                                (DefaultSeats.id notInList bookedSeats)
                    }.map { it.toSeat() }
                    ServiceResult.Success(availableSeats)
                } ?: return@dbQuery ServiceResult.Failure(ErrorCodes.BOOKING_NOT_YET_OPEN_FOR_SHOW)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
        }
    }
}

private fun ResultRow.toSeat(): Seat {
    return Seat(
        seatRow = this[DefaultSeats.seatRow],
        seatColumn = this[DefaultSeats.seatColumn],
        seatCategory = this[DefaultSeats.seatCategory],
        seatPrice = this[DefaultSeats.seatPrice],
        seatQualifier = this[DefaultSeats.seatQualifier]
    )
}