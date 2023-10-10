package dev.techullurgy.movieticketbooking.data.schema

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Bookings: Table() {
    val userId = long("user_id").references(CustomerTable.id, onDelete = ReferenceOption.CASCADE)
    val ticketId = long("ticket_id").references(Tickets.ticketId, onDelete = ReferenceOption.CASCADE)

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(userId, ticketId)
}