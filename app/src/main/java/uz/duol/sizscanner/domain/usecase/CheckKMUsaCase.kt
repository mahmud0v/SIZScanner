package uz.duol.sizscanner.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.duol.sizscanner.data.remote.response.CheckKMResponse

interface CheckKMUsaCase {

    fun checkKMFromServer(kmList:List<String?>, transactionId:Int?): Flow<Result<CheckKMResponse?>>

}