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
import uz.duol.sizscanner.data.remote.response.CheckPinResponse
import uz.duol.sizscanner.data.repository.app.AppRepository
import uz.duol.sizscanner.data.sharedpreference.AppSharedPreference
import uz.duol.sizscanner.domain.usecase.CheckPinUseCase
import uz.duol.sizscanner.utils.isConnected
import javax.inject.Inject

class CheckPinUseCaseImpl @Inject constructor(
    private val appRepository: AppRepository,
    private val sharedPreference: AppSharedPreference,
    @ApplicationContext val context:Context
): CheckPinUseCase {

    override fun checkPin(pin: String): Flow<Result<CheckPinResponse?>> {
        return flow {
            if (isConnected()){
                val response = appRepository.checkPin(pin)
                when (response.body()?.status) {
                    in 200..209 -> {
                        response.body()?.newToken?.let {
                            sharedPreference.token = it
                        }
                        emit(Result.success(response.body()!!.obj))
                    }
                    401 -> emit(Result.failure(Exception(context.getString(R.string.unauthorised))))
                    404 -> emit(Result.failure(Exception(context.getString(R.string.not_found))))
                    in 500..599 -> emit(Result.failure(Exception(context.getString(R.string.server_error))))
                    else -> emit(Result.failure(Exception(context.getString(R.string.unknown_error))))
                }
            }

        }.catch {
            emit(Result.failure(Exception(context.getString(R.string.unknown_error))))
        }.flowOn(Dispatchers.IO)
    }
}