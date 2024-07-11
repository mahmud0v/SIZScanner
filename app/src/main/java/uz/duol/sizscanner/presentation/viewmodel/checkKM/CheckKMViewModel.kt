package uz.duol.sizscanner.presentation.viewmodel.checkKM

import androidx.lifecycle.LiveData
import uz.duol.sizscanner.data.database.entity.GtinEntity
import uz.duol.sizscanner.data.model.ChangeWaitingGtinCountInfo
import uz.duol.sizscanner.data.model.ExistsKMInfo
import uz.duol.sizscanner.data.model.WaitingGtinInfo
import uz.duol.sizscanner.data.remote.response.CheckKMResponse

interface CheckKMViewModel {
    val errorMessageLiveData:LiveData<String>
    val successCheckKMLiveData:LiveData<CheckKMResponse?>
    val getWaitingKMCountLiveData:LiveData<WaitingGtinInfo>
    val inertGtinLiveData:LiveData<Unit>
    val allTaskGtinKMErrorLiveData:LiveData<String>
    val allTaskGtinKMLiveData:LiveData<ExistsKMInfo?>
    val kmChangeStatusScannedVerifiedLiveData:LiveData<Unit>
    val countNotVerifiedTaskGtinKMLiveData:LiveData<WaitingGtinInfo?>
    val changeWaitingKMCountLiveData:LiveData<ChangeWaitingGtinCountInfo?>
    val waitingKMForInsertLiveData:LiveData<WaitingGtinInfo?>

    fun checkKMFromServer(kmList:List<String?>, transactionId:Int?)
    fun getWaitingKMCount(gtin:String?, taskId: Int?)
    fun changeWaitingKMCount(count:Int, gtin:String?, taskId: Int?)
    fun getWaitingKMForInsert(gtin: String?, taskId: Int?, km: String?, gtinKMCountNotVerified:Int?)
    fun insertGtin(gtinEntity: GtinEntity)
    fun allTaskGtinKM(gtin: String?, taskId: Int?, existsKMInfo: ExistsKMInfo?)
    fun kmChangeStatusScannedVerified(km:String?)
    fun countNotVerifiedTaskGtinKM(waitingGtinInfo:WaitingGtinInfo? )
    fun deleteKM(km:String?)
}