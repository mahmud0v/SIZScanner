package uz.duol.sizscanner.domain.usecase.impl

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import uz.duol.sizscanner.R
import uz.duol.sizscanner.data.database.entity.KMModel
import uz.duol.sizscanner.data.repository.db.AppDatabaseRepository
import uz.duol.sizscanner.domain.usecase.KMSaveDBUseCase
import javax.inject.Inject

class KMSaveDBUseCaseImpl @Inject constructor(
    private val appDatabaseRepository: AppDatabaseRepository,
    @ApplicationContext val context: Context
) : KMSaveDBUseCase {

    override fun insertKM(kmModel: KMModel): Flow<Result<Long>> {
       return flow {
           val response = appDatabaseRepository.insertKM(kmModel)
           emit(Result.success(response))
       }.catch {
           emit(Result.failure(Exception(context.getString(R.string.unknown_error))))
       }.flowOn(Dispatchers.IO)
    }

    override fun failedServerKMList(taskId:Int?): Flow<Result<List<String?>?>> {
        return flow {
            val response = appDatabaseRepository.failedServerKmList(taskId)
            emit(Result.success(response))
        }.catch {
            emit(Result.failure(Exception(context.getString(R.string.unknown_error))))
        }.flowOn(Dispatchers.IO)
    }

}