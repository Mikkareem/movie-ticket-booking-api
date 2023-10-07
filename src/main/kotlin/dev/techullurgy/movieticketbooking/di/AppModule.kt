package dev.techullurgy.movieticketbooking.di

import dev.techullurgy.movieticketbooking.data.daos.*
import dev.techullurgy.movieticketbooking.domain.usecases.GetMovieByName
import dev.techullurgy.movieticketbooking.domain.usecases.GetRecommendedMovies
import org.koin.dsl.module

val appModule = module {
    single<CustomerDao> { CustomerDaoImpl() }
    single<MoviesDao> { MoviesDaoImpl() }
    single<TheatresDao> { TheatresDaoImpl() }
    single { GetMovieByName(get()) }
    single { GetRecommendedMovies(get()) }
}