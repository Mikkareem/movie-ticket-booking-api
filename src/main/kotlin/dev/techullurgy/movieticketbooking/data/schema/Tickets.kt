package dev.techullurgy.movieticketbooking.data.schema

import org.jetbrains.exposed.sql.Table

object Tickets: Table() {
    val ticketId = long("ticket_id").autoIncrement()
    val showId = long("show_id").references(BookableShows.id)
    val customer = long("customer_id").references(CustomerTable.id)
    val hash = varchar("hash_value", 100).uniqueIndex()
    val paidAmount = double("paid_amount")
    val isActive = bool("is_active")
    val totalSeats = integer("total_seats")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(ticketId)
}