package uz.duol.sizscanner.domain.usecase.impl

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
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

    @OptIn(DelicateCoroutinesApi::class)
    override fun scannedNotVerifiedKMList(taskId:Int?): Flow<Result<List<String?>?>> {
        return channelFlow {
            while (!isClosedForSend){
                try {
                    kotlinx.coroutines.delay(10000L)
                    val response = appDatabaseRepository.scannedNotVerifiedKmList(taskId)
                    send(Result.success(response))
                }catch (e:Exception){
                    close()
                    return@channelFlow
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun kmChangeStatusScannedVerified(km: String?): Flow<Result<Unit>> {
        return flow {
            val response = appDatabaseRepository.kmChangeStatusScannedVerified(km)
            emit(Result.success(Unit))
        }.catch {
            emit(Result.failure(Exception(context.getString(R.string.unknown_error))))
        }.flowOn(Dispatchers.IO)
    }

    override fun countNotVerifiedTaskGtinKM(gtin: String?, taskId: Int?): Flow<Result<Int?>> {
        return flow {
            val response = appDatabaseRepository.countNotVerifiedTaskGtinKM(gtin, taskId)
            emit(Result.success(response))
        }.catch {
            emit(Result.failure(Exception(context.getString(R.string.unknown_error))))
        }.flowOn(Dispatchers.IO)
    }

    override fun deleteKM(km: String?) : Flow<Result<Unit>>{
        return flow {
            val response = appDatabaseRepository.deleteKM(km)
            emit(Result.success(Unit))
        }.catch {
            emit(Result.failure(Exception(context.getString(R.string.unknown_error))))
        }.flowOn(Dispatchers.IO)
    }

}