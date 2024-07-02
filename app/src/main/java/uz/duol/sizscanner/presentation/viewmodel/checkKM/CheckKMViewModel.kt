package uz.duol.sizscanner.presentation.viewmodel.checkKM

import androidx.lifecycle.LiveData
import uz.duol.sizscanner.data.remote.response.CheckKMResponse

interface CheckKMViewModel {
    val errorMessageLiveData:LiveData<String>
    val successCheckKMLiveData:LiveData<CheckKMResponse?>


    fun checkKMFromServer(kmList:List<String?>, transactionId:Int?)
}