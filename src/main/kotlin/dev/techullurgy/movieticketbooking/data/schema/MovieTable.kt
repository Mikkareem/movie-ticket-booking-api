package dev.techullurgy.movieticketbooking.data.schema


import dev.techullurgy.movieticketbooking.data.models.Censor
import dev.techullurgy.movieticketbooking.data.models.Language
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object MovieTable: Table(name = "movies") {
    val id: Column<Long> = long("id").autoIncrement()
    val name: Column<String> = varchar("name", 255)
    val director: Column<String> = varchar("director", 50)
    val actors: Column<String> = varchar("actors", 500)
    val releaseYear: Column<Int> = integer("release_year")
    val censor: Column<Censor> = enumeration("censor_category")
    val originalLanguage: Column<Language> = enumeration("original_language")
    val dubbedLanguage: Column<Language?> = enumeration<Language>("dubbed_language").nullable()
    val releaseDate: Column<LocalDate> = date("release_date")
    val ticketsOpenDate: Column<LocalDateTime> = datetime("tickets_open_date")

    init {
        uniqueIndex(name, originalLanguage, dubbedLanguage)
    }

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id, name = "PK_Movie")
}
