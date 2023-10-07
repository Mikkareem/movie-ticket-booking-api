package dev.techullurgy.movieticketbooking.domain.models

import dev.techullurgy.movieticketbooking.data.models.Censor
import dev.techullurgy.movieticketbooking.data.models.Language
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    val id: Long = -1,
    val name: String,
    val director: String,
    val actors: String,
    val releaseYear: Int,
    val censor: Censor,
    val originalLanguage: Language,
    val dubbedLanguage: Language? = null,
    val releaseDate: LocalDate,
    val ticketsOpenDate: LocalDateTime
)