package dev.techullurgy.movieticketbooking.data.daos

import dev.techullurgy.movieticketbooking.data.schema.BookableShows
import dev.techullurgy.movieticketbooking.data.schema.Screen
import dev.techullurgy.movieticketbooking.data.schema.ShowTimings
import dev.techullurgy.movieticketbooking.domain.models.Theatre
import dev.techullurgy.movieticketbooking.domain.utils.ErrorCodes
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import dev.techullurgy.movieticketbooking.plugins.dbQuery
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
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
}

internal class TheatresDaoImpl: TheatresDao {
    override suspend fun getTheatresByName(name: String): ServiceResult<List<Theatre>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTheatreById(id: Long): ServiceResult<Theatre>{
        TODO("Not yet implemented")
    }

    override suspend fun openTicketsForShow(screenId: Long, showTimingId: Long, date: LocalDate): ServiceResult<Boolean> {
        data class BookableShowTemp(val theatreId: Long, val movieId: Long)
        return try {
            dbQuery {
                val bookableShow = (ShowTimings innerJoin Screen)
                    .slice(ShowTimings.id, Screen.id, Screen.theatreId, Screen.movieId)
                    .select { (ShowTimings.screenId eq screenId) and (ShowTimings.id eq showTimingId) }
                    .map {
                        BookableShowTemp(
                            movieId = it[Screen.movieId],
                            theatreId = it[Screen.theatreId]
                        )
                    }
                    .first()

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
            when(original) {
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
                val bookableShows = (ShowTimings innerJoin Screen)
                    .slice(ShowTimings.id, Screen.id, Screen.theatreId, Screen.movieId)
                    .select { (ShowTimings.screenId eq screenId) }
                    .map {
                        BookableShowTemp(
                            movieId = it[Screen.movieId],
                            theatreId = it[Screen.theatreId],
                            showTimingId = it[ShowTimings.id]
                        )
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
            val original = (e as? ExposedSQLException)?.cause
            when(original) {
                is SQLIntegrityConstraintViolationException -> {
                    ServiceResult.Failure(ErrorCodes.SHOW_BOOKING_ALREADY_OPEN)
                }
                else -> {
                    ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
                }
            }
        }
    }
}