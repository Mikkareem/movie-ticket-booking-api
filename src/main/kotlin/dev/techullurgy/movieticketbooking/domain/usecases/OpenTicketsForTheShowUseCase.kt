package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.TheatresDao
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult
import kotlinx.datetime.LocalDate

class OpenTicketsForTheShowUseCase(
    private val theatresDao: TheatresDao
) {
    suspend operator fun invoke(theatreId: Long, screenId: Long, showTimingId: Long, date: LocalDate): ServiceResult<Boolean> {
        val screenResult = theatresDao.getScreenByIdFromTheatre(theatreId, screenId)
        if(screenResult is ServiceResult.Failure) {
            return ServiceResult.Failure(screenResult.errorCode)
        }
        return theatresDao.openTicketsForShow(screenId, showTimingId, date)
    }
}