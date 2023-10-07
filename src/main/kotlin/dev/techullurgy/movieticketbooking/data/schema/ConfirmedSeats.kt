package dev.techullurgy.movieticketbooking.data.schema

import org.jetbrains.exposed.sql.Table

object ConfirmedSeats : Table() {
    val id = long("id").autoIncrement()
    val ticketId = long("ticket_id").references(Tickets.ticketId)
    val screenId = long("screen_id").references(Screen.id)
    val showId = long("show_id").references(BookableShows.id)
    val seatId = long("seat_id").references(DefaultSeats.id)

    init {
        uniqueIndex(ticketId, screenId, showId, seatId)
    }

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}