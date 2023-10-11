package dev.techullurgy.movieticketbooking.data.schema

import dev.techullurgy.movieticketbooking.data.models.BookingStatus
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Bookings: Table() {
    val id = long("id").autoIncrement()
    val customerId = long("customer_id").references(CustomerTable.id, onDelete = ReferenceOption.CASCADE)
    val bookableShowId = long("bookable_show_id").references(BookableShows.id, onDelete = ReferenceOption.CASCADE)
    val seats = varchar("seats", 255)
    val status = enumeration<BookingStatus>("status")

    init {
        uniqueIndex(customerId, bookableShowId, seats)
    }

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}