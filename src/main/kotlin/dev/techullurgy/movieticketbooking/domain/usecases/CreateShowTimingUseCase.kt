package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.TheatresDao
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import kotlinx.datetime.LocalTime

class CreateShowTimingUseCase(
    private val theatresDao: TheatresDao
) {
    suspend operator fun invoke(theatreId: Long, screenId: Long, time: LocalTime): ServiceResult<Long> {
        return theatresDao.addShow(theatreId, screenId, time)
    }
}