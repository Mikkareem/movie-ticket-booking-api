package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.MoviesDao
import dev.techullurgy.movieticketbooking.domain.models.Movie
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult

class GetMovieById(
    private val moviesDao: MoviesDao
) {
    suspend operator fun invoke(movieId: Long): ServiceResult<Movie> {
        return moviesDao.getMovieById(id = movieId)
    }
}