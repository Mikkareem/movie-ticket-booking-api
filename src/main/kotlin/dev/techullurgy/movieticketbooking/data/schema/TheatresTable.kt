package dev.techullurgy.movieticketbooking.data.schema

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object TheatresTable: Table(name = "theatres") {
    val id: Column<Long> = long("id").autoIncrement()
    val name: Column<String> = varchar("name", 50).uniqueIndex()
    val address: Column<String> = varchar("address", 255)
    val city: Column<String> = varchar("city", 50)
    val state: Column<String> = varchar("state", 50)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id, name = "PK_Theatre")
}