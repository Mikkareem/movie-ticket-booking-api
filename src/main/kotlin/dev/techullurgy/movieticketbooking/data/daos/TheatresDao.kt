package dev.techullurgy.movieticketbooking.data.daos

import dev.techullurgy.movieticketbooking.data.models.BookingStatus
import dev.techullurgy.movieticketbooking.data.schema.*
import dev.techullurgy.movieticketbooking.domain.models.*
import dev.techullurgy.movieticketbooking.domain.utils.ErrorCodes
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import dev.techullurgy.movieticketbooking.domain.utils.today
import dev.techullurgy.movieticketbooking.plugins.dbQuery
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.SQLIntegrityConstraintViolationException

interface TheatresDao {
    suspend fun getTheatresByName(name: String): ServiceResult<List<Theatre>>

    suspend fun getTheatreById(id: Long): ServiceResult<Theatre>

    /**
     * This function is used to insert data into the BookableShows,
     * So that the Customer can book the tickets.
     *
     * This is used to open the tickets for a particular show on the specified date.
     *
     * @param showTimingId The ShowTiming id which the bookings are open for.
     * @param date The date for open the tickets.
     *
     * @return Whether the opening tickets process is successful or not.
     */
    suspend fun openTicketsForShow(screenId: Long, showTimingId: Long, date: LocalDate): ServiceResult<Boolean>

    /**
     * This function is used to insert data into the BookableShows,
     * So that the Customer can book the tickets.
     *
     * This is used to open the tickets for all the available shows on the specified date.
     *
     * @param date The date for open the tickets.
     *
     * @return Whether the opening tickets process is successful or not.
     */
    suspend fun openTicketsForAllShows(screenId: Long, date: LocalDate): ServiceResult<Boolean>

    suspend fun addTheatre(theatre: Theatre): ServiceResult<Long>

    suspend fun addScreen(theatreId: Long, movieId: Long? = null, screen: Screen): ServiceResult<Long>

    suspend fun addShow(theatreId: Long, screenId: Long, time: LocalTime): ServiceResult<Long>

    suspend fun addSeats(theatreId: Long, screenId: Long, seats: List<Seat>): ServiceResult<Boolean>

    suspend fun getScreenByIdFromTheatre(theatreId: Long, screenId: Long): ServiceResult<Screen>

    suspend fun getTheatreListForMovieFromToday(movieId: Long): ServiceResult<List<Theatre>>

    suspend fun getBookableDatesFromTheatreForMovie(theatreId: Long, movieId: Long): ServiceResult<Set<LocalDate>>

    suspend fun getBookableShowsFromTheatreForMovie(
        theatreId: Long, movieId: Long, date: LocalDate
    ): ServiceResult<TheatreFullDetail>

    suspend fun updateMovieFromScreen(
        theatreId: Long, screenId: Long, movieId: Long
    ): ServiceResult<Boolean>

    suspend fun getBookableShowIdFor(
        theatreId: Long,
        screenId: Long,
        movieId: Long,
        showId: Long,
        date: LocalDate
    ): ServiceResult<Long>

    suspend fun registerBooking(
        bookableShowId: Long,
        customer: Long,
        seats: String
    ): ServiceResult<Long>
}

