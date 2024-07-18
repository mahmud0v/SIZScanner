package uz.duol.sizscanner.domain.usecase

import kotlinx.coroutines.flow.Flow

interface TaskStatusUseCase {

    fun checkTaskStatus(transactionId:Int?): Flow<Result<Boolean?>>

}