package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.TheatresDao
import dev.techullurgy.movieticketbooking.domain.models.Screen
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult

class GetScreenByIdUseCase(
    private val theatresDao: TheatresDao
) {
    suspend operator fun invoke(theatreId: Long, screenId: Long): ServiceResult<Screen> {
        return theatresDao.getScreenByIdFromTheatre(theatreId, screenId)
    }
}