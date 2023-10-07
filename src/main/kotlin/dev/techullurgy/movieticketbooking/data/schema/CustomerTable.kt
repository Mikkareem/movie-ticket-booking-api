package dev.techullurgy.movieticketbooking.data.schema

import org.jetbrains.exposed.sql.Table

object CustomerTable: Table(name = "customer") {
    val id = long("id").autoIncrement()
    val name = varchar("name", 100)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id, name = "PK_Customer")
}