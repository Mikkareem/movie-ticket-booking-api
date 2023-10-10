package dev.techullurgy.movieticketbooking.data.schema

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.time

object ShowTimingsTable: Table("show_timings") {
    val id = long("id").autoIncrement()
    val theatreId = long("theatre_id").references(TheatresTable.id, onDelete = ReferenceOption.CASCADE)
    val screenId = long("screen_id").references(ScreenTable.id, onDelete = ReferenceOption.CASCADE)
    val time = time("time")

    init {
        uniqueIndex(theatreId, screenId, time)
    }

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id, name = "PK_ShowTimings")
}