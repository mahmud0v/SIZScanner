package uz.duol.sizscanner.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.duol.sizscanner.data.remote.response.CheckPinResponse

interface CheckPinUseCase {

    fun checkPin(pin:String, deviceId:String?): Flow<Result<CheckPinResponse?>>

}