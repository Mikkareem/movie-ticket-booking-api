package dev.techullurgy.movieticketbooking.di

import dev.techullurgy.movieticketbooking.data.daos.*
import dev.techullurgy.movieticketbooking.domain.usecases.*
import org.koin.dsl.module

val appModule = module {
    single<CustomerDao> { CustomerDaoImpl() }
    single<MoviesDao> { MoviesDaoImpl() }
    single<SeatsDao> { SeatsDaoImpl() }
    single<TheatresDao> { TheatresDaoImpl(get()) }
    single<BookingsDao> { BookingsDaoImpl() }
    single { GetMovieByNameUseCase(get()) }
    single { GetRecommendedMoviesUseCase(get()) }
    single { GetSeatDetailsForShowUseCase(get()) }
    single { CreateTheatreUseCase(get()) }
    single { CreateScreenUseCase(get()) }
    single { CreateSeatsUseCase(get()) }
    single { CreateShowTimingUseCase(get()) }
    single { GetTheatresListForMovieUseCase(get()) }
    single { GetBookableShowListFromTheatreUseCase(get()) }
    single { OpenTicketsForTheShowUseCase(get()) }
    single { GetBookableMoviesUseCase(get()) }
    single { UpdateMovieInScreenUseCase(get()) }
    single { BookTicketUseCase(get(), get()) }
    single { CalculatePriceUseCase(get(), get()) }
    single { GenerateTicketUseCase(get(), get()) }
    single { CancelBookingUseCase(get()) }
    single { GetMovieByIdUseCase(get()) }
    single { GetBookableDatesForMovieFromTheatreUseCase(get()) }
    single { GetScreenByIdUseCase(get()) }
}