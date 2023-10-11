package dev.techullurgy.movieticketbooking.data.daos

import dev.techullurgy.movieticketbooking.data.schema.BookableShows
import dev.techullurgy.movieticketbooking.data.schema.MovieTable
import dev.techullurgy.movieticketbooking.domain.models.Movie
import dev.techullurgy.movieticketbooking.domain.utils.ErrorCodes
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import dev.techullurgy.movieticketbooking.domain.utils.today
import dev.techullurgy.movieticketbooking.plugins.dbQuery
import org.jetbrains.exposed.sql.*

interface MoviesDao {
    suspend fun addMovie(movie: Movie): Boolean

    suspend fun getMoviesByName(prefix: String): List<Movie>

    suspend fun getMovieById(id: Long): Movie?

    suspend fun getAllMovies(): List<Movie>

    suspend fun getBookableMovies(): ServiceResult<List<Movie>>
}

class MoviesDaoImpl: MoviesDao {
    override suspend fun addMovie(movie: Movie): Boolean {
        dbQuery {
            MovieTable.insert {
                it[name] = movie.name
                it[director] = movie.director
                it[actors] = movie.actors
                it[releaseYear] = movie.releaseYear
                it[censor] = movie.censor
                it[originalLanguage] = movie.originalLanguage
                it[dubbedLanguage] = movie.dubbedLanguage
                it[releaseDate] = movie.releaseDate
                it[ticketsOpenDate] = movie.ticketsOpenDate
            }
        }
        return true
    }

    override suspend fun getMovieById(id: Long): Movie? {
        return dbQuery {
            MovieTable
                .select { MovieTable.id eq id }
                .map { it.toMovie() }
                .firstOrNull()
        }
    }

    override suspend fun getMoviesByName(prefix: String): List<Movie> {
        return dbQuery {
            MovieTable
                .select { MovieTable.name.lowerCase() like "${prefix}%" }
                .orderBy(MovieTable.releaseDate, order = SortOrder.DESC_NULLS_LAST)
                .map { it.toMovie() }
        }
    }

    override suspend fun getAllMovies(): List<Movie> {
        return dbQuery {
            MovieTable
                .selectAll()
                .orderBy(MovieTable.releaseDate, order = SortOrder.DESC_NULLS_LAST)
                .map { it.toMovie() }
        }
    }

    override suspend fun getBookableMovies(): ServiceResult<List<Movie>> {
        return try {
            dbQuery {
                val moviesList = (BookableShows innerJoin MovieTable)
                    .slice(MovieTable.columns)
                    .select {
                        BookableShows.showDate greaterEq today()
                    }
                    .distinct()
                    .map {
                        it.toMovie()
                    }.toSet().toList()
                ServiceResult.Success(moviesList)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(ErrorCodes.DATABASE_ERROR)
        }
    }
}

private fun ResultRow.toMovie(): Movie = Movie(
    id = this[MovieTable.id],
    name = this[MovieTable.name],
    director = this[MovieTable.director],
    actors = this[MovieTable.actors],
    releaseYear = this[MovieTable.releaseYear],
    censor = this[MovieTable.censor],
    originalLanguage = this[MovieTable.originalLanguage],
    dubbedLanguage = this[MovieTable.dubbedLanguage],
    releaseDate = this[MovieTable.releaseDate],
    ticketsOpenDate = this[MovieTable.ticketsOpenDate],
)