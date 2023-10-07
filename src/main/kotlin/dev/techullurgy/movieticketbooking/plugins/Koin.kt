package dev.techullurgy.movieticketbooking.plugins

import dev.techullurgy.movieticketbooking.di.appModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    install(Koin) {
        modules(appModule)
    }
}