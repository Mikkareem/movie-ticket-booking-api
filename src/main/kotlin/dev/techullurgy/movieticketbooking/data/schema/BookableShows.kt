package dev.techullurgy.movieticketbooking.data.schema

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date

object BookableShows: Table() {
    val id = long("id").autoIncrement()
    val theatreId = long("theatre_id").references(TheatresTable.id)
    val movieId = long("movie_id").references(MovieTable.id)
    val screenId = long("screen_id").references(Screen.id)
    val showId = long("show_id").references(ShowTimings.id)
    val showDate = date("show_date")

    init {
        uniqueIndex(theatreId, screenId, showId, showDate)
    }

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id, name = "PK_BookableShows")
}