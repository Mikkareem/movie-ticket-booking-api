package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.MoviesDao
import dev.techullurgy.movieticketbooking.domain.models.Movie
import dev.techullurgy.movieticketbooking.domain.utils.ErrorCodes
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult

class GetMovieByName(
    private val moviesDao: MoviesDao
) {
    suspend operator fun invoke(name: String): ServiceResult<List<Movie>> {
        val movies = moviesDao.getMoviesByName(name)
        return if(movies.isNotEmpty()) {
            ServiceResult.Success(movies)
        } else {
            ServiceResult.Failure(ErrorCodes.MOVIE_NOT_EXISTS)
        }
    }
}