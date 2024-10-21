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
import uz.duol.sizscanner.data.sharedpreference.AppSharedPreference
import uz.duol.sizscanner.domain.usecase.LogoutUseCase
import uz.duol.sizscanner.utils.isConnected
import javax.inject.Inject

class LogoutUseCaseImpl @Inject constructor(
    private val appRepository: AppRepository,
    private val appSharedPreference: AppSharedPreference,
    @ApplicationContext val context: Context
) : LogoutUseCase {
    override fun logout(): Flow<Result<Unit>> {
        return flow {
            if (isConnected()) {
                val response = appRepository.logout()
                when (response.code()) {
                    in 200..209 -> {
                        appSharedPreference.token = ""
                        appSharedPreference.refreshToken = ""
                        emit(Result.success(Unit))
                    }
                    401 -> emit(Result.failure(Exception(context.getString(R.string.unauthorised))))
                    404 -> emit(Result.failure(Exception(context.getString(R.string.not_found))))
                    in 500..599 -> emit(Result.failure(Exception(context.getString(R.string.server_error))))
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