package dev.techullurgy.movieticketbooking

import dev.techullurgy.movieticketbooking.plugins.configureDatabase
import dev.techullurgy.movieticketbooking.plugins.configureKoin
import dev.techullurgy.movieticketbooking.plugins.configureRouting
import dev.techullurgy.movieticketbooking.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

@Suppress("UNUSED")
fun Application.module() {
    configureDatabase()
    configureKoin()
    configureSerialization()
    configureRouting()
}