package dev.techullurgy.movieticketbooking.data.schema

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ConfirmedSeats : Table() {
    val id = long("id").autoIncrement()
    val ticketId = long("ticket_id").references(Tickets.ticketId, onDelete = ReferenceOption.CASCADE)
    val screenId = long("screen_id").references(ScreenTable.id, onDelete = ReferenceOption.CASCADE)
    val showId = long("show_id").references(BookableShows.id, onDelete = ReferenceOption.CASCADE)
    val seatId = long("seat_id").references(DefaultSeats.id, onDelete = ReferenceOption.CASCADE)

    init {
        uniqueIndex(ticketId, screenId, showId, seatId)
    }

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}