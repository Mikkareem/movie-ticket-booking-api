package dev.techullurgy.movieticketbooking.data.schema

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date

object BookableShows: Table() {
    val id = long("id").autoIncrement()
    val theatreId = long("theatre_id").references(TheatresTable.id, onDelete = ReferenceOption.CASCADE)
    val movieId = long("movie_id").references(MovieTable.id, onDelete = ReferenceOption.CASCADE)
    val screenId = long("screen_id").references(ScreenTable.id, onDelete = ReferenceOption.CASCADE)
    val showId = long("show_id").references(ShowTimingsTable.id, onDelete = ReferenceOption.CASCADE)
    val showDate = date("show_date")

    init {
        uniqueIndex(theatreId, screenId, showId, showDate)
    }

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id, name = "PK_BookableShows")
}