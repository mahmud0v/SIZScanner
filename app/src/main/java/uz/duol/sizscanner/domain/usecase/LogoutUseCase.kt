package uz.duol.sizscanner.domain.usecase

import kotlinx.coroutines.flow.Flow

interface LogoutUseCase {

    fun logout(): Flow<Result<Unit>>
}