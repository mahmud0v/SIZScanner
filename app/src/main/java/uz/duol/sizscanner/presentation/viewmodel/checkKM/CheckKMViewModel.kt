package uz.duol.sizscanner.presentation.viewmodel.checkKM

import androidx.lifecycle.LiveData
import uz.duol.sizscanner.data.database.entity.GtinEntity
import uz.duol.sizscanner.data.model.WaitingGtinInfo
import uz.duol.sizscanner.data.remote.response.CheckKMResponse

interface CheckKMViewModel {
    val errorMessageLiveData:LiveData<String>
    val successCheckKMLiveData:LiveData<CheckKMResponse?>
    val getWaitingKMCountLiveData:LiveData<WaitingGtinInfo>
    val inertGtinLiveData:LiveData<Unit>
    val allTaskGtinKMErrorLiveData:LiveData<String>
    val allTaskGtinKMLiveData2:LiveData<Int?>
    val allTaskGtinKMErrorLiveData2:LiveData<String>
    val kmChangeStatusScannedVerifiedLiveData:LiveData<Unit>
    val countNotVerifiedTaskGtinKMLiveData:LiveData<WaitingGtinInfo?>


    fun checkKMFromServer(kmList:List<String?>, transactionId:Int?)
    fun getWaitingKMCount(gtin:String?, taskId: Int?)
    fun insertGtin(gtinEntity: GtinEntity)
    fun allTaskGtinKM2(gtin: String?, taskId: Int?)
    fun kmChangeStatusScannedVerified(km:String?)
    fun countNotVerifiedTaskGtinKM(waitingGtinInfo:WaitingGtinInfo? )
}