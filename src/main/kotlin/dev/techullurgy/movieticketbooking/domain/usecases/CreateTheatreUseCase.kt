package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.TheatresDao
import dev.techullurgy.movieticketbooking.domain.models.Theatre
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult

class CreateTheatreUseCase(
    private val theatresDao: TheatresDao
) {
    suspend operator fun invoke(name: String, address: String, city: String, state: String): ServiceResult<Long> {
        val theatre = Theatre(name = name, address = address, city = city, state = state)
        val result = theatresDao.addTheatre(theatre)
        if(result is ServiceResult.Failure) return ServiceResult.Failure(result.errorCode)

        return ServiceResult.Success((result as ServiceResult.Success).data)
    }
}