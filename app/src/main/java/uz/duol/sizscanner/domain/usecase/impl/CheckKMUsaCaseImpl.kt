package uz.duol.sizscanner.domain.usecase.impl

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import uz.duol.sizscanner.R
import uz.duol.sizscanner.data.repository.app.AppRepository
import uz.duol.sizscanner.domain.usecase.CheckKMUsaCase
import uz.duol.sizscanner.utils.isConnected
import javax.inject.Inject

class CheckKMUsaCaseImpl @Inject constructor(
    private val appRepository: AppRepository,
    @ApplicationContext val context: Context
) : CheckKMUsaCase {

    override fun checkKMFromServer(km: String?, transactionId:Int?): Flow<Result<Unit>> {
        return flow {
                if (isConnected()) {
                    val response = appRepository.checkKMFromServer(km, transactionId)
                    when (response.code()) {
                        in 200..209 -> {
                            emit(Result.success(Unit))
                        }
                        400 -> emit(Result.failure(Exception(context.getString(R.string.not_found))))
                        401 -> emit(Result.failure(Exception(context.getString(R.string.unauthorised))))
                        404 -> emit(Result.failure(Exception(context.getString(R.string.not_found))))
                        500-> emit(Result.failure(Exception(context.getString(R.string.server_error))))
                        505 -> emit(Result.failure(Exception(context.getString(R.string.duplicate_product))))
                        506 -> emit(Result.failure(Exception(context.getString(R.string.excess_km))))
                        else -> emit(Result.failure(Exception(context.getString(R.string.unknown_error))))
                    }
                } else {
                    emit(Result.failure(Exception(context.getString(R.string.error_internet))))
                }

        }.catch {
            emit(Result.failure(Exception(context.getString(R.string.unknown_error))))
        }.flowOn(Dispatchers.IO)

    }


}