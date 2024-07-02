package uz.duol.sizscanner.domain.usecase

import kotlinx.coroutines.flow.Flow

interface TaskStatusUseCase {

    fun taskStatus(transactionId:Int?): Flow<Result<Boolean?>>

}