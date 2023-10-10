package dev.techullurgy.movieticketbooking.data.schema

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ScreenTable: Table("screens") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 10)
    val rows = integer("total_rows")
    val cols = integer("total_cols")
    val theatreId = long("theatre_id").references(TheatresTable.id, onDelete = ReferenceOption.CASCADE)
    val movieId = long("movie_id").references(MovieTable.id, onDelete = ReferenceOption.CASCADE).nullable()

    init {
        uniqueIndex(theatreId, name)
    }

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id, name = "PK_Screen")
}