package dev.techullurgy.movieticketbooking.plugins

import dev.techullurgy.movieticketbooking.data.models.Censor
import dev.techullurgy.movieticketbooking.data.models.Language
import dev.techullurgy.movieticketbooking.data.schema.*
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLIntegrityConstraintViolationException

fun Application.configureDatabase() {
    val jdbcUrl = environment.config.property("storage.jdbcUrl").getString()
    val driverClassName = environment.config.property("storage.driverClassName").getString()

    val username = environment.config.propertyOrNull("storage.username")?.getString()
    val password = environment.config.propertyOrNull("storage.password")?.getString()

    val dbMode = environment.config.propertyOrNull("storage.dbMode")?.getString()

    Database.connect(
        url = jdbcUrl,
        driver = driverClassName,
        user = username ?: "",
        password = password ?: ""
    )
    transaction {
        SchemaUtils.create(MovieTable, CustomerTable, TheatresTable, ScreenTable, ShowTimingsTable, DefaultSeats, BookableShows, Tickets, ConfirmedSeats, Bookings)
        if(dbMode == "IN-MEMORY") initialData()
    }
}

suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }

private fun initialData() {
    tryInsert {
        MovieTable.insert {
            it[name] = "Leo"
            it[director] = "Lokesh Kanagaraj"
            it[actors] = "Vijay, Trisha, Arjun, Sanjay Dutt"
            it[releaseYear] = 2023
            it[censor] = Censor.UA
            it[originalLanguage] = Language.TAMIL
            it[dubbedLanguage] = Language.TAMIL
            it[releaseDate] = LocalDate(2023, Month.OCTOBER, 19)
            it[ticketsOpenDate] = LocalDateTime(2023, Month.OCTOBER, 5, 1, 0, 0)
        }
    }

    tryInsert {
        MovieTable.insert {
            it[name] = "Leo"
            it[director] = "Lokesh Kanagaraj"
            it[actors] = "Vijay, Trisha, Arjun, Sanjay Dutt"
            it[releaseYear] = 2023
            it[censor] = Censor.UA
            it[originalLanguage] = Language.TAMIL
            it[dubbedLanguage] = Language.HINDI
            it[releaseDate] = LocalDate(2023, Month.OCTOBER, 19)
            it[ticketsOpenDate] = LocalDateTime(2023, Month.OCTOBER, 5, 1, 0, 0)
        }
    }

    tryInsert {
        MovieTable.insert {
            it[name] = "Leo"
            it[director] = "Lokesh Kanagaraj"
            it[actors] = "Vijay, Trisha, Arjun, Sanjay Dutt"
            it[releaseYear] = 2023
            it[censor] = Censor.UA
            it[originalLanguage] = Language.TAMIL
            it[dubbedLanguage] = Language.MALAYALAM
            it[releaseDate] = LocalDate(2023, Month.OCTOBER, 19)
            it[ticketsOpenDate] = LocalDateTime(2023, Month.OCTOBER, 5, 1, 0, 0)
        }
    }

    tryInsert {
        MovieTable.insert {
            it[name] = "Leo"
            it[director] = "Lokesh Kanagaraj"
            it[actors] = "Vijay, Trisha, Arjun, Sanjay Dutt"
            it[releaseYear] = 2023
            it[censor] = Censor.UA
            it[originalLanguage] = Language.TAMIL
            it[dubbedLanguage] = Language.KANNADA
            it[releaseDate] = LocalDate(2023, Month.OCTOBER, 19)
            it[ticketsOpenDate] = LocalDateTime(2023, Month.OCTOBER, 5, 1, 0, 0)
        }
    }

    tryInsert {
        MovieTable.insert {
            it[name] = "Vikram"
            it[director] = "Lokesh Kanagaraj"
            it[actors] = "Kamal Hassan, Suriya, Fahad Fassil, Vijay Sethupathi"
            it[releaseYear] = 2022
            it[censor] = Censor.UA
            it[originalLanguage] = Language.TAMIL
            it[dubbedLanguage] = Language.TAMIL
            it[releaseDate] = LocalDate(2022, Month.OCTOBER, 19)
            it[ticketsOpenDate] = LocalDateTime(2022, Month.OCTOBER, 5, 1, 0, 0)
        }
    }

    tryInsert {
        MovieTable.insert {
            it[name] = "Vikram"
            it[director] = "Lokesh Kanagaraj"
            it[actors] = "Kamal Hassan, Suriya, Fahad Fassil, Vijay Sethupathi"
            it[releaseYear] = 2022
            it[censor] = Censor.UA
            it[originalLanguage] = Language.TAMIL
            it[dubbedLanguage] = Language.HINDI
            it[releaseDate] = LocalDate(2022, Month.OCTOBER, 19)
            it[ticketsOpenDate] = LocalDateTime(2022, Month.OCTOBER, 5, 1, 0, 0)
        }
    }

    tryInsert {
        MovieTable.insert {
            it[name] = "Vikram"
            it[director] = "Lokesh Kanagaraj"
            it[actors] = "Kamal Hassan, Suriya, Fahad Fassil, Vijay Sethupathi"
            it[releaseYear] = 2022
            it[censor] = Censor.UA
            it[originalLanguage] = Language.TAMIL
            it[dubbedLanguage] = Language.MALAYALAM
            it[releaseDate] = LocalDate(2022, Month.OCTOBER, 19)
            it[ticketsOpenDate] = LocalDateTime(2022, Month.OCTOBER, 5, 1, 0, 0)
        }
    }

    tryInsert {
        MovieTable.insert {
            it[name] = "Vikram"
            it[director] = "Lokesh Kanagaraj"
            it[actors] = "Kamal Hassan, Suriya, Fahad Fassil, Vijay Sethupathi"
            it[releaseYear] = 2022
            it[censor] = Censor.UA
            it[originalLanguage] = Language.TAMIL
            it[dubbedLanguage] = Language.KANNADA
            it[releaseDate] = LocalDate(2022, Month.OCTOBER, 19)
            it[ticketsOpenDate] = LocalDateTime(2022, Month.OCTOBER, 5, 1, 0, 0)
        }
    }
}

private fun tryInsert(
    catchBlock: ((Exception) -> Unit)? = null,
    block: () -> Unit
) {
    try {
        block()
    } catch (e: Exception) {
        catchBlock?.let { it(e) } ?: run {
            val original = (e as ExposedSQLException).cause
            when(original) {
                is SQLIntegrityConstraintViolationException -> {
                    println("Record already available")
                }
                else -> {
                    original?.printStackTrace()
                }
            }
        }
    }
}