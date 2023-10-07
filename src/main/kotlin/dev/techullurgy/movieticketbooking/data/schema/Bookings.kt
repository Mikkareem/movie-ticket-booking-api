package dev.techullurgy.movieticketbooking.data.schema

import org.jetbrains.exposed.sql.Table

object Bookings: Table() {
    val userId = long("user_id").references(CustomerTable.id)
    val ticketId = long("ticket_id").references(Tickets.ticketId)

    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(userId, ticketId)
}