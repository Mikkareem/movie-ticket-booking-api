package dev.techullurgy.movieticketbooking.data.daos

import dev.techullurgy.movieticketbooking.data.schema.BookableShows
import dev.techullurgy.movieticketbooking.data.schema.ConfirmedSeats
import dev.techullurgy.movieticketbooking.data.schema.DefaultSeats
import dev.techullurgy.movieticketbooking.data.schema.Tickets
import dev.techullurgy.movieticketbooking.domain.models.Seat
import dev.techullurgy.movieticketbooking.domain.utils.ErrorCodes
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import dev.techullurgy.movieticketbooking.plugins.dbQuery
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.SQLIntegrityConstraintViolationException

interface SeatsDao {
    suspend fun addSeat(theatreId: Long, screenId: Long, seat: Seat): ServiceResult<Long>

    suspend fun bookSeats(
        bookableShowId: Long,
        customerId: Long,
        defaultSeatIds: Set<Long>
    ): ServiceResult<Long>

    suspend fun getAvailableSeatsForTheShow(
        theatreId: Long,
        screenId: Long,
        showId: Long,
        date: LocalDate
    ): ServiceResult<List<Seat>>

    suspend fun getAllSeatsForTheShow(
        theatreId: Long,
        screenId: Long,
        showId: Long,
        date: LocalDate
    ): ServiceResult<List<Seat>>
}

class SeatsDaoImpl : SeatsDao {
    override suspend fun addSeat(theatreId: Long, screenId: Long, seat: Seat): ServiceResult<Long> {
        val isTransactionPresent = TransactionManager.currentOrNull() != null

        val tryBlock = {
            DefaultSeats.insert {
                it[seatRow] = seat.seatRow
                it[seatColumn] = seat.seatColumn
                it[seatPrice] = seat.seatPrice
                it[seatCategory] = seat.seatCategory
                it[seatQualifier] = seat.seatQualifier
                it[this.screenId] = screenId
                it[this.theatreId] = theatreId
            }
            val seatId = DefaultSeats
                .select {
                    (DefaultSeats.seatRow eq seat.seatRow) and (DefaultSeats.seatColumn eq seat.seatColumn) and
                            (DefaultSeats.seatCategory eq seat.seatCategory) and (DefaultSeats.seatPrice eq seat.seatPrice) and
                            (DefaultSeats.seatQualifier eq seat.seatQualifier) and (DefaultSeats.screenId eq screenId) and
                            (DefaultSeats.theatreId eq theatreId)
                }.map { it[DefaultSeats.id] }.first()
            ServiceResult.Success(seatId)
        }

        val catchBlock: (Exception) -> ServiceResult.Failure<Long> = { e: Exception ->
            val original = (e as? ExposedSQLException)?.cause
            when(original) {
                is SQLIntegrityConstraintViolationException -> ServiceResult.Failure(ErrorCodes.SEAT_ALREADY_CREATED)
                else -> ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
            }
        }

        return if(isTransactionPresent) {
            try {
                tryBlock()
            } catch (e: Exception) {
                catchBlock(e)
            }
        } else {
            try {
                dbQuery { tryBlock() }
            } catch (e: Exception) {
                catchBlock(e)
            }
        }
    }

