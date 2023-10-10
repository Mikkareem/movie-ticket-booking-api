package dev.techullurgy.movieticketbooking.domain.usecases

import dev.techullurgy.movieticketbooking.data.daos.TheatresDao
import dev.techullurgy.movieticketbooking.domain.models.TheatreFullDetail
import dev.techullurgy.movieticketbooking.domain.utils.ServiceResult

class GetBookableShowListFromTheatreUseCase(
    private val theatresDao: TheatresDao
) {
    suspend operator fun invoke(theatreId: Long, movieId: Long): ServiceResult<List<TheatreFullDetail>> {
        val datesResult = theatresDao.getBookableDatesFromTheatreForMovie(theatreId, movieId)
        if(datesResult is ServiceResult.Failure) {
            return ServiceResult.Failure(datesResult.errorCode)
        }

        val dates = (datesResult as ServiceResult.Success).data

        val result = mutableListOf<TheatreFullDetail>().apply {
            for (date in dates) {
                val theatreDetailsResult = theatresDao.getBookableShowsFromTheatreForMovie(theatreId, movieId, date)
                if(theatreDetailsResult is ServiceResult.Failure) {
                    return ServiceResult.Failure(theatreDetailsResult.errorCode)
                }
                val theatreDetails = (theatreDetailsResult as ServiceResult.Success).data
                add(theatreDetails)
            }
        }

        return ServiceResult.Success(result)
    }
}