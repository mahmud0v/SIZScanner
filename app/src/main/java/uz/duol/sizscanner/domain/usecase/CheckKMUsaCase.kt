package uz.duol.sizscanner.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.duol.sizscanner.data.remote.response.CheckKMResponse

interface CheckKMUsaCase {

    fun checkKMFromServer(km: String?, transactionId:Int?): Flow<Result<CheckKMResponse?>>

    fun allTaskGtinKM(taskId: Int?, gtin: String?):Flow<Result<Int?>>

}