    override suspend fun bookSeats(
        bookableShowId: Long,
        customerId: Long,
        defaultSeatIds: Set<Long>
    ): ServiceResult<Long> {
        val isTransactionPresent = TransactionManager.currentOrNull() != null

        val tryBlock = {
            val screenId = BookableShows.select { BookableShows.id eq bookableShowId }.map { it[BookableShows.screenId] }.first()
            val totalAmount = DefaultSeats
                .slice(DefaultSeats.seatPrice.sum())
                .select { DefaultSeats.id inList defaultSeatIds }.map { it[DefaultSeats.seatPrice.sum()]!! }.first()
            val ticketHash = generateTicketHash()
            Tickets.insert {
                it[showId] = bookableShowId
                it[totalSeats] = defaultSeatIds.size
                it[customer] = customerId
                it[isActive] = true
                it[hash] = ticketHash
                it[paidAmount] = totalAmount
            }
            val ticketId = Tickets.select { Tickets.hash eq ticketHash }.map { it[Tickets.ticketId] }.first()
            defaultSeatIds.forEach { seatId ->
                ConfirmedSeats.insert {
                    it[this.ticketId] = ticketId
                    it[this.screenId] = screenId
                    it[this.seatId] = seatId
                    it[showId] = bookableShowId
                }
            }
            ServiceResult.Success(ticketId)
        }

        val catchBlock: (Exception) -> ServiceResult.Failure<Long> = { e ->
            val original = (e as? ExposedSQLException)?.cause
            when(original) {
                is SQLIntegrityConstraintViolationException -> ServiceResult.Failure(ErrorCodes.SEAT_ALREADY_BOOKED)
                else -> ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
            }
        }

        return if(isTransactionPresent) {
            try {
                tryBlock()
            } catch (e: Exception) {
                catchBlock(e)
            }
        } else {
            try {
                dbQuery { tryBlock() }
            } catch (e: Exception) {
                catchBlock(e)
            }
        }
    }

    override suspend fun getAvailableSeatsForTheShow(
        theatreId: Long,
        screenId: Long,
        showId: Long,
        date: LocalDate
    ): ServiceResult<List<Seat>> {

        val tryBlock = {
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
            } ?: ServiceResult.Failure(ErrorCodes.BOOKING_NOT_YET_OPEN_FOR_SHOW)
        }

        val catchBlock: (Exception) -> ServiceResult.Failure<List<Seat>> = {
            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
        }

        val isTransactionPresent = TransactionManager.currentOrNull() != null

        return if(isTransactionPresent) {
            try {
                tryBlock()
            } catch (e: Exception) {
                catchBlock(e)
            }
        } else {
            try {
                dbQuery { tryBlock() }
            } catch (e: Exception) {
                catchBlock(e)
            }
        }
    }

    override suspend fun getAllSeatsForTheShow(
        theatreId: Long,
        screenId: Long,
        showId: Long,
        date: LocalDate
    ): ServiceResult<List<Seat>> {

        val tryBlock = {
            val bookableShowId = BookableShows
                .slice(BookableShows.id).select {
                    (BookableShows.showId eq showId) and (BookableShows.screenId eq screenId) and
                            (BookableShows.theatreId eq theatreId) and (BookableShows.showDate eq date)
                }.map { it[BookableShows.id] }.firstOrNull()
            bookableShowId?.let {
                val seats = DefaultSeats.select {
                    (DefaultSeats.theatreId eq theatreId) and (DefaultSeats.screenId eq screenId)
                }.map { it.toSeat() }
                ServiceResult.Success(seats)
            } ?: ServiceResult.Failure(ErrorCodes.BOOKING_NOT_YET_OPEN_FOR_SHOW)
        }

        val catchBlock: (Exception) -> ServiceResult.Failure<List<Seat>> = {
            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
        }

        val isTransactionPresent = TransactionManager.currentOrNull() != null

        return if(isTransactionPresent) {
            try {
                tryBlock()
            } catch (e: Exception) {
                catchBlock(e)
            }
        } else {
            try {
                dbQuery { tryBlock() }
            } catch (e: Exception) {
                catchBlock(e)
            }
        }
    }
}

private fun ResultRow.toSeat(): Seat {
    return Seat(
        id = this[DefaultSeats.id],
        seatRow = this[DefaultSeats.seatRow],
        seatColumn = this[DefaultSeats.seatColumn],
        seatCategory = this[DefaultSeats.seatCategory],
        seatPrice = this[DefaultSeats.seatPrice],
        seatQualifier = this[DefaultSeats.seatQualifier]
    )
}

private fun generateTicketHash(): String {
    val alphaNumerics = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    val hashBuilder = StringBuilder()
    for(i in 1..95) {
        hashBuilder.append(alphaNumerics.random())
    }
    return hashBuilder.toString()
}