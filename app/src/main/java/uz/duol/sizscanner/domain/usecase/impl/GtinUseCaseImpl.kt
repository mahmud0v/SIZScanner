package uz.duol.sizscanner.domain.usecase.impl

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import uz.duol.sizscanner.R
import uz.duol.sizscanner.data.database.entity.GtinEntity
import uz.duol.sizscanner.data.model.LimitTotalWaitingKM
import uz.duol.sizscanner.data.repository.db.AppDatabaseRepository
import uz.duol.sizscanner.domain.usecase.GtinUseCase
import javax.inject.Inject

class GtinUseCaseImpl @Inject constructor(
    private val appDatabaseRepository: AppDatabaseRepository,
    @ApplicationContext val context: Context
) : GtinUseCase {

    override fun getWaitingKMCount(gtin: String?, taskId:Int?): Flow<Result<LimitTotalWaitingKM?>> {
        return flow {
            val response = appDatabaseRepository.getWaitingKMCount(gtin, taskId)
            emit(Result.success(response))
        }.catch {
            emit(Result.failure(Exception(context.getString(R.string.unknown_error))))
        }.flowOn(Dispatchers.IO)
    }

    override fun insertGtin(gtinEntity: GtinEntity): Flow<Result<Unit>> {
        return flow {
            appDatabaseRepository.inertGtin(gtinEntity)
            emit(Result.success(Unit))
        }.catch {
            emit(Result.failure(Exception(context.getString(R.string.unknown_error))))
        }.flowOn(Dispatchers.IO)
    }

    override fun existsGtin(gtin: String?, taskId: Int?): Flow<Result<Int>> {
        return flow{
            val response = appDatabaseRepository.existsGtin(gtin, taskId)
            emit(Result.success(response))
        }.catch {
            emit(Result.failure(Exception(context.getString(R.string.unknown_error))))
        }.flowOn(Dispatchers.IO)
    }

    override fun editGtinTotalSoldKM(id: Int?, totalKM: Int?, sold: Int?): Flow<Result<Unit>> {
        return flow {
            val response = appDatabaseRepository.editGtinTotalSoldKM(id, totalKM, sold)
            emit(Result.success(response))
        }.catch {
            emit(Result.failure(Exception(it.message)))
        }.flowOn(Dispatchers.IO)
    }

    override fun getAllGtinDB(taskId: Int?): Flow<Result<List<GtinEntity>>>{
        return flow {
            val response = appDatabaseRepository.getAllGtinDB(taskId)
            emit(Result.success(response))
        }.catch {
            emit(Result.failure(Exception(context.getString(R.string.unknown_error))))
        }.flowOn(Dispatchers.IO)
    }

    override fun editWaitingKM(waitingKM: Int?, gtin: String?, taskId: Int?): Flow<Result<Int>> {
        return flow {
            val response = appDatabaseRepository.editWaitingKM(waitingKM, gtin, taskId)
            emit(Result.success(response))
        }.catch {
            emit(Result.failure(Exception(context.getString(R.string.unknown_error))))
        }.flowOn(Dispatchers.IO)
    }

    override fun existsKM(km: String?): Flow<Result<Int>> {
        return flow {
            val response = appDatabaseRepository.existsKM(km)
            emit(Result.success(response))
        }.catch {
            emit(Result.failure(Exception(context.getString(R.string.unknown_error))))
        }.flowOn(Dispatchers.IO)
    }


}