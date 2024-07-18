package uz.duol.sizscanner.presentation.viewmodel.task

import androidx.lifecycle.LiveData
import uz.duol.sizscanner.data.database.entity.GtinEntity
import uz.duol.sizscanner.data.database.entity.KMModel
import uz.duol.sizscanner.data.model.ExistsKMInfo
import uz.duol.sizscanner.data.model.InsertKMInfo
import uz.duol.sizscanner.data.remote.response.TaskItemResponse

interface TaskItemListViewModel {
    val taskItemListLiveData:LiveData<List<TaskItemResponse>?>
    val errorMessageLiveData: LiveData<String>
    val progressLoadingLiveData: LiveData<Boolean>
    val pageSizeLiveData: LiveData<Int>
    val addWaitingKMSaveDB: LiveData<InsertKMInfo>
    val taskStatusLiveData: LiveData<String?>
    val scannedNotVerifiedKMListLiveData:LiveData<List<String?>?>
    val errorScannedNotVerifiedKMListLiveData:LiveData<String>
    val taskMainStatusLiveData : LiveData<Boolean?>
    val errorTaskMainStatusLiveData:LiveData<String>
    val existGtinLiveData:LiveData<TaskItemResponse?>
    val editGtinTotalSoldKMLiveData:LiveData<Unit>
    val getAllGtinDBLiveData:LiveData<List<GtinEntity>>
    val editWaitingKMLiveData:LiveData<Int>
    val existKMLiveData:LiveData<ExistsKMInfo>
    val horizontalProgressLiveData:LiveData<Boolean>

    fun taskItemList(taskItemId:Int?, page:Int, size: Int)
    fun insertKMDB(kmModel: KMModel)
    fun scannedNotVerifiedKMList(taskId:Int?)
    fun checkTaskStatus(transactionId:Int?)
    fun existGtin(gtin:String?, taskId: Int?, taskItem:TaskItemResponse?)
    fun editGtinTotalSoldKM(id:Int?, totalKM:Int?, soldKM:Int?)
    fun getAllGtinDB(taskId: Int?)
    fun editWaitingKM(waitingKM: Int?, gtin: String?, taskId: Int?)
    fun existKM(km:String?)

}