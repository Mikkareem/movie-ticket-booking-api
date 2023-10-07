package dev.techullurgy.movieticketbooking.domain.utils

sealed class ServiceResult<T> {
    data class Success<T>(val data: T): ServiceResult<T>()
    data class Failure<T>(val errorCode: ErrorCodes): ServiceResult<T>()
}