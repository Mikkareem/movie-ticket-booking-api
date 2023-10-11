package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.MoviesDao
import dev.techullurgy.movieticketbooking.domain.models.Movie
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult

class GetBookableMoviesUseCase(
    private val moviesDao: MoviesDao
) {
    suspend operator fun invoke(): ServiceResult<List<Movie>> {
        return moviesDao.getBookableMovies()
    }
}