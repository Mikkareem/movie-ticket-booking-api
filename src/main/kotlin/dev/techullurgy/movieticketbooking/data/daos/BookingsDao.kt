package dev.techullurgy.movieticketbooking.data.daos

import dev.techullurgy.movieticketbooking.data.models.BookingStatus
import dev.techullurgy.movieticketbooking.data.schema.Bookings
import dev.techullurgy.movieticketbooking.domain.utils.ErrorCodes
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import dev.techullurgy.movieticketbooking.plugins.dbQuery
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

interface BookingsDao {
    suspend fun getBookableShowIdForBooking(id: Long): ServiceResult<Long>

    suspend fun getCustomerFromBooking(id: Long): ServiceResult<Long>

    suspend fun getSeatsFromBooking(id: Long): ServiceResult<Set<Long>>

    suspend fun updateStatus(id: Long, status: BookingStatus): Boolean
    suspend fun cancelBooking(id: Long): ServiceResult<Boolean>
}

class BookingsDaoImpl: BookingsDao {
    override suspend fun getBookableShowIdForBooking(id: Long): ServiceResult<Long> {
        return try {
            dbQuery {
                val bookingId = Bookings.select { Bookings.id eq id }
                    .map { it[Bookings.bookableShowId] }
                    .firstOrNull() ?: return@dbQuery ServiceResult.Failure(ErrorCodes.BOOKING_NOT_FOUND)
                ServiceResult.Success(bookingId)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
        }
    }

    override suspend fun getCustomerFromBooking(id: Long): ServiceResult<Long> {
        return try {
            dbQuery {
                val customerId = Bookings.select { Bookings.id eq id }
                    .map { it[Bookings.customerId] }
                    .firstOrNull() ?: return@dbQuery ServiceResult.Failure(ErrorCodes.BOOKING_NOT_FOUND)
                ServiceResult.Success(customerId)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
        }
    }

    override suspend fun getSeatsFromBooking(id: Long): ServiceResult<Set<Long>> {
        return try {
            dbQuery {
                val seats = Bookings.select { Bookings.id eq id }
                    .map { it[Bookings.seats] }
                    .firstOrNull() ?: return@dbQuery ServiceResult.Failure(ErrorCodes.BOOKING_NOT_FOUND)

                val seatIds = HashSet<Long>().apply {
                    seats.split(",")
                        .map { it.replace("-","").trim().toLong() }
                        .forEach { add(it) }
                }

                ServiceResult.Success(seatIds)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
        }
    }

    override suspend fun updateStatus(id: Long, status: BookingStatus): Boolean {
        return try {
            dbQuery {
                val count = Bookings.update(
                    where = { Bookings.id eq id }
                ) {
                    it[Bookings.status] = status
                }
                count == 1
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun cancelBooking(id: Long): ServiceResult<Boolean> {
        return try {
            dbQuery {
                val count = Bookings.update(
                    where = { Bookings.id eq id }
                ) {
                    it[status] = BookingStatus.FAILED
                }
                ServiceResult.Success(count == 1)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
        }
    }
}