package uz.duol.sizscanner.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.duol.sizscanner.data.database.entity.KMModel

interface KMSaveDBUseCase {

    fun insertKM(kmModel: KMModel): Flow<Result<Long>>

    fun failedServerKMList(taskId:Int?): Flow<Result<List<String?>?>>

}