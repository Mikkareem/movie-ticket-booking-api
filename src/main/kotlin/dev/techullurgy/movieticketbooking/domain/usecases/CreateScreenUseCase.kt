package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.TheatresDao
import dev.techullurgy.movieticketbooking.domain.models.Screen
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult

class CreateScreenUseCase(
    private val theatresDao: TheatresDao
) {
    suspend operator fun invoke(theatreId: Long, name: String, rows: Int, cols: Int): ServiceResult<Long> {
        val screen = Screen(name = name, rows = rows, cols = cols)
        val result = theatresDao.addScreen(theatreId, screen = screen)
        if(result is ServiceResult.Failure) return ServiceResult.Failure(result.errorCode)
        return ServiceResult.Success((result as ServiceResult.Success).data)
    }
}