internal class TheatresDaoImpl(
    private val seatsDao: SeatsDao
) : TheatresDao {
    override suspend fun getTheatresByName(name: String): ServiceResult<List<Theatre>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTheatreById(id: Long): ServiceResult<Theatre> {
        TODO("Not yet implemented")
    }

    override suspend fun openTicketsForShow(
        screenId: Long, showTimingId: Long, date: LocalDate
    ): ServiceResult<Boolean> {
        data class BookableShowTemp(val theatreId: Long, val movieId: Long)
        return try {
            dbQuery {
                val bookableShow = (ShowTimingsTable innerJoin ScreenTable).slice(
                    ShowTimingsTable.id,
                    ScreenTable.id,
                    ScreenTable.theatreId,
                    ScreenTable.movieId
                ).select {
                    (ShowTimingsTable.screenId eq screenId) and (ShowTimingsTable.id eq showTimingId) and (ScreenTable.movieId neq null)
                }.map {
                    BookableShowTemp(
                        movieId = it[ScreenTable.movieId]!!, theatreId = it[ScreenTable.theatreId]
                    )
                }.firstOrNull() ?: return@dbQuery ServiceResult.Failure(ErrorCodes.UNABLE_TO_OPEN_TICKETS_FOR_SHOW)

                BookableShows.insert {
                    it[theatreId] = bookableShow.theatreId
                    it[showId] = showTimingId
                    it[showDate] = date
                    it[movieId] = bookableShow.movieId
                    it[this.screenId] = screenId
                }
                ServiceResult.Success(true)
            }
        } catch (e: Exception) {
            val original = (e as? ExposedSQLException)?.cause
            when (original) {
                is SQLIntegrityConstraintViolationException -> {
                    ServiceResult.Failure(ErrorCodes.SHOW_BOOKING_ALREADY_OPEN)
                }

                else -> {
                    ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
                }
            }
        }
    }

    override suspend fun openTicketsForAllShows(screenId: Long, date: LocalDate): ServiceResult<Boolean> {
        data class BookableShowTemp(val theatreId: Long, val movieId: Long, val showTimingId: Long)
        return try {
            dbQuery {
                val bookableShows = (ShowTimingsTable innerJoin ScreenTable).slice(
                    ShowTimingsTable.id,
                    ScreenTable.id,
                    ScreenTable.theatreId,
                    ScreenTable.movieId
                ).select { (ShowTimingsTable.screenId eq screenId) and (ScreenTable.movieId neq null) }.map {
                    BookableShowTemp(
                        movieId = it[ScreenTable.movieId]!!,
                        theatreId = it[ScreenTable.theatreId],
                        showTimingId = it[ShowTimingsTable.id]
                    )
                }

                if (bookableShows.isEmpty()) {
                    return@dbQuery ServiceResult.Failure(ErrorCodes.UNABLE_TO_OPEN_TICKETS_FOR_SHOW)
                }

                bookableShows.forEach { bookableShow ->
                    BookableShows.insert {
                        it[theatreId] = bookableShow.theatreId
                        it[showId] = bookableShow.showTimingId
                        it[showDate] = date
                        it[movieId] = bookableShow.movieId
                        it[this.screenId] = screenId
                    }
                }
                ServiceResult.Success(true)
            }
        } catch (e: Exception) {
            when (val original = (e as? ExposedSQLException)?.cause) {
                is SQLIntegrityConstraintViolationException -> {
                    val message = original.message!!.lowercase()
                    if (message.contains("referential integrity") || message.contains("foreign key")) {
                        if (message.contains("fk_${BookableShows.tableName.lowercase()}_${BookableShows.screenId.name.lowercase()}")) {
                            ServiceResult.Failure(ErrorCodes.SCREEN_NOT_EXISTS)
                        } else if (message.contains("fk_${BookableShows.tableName.lowercase()}_${BookableShows.theatreId.name.lowercase()}")) {
                            ServiceResult.Failure(ErrorCodes.THEATRE_NOT_EXISTS)
                        } else if (message.contains("fk_${BookableShows.tableName.lowercase()}_${BookableShows.showId.name.lowercase()}")) {
                            ServiceResult.Failure(ErrorCodes.SHOW_TIMING_NOT_EXISTS)
                        } else if (message.contains("fk_${BookableShows.tableName.lowercase()}_${BookableShows.movieId.name.lowercase()}")) {
                            ServiceResult.Failure(ErrorCodes.MOVIE_NOT_EXISTS)
                        } else {
                            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
                        }
                    } else {
                        ServiceResult.Failure(ErrorCodes.SHOW_BOOKING_ALREADY_OPEN)
                    }
                }

                else -> {
                    ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
                }
            }
        }
    }

    override suspend fun addTheatre(theatre: Theatre): ServiceResult<Long> {
        return try {
            dbQuery {
                TheatresTable.insert {
                    it[name] = theatre.name
                    it[city] = theatre.city
                    it[address] = theatre.address
                    it[state] = theatre.state
                }
                val theatreId =
                    TheatresTable.select { TheatresTable.name eq theatre.name }.map { it[TheatresTable.id] }.first()
                ServiceResult.Success(theatreId)
            }
        } catch (e: Exception) {
            val original = (e as? ExposedSQLException)?.cause
            when (original) {
                is SQLIntegrityConstraintViolationException -> {
                    ServiceResult.Failure(ErrorCodes.THEATRE_ALREADY_EXISTS)
                }

                else -> {
                    ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
                }
            }
        }
    }

    override suspend fun addScreen(theatreId: Long, movieId: Long?, screen: Screen): ServiceResult<Long> {
        return try {
            dbQuery {
                ScreenTable.insert {
                    it[name] = screen.name
                    it[rows] = screen.rows
                    it[cols] = screen.cols
                    it[this.theatreId] = theatreId
                    it[this.movieId] = movieId
                }
                val screenId =
                    ScreenTable.select { (ScreenTable.theatreId eq theatreId) and (ScreenTable.name eq screen.name) }
                        .map { it[ScreenTable.id] }.first()
                ServiceResult.Success(screenId)
            }
        } catch (e: Exception) {
            when (val original = (e as? ExposedSQLException)?.cause) {
                is SQLIntegrityConstraintViolationException -> {
                    val message = original.message!!.lowercase()
                    if (message.contains("referential integrity") || message.contains("foreign key")) {
                        if (message.contains("fk_${ScreenTable.tableName.lowercase()}_${ScreenTable.movieId.name.lowercase()}")) {
                            ServiceResult.Failure(ErrorCodes.MOVIE_NOT_EXISTS)
                        } else if (message.contains("fk_${ScreenTable.tableName.lowercase()}_${ScreenTable.theatreId.name.lowercase()}")) {
                            ServiceResult.Failure(ErrorCodes.THEATRE_NOT_EXISTS)
                        } else {
                            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
                        }
                    } else {
                        ServiceResult.Failure(ErrorCodes.SCREEN_ALREADY_AVAILABLE)
                    }
                }

                else -> {
                    ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
                }
            }
        }
    }

    override suspend fun addShow(theatreId: Long, screenId: Long, time: LocalTime): ServiceResult<Long> {
        return try {
            dbQuery {
                ShowTimingsTable.insert {
                    it[this.theatreId] = theatreId
                    it[this.screenId] = screenId
                    it[this.time] = time
                }

                val showTimingId = ShowTimingsTable.select {
                    (ShowTimingsTable.theatreId eq theatreId) and (ShowTimingsTable.screenId eq screenId) and (ShowTimingsTable.time eq time)
                }.map { it[ShowTimingsTable.id] }.first()

                ServiceResult.Success(showTimingId)
            }
        } catch (e: Exception) {
            when (val original = (e as? ExposedSQLException)?.cause) {
                is SQLIntegrityConstraintViolationException -> {
                    val message = original.message!!.lowercase()
                    if (message.contains("referential integrity") || message.contains("foreign key")) {
                        if (message.contains("fk_${ShowTimingsTable.tableName.lowercase()}_${ShowTimingsTable.screenId.name.lowercase()}")) {
                            ServiceResult.Failure(ErrorCodes.SCREEN_NOT_EXISTS)
                        } else if (message.contains("fk_${ShowTimingsTable.tableName.lowercase()}_${ShowTimingsTable.theatreId.name.lowercase()}")) {
                            ServiceResult.Failure(ErrorCodes.THEATRE_NOT_EXISTS)
                        } else {
                            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
                        }
                    } else {
                        ServiceResult.Failure(ErrorCodes.SCREEN_ALREADY_AVAILABLE)
                    }
                }

                else -> {
                    ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
                }
            }
        }
    }

    override suspend fun addSeats(
        theatreId: Long, screenId: Long, seats: List<Seat>
    ): ServiceResult<Boolean> {
        return dbQuery {
            for (seat in seats) {
                val result = seatsDao.addSeat(theatreId, screenId, seat)
                if (result is ServiceResult.Failure) {
                    TransactionManager.current().rollback()
                    return@dbQuery ServiceResult.Failure<Boolean>(result.errorCode)
                }
            }
            ServiceResult.Success(true)
        }
    }

    override suspend fun getScreenByIdFromTheatre(theatreId: Long, screenId: Long): ServiceResult<Screen> {
        return try {
            dbQuery {
                val screen =
                    ScreenTable.select { (ScreenTable.id eq screenId) and (ScreenTable.theatreId eq theatreId) }.map {
                        Screen(
                            it[ScreenTable.id], it[ScreenTable.name], it[ScreenTable.rows], it[ScreenTable.cols]
                        )
                    }.firstOrNull()
                screen?.let { ServiceResult.Success(it) } ?: ServiceResult.Failure(ErrorCodes.SCREEN_NOT_EXISTS)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
        }
    }

    override suspend fun getTheatreListForMovieFromToday(movieId: Long): ServiceResult<List<Theatre>> {
        return try {
            dbQuery {
                val theatreIds = BookableShows.select {
                    (BookableShows.movieId eq movieId) and (BookableShows.showDate greaterEq today())
                }.map { it[BookableShows.theatreId] }.toSet()

                val result = mutableListOf<Theatre>().apply {
                    theatreIds.forEach { id ->
                        val theatre = TheatresTable.select { (TheatresTable.id eq id) }.map {
                            Theatre(
                                id = id,
                                name = it[TheatresTable.name],
                                address = it[TheatresTable.address],
                                city = it[TheatresTable.city],
                                state = it[TheatresTable.state]
                            )
                        }.first()
                        add(theatre)
                    }
                }.toList()
                ServiceResult.Success(result)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
        }
    }

    override suspend fun getBookableDatesFromTheatreForMovie(
        theatreId: Long,
        movieId: Long
    ): ServiceResult<Set<LocalDate>> {
        return try {
            dbQuery {
                val dates = BookableShows.select {
                    (BookableShows.movieId eq movieId) and (BookableShows.showDate greaterEq today()) and (BookableShows.theatreId eq theatreId)
                }.orderBy(BookableShows.showDate).map { it[BookableShows.showDate] }
                ServiceResult.Success(dates.toSet())
            }
        } catch (e: Exception) {
            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
        }
    }

    override suspend fun getBookableShowsFromTheatreForMovie(
        theatreId: Long, movieId: Long, date: LocalDate
    ): ServiceResult<TheatreFullDetail> {
        return try {
            dbQuery {
                data class BookableShowTemp(
                    val id: Long, val screenId: Long, val showTimingId: Long, val showDate: LocalDate
                )

                val bookableShowTemps = BookableShows.select {
                    (BookableShows.movieId eq movieId) and (BookableShows.showDate eq date) and (BookableShows.theatreId eq theatreId)
                }.map {
                    BookableShowTemp(
                        id = it[BookableShows.id],
                        screenId = it[BookableShows.screenId],
                        showTimingId = it[BookableShows.showId],
                        showDate = it[BookableShows.showDate]
                    )
                }

                val theatre = TheatresTable.select { (TheatresTable.id eq theatreId) }.map {
                    Theatre(
                        id = it[TheatresTable.id],
                        name = it[TheatresTable.name],
                        address = it[TheatresTable.address],
                        city = it[TheatresTable.city],
                        state = it[TheatresTable.state]
                    )
                }.first()

                val screens = bookableShowTemps.map { it.screenId }.toSet()

                val screenFullDetails = mutableListOf<ScreenFullDetail>()

                screens.forEach {
                    val bookableShowFullDetails =
                        (BookableShows innerJoin ShowTimingsTable)
                            .select {
                                (ShowTimingsTable.theatreId eq theatreId) and (ShowTimingsTable.screenId eq it) and
                                        (BookableShows.movieId eq movieId) and (BookableShows.showDate eq date)
                            }
                            .map { BookableShowFullDetail(it[BookableShows.id], it[BookableShows.showDate], it[ShowTimingsTable.time]) }

                    val screen = ScreenTable.select { ScreenTable.id eq it }.map { row ->
                        Screen(
                            id = row[ScreenTable.id],
                            name = row[ScreenTable.name],
                            rows = row[ScreenTable.rows],
                            cols = row[ScreenTable.cols]
                        )
                    }.first()

                    val screenFullDetail = ScreenFullDetail(screen, movieId, bookableShowFullDetails)
                    screenFullDetails.add(screenFullDetail)
                }

                val theatreFullDetail = TheatreFullDetail(theatre, screenFullDetails)

                ServiceResult.Success(theatreFullDetail)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
        }
    }

    override suspend fun updateMovieFromScreen(theatreId: Long, screenId: Long, movieId: Long): ServiceResult<Boolean> {
        return try {
            dbQuery {
                val count = ScreenTable.update(
                    where = {
                        (ScreenTable.theatreId eq theatreId) and (ScreenTable.id eq screenId)
                    }
                ) {
                    it[ScreenTable.movieId] = movieId
                }

                if (count > 0) {
                    ServiceResult.Success(true)
                } else {
                    ServiceResult.Failure(ErrorCodes.SCREEN_NOT_EXISTS)
                }
            }
        } catch (e: Exception) {
            when (val original = (e as? ExposedSQLException)?.cause) {
                is SQLIntegrityConstraintViolationException -> {
                    val message = original.message!!.lowercase()
                    if (message.contains("referential integrity") || message.contains("foreign key")) {
                        if (message.contains("fk_${ScreenTable.tableName.lowercase()}_${ScreenTable.movieId.name.lowercase()}")) {
                            ServiceResult.Failure(ErrorCodes.MOVIE_NOT_EXISTS)
                        } else {
                            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
                        }
                    } else {
                        ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
                    }
                }

                else -> {
                    ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
                }
            }
        }
    }

    override suspend fun getBookableShowIdFor(
        theatreId: Long,
        screenId: Long,
        movieId: Long,
        showId: Long,
        date: LocalDate
    ): ServiceResult<Long> {
        return try {
            dbQuery {
                val bookableShowId = BookableShows.select {
                    (BookableShows.theatreId eq theatreId) and
                            (BookableShows.screenId eq screenId) and
                            (BookableShows.movieId eq movieId) and
                            (BookableShows.showId eq showId) and
                            (BookableShows.showDate eq date)
                }.map { it[BookableShows.id] }.firstOrNull() ?: return@dbQuery ServiceResult.Failure(ErrorCodes.BOOKING_NOT_YET_OPEN_FOR_SHOW)

                ServiceResult.Success(bookableShowId)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
        }
    }

    private suspend fun isBookingPendingForSeat(
        bookableShowId: Long,
        seat: String
    ): Boolean {
        return try {
            dbQuery {
                Bookings.select { (Bookings.bookableShowId eq bookableShowId) and
                        (Bookings.seats like "%$seat%") and
                        (Bookings.status eq BookingStatus.PENDING)
                }.firstOrNull() != null
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun registerBooking(
        bookableShowId: Long,
        customer: Long,
        seats: String
    ): ServiceResult<Long> {
        val seatIds = seats.split(",")
        for(seat in seatIds) {
            if(isBookingPendingForSeat(bookableShowId, seat)) {
                return ServiceResult.Failure(ErrorCodes.UNABLE_TO_BOOK_THE_TICKET)
            }
        }

        return try {
            dbQuery {
                val booking = Bookings.insert {
                    it[Bookings.bookableShowId] = bookableShowId
                    it[customerId] = customer
                    it[Bookings.seats] = seats
                }
                ServiceResult.Success(booking[Bookings.id])
            }
        } catch (e: Exception) {
            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
        }
    }
}