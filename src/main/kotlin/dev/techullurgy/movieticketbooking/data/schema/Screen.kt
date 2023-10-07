package dev.techullurgy.movieticketbooking.data.schema

import org.jetbrains.exposed.sql.Table

object Screen: Table() {
    val id = long("id").autoIncrement()
    val name = varchar("name", 10)
    val theatreId = long("theatre_id").references(TheatresTable.id)
    val movieId = long("movie_id").references(MovieTable.id)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id, name = "PK_Screen")
}