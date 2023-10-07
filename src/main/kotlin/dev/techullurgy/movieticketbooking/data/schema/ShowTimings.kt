package dev.techullurgy.movieticketbooking.data.schema

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.time

object ShowTimings: Table() {
    val id = long("id").autoIncrement()
    val screenId = long("screen_id").references(Screen.id)
    val time = time("time")

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id, name = "PK_ShowTimings")
}