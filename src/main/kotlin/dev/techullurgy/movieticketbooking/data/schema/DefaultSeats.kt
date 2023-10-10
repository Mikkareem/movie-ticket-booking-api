package dev.techullurgy.movieticketbooking.data.schema

import dev.techullurgy.movieticketbooking.data.models.SeatCategory
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object DefaultSeats: Table() {
    val id = long("id").autoIncrement()
    val theatreId = long("theatre_id").references(TheatresTable.id, ReferenceOption.CASCADE)
    val screenId = long("screen_id").references(ScreenTable.id, ReferenceOption.CASCADE)
    val seatRow = integer("seat_row")
    val seatColumn = integer("seat_column")
    val seatCategory = enumeration<SeatCategory>("seat_category")
    val seatQualifier = varchar("seat_qualifier", 10)
    val seatPrice = double("seat_price")

    init {
        uniqueIndex(theatreId, screenId, seatRow, seatColumn, seatCategory, seatPrice, seatQualifier)
    }

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}