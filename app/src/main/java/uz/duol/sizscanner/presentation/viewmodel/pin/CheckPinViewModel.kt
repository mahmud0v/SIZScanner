package uz.duol.sizscanner.presentation.viewmodel.pin

import androidx.lifecycle.LiveData
import uz.duol.sizscanner.data.remote.response.CheckPinResponse

interface CheckPinViewModel {

    val checkPinLiveData:LiveData<CheckPinResponse?>
    val errorMessageLiveData:LiveData<String>
    val progressLoadingLiveData:LiveData<Boolean>

    fun checkPin(pin:String, deviceId:String?, fcmToken:String?)
}