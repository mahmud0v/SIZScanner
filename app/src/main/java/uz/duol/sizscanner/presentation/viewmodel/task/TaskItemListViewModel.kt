package uz.duol.sizscanner.presentation.viewmodel.task

import androidx.lifecycle.LiveData
import uz.duol.sizscanner.data.database.entity.KMModel
import uz.duol.sizscanner.data.remote.response.TaskItemResponse

interface TaskItemListViewModel {
    val taskItemListLiveData:LiveData<List<TaskItemResponse>?>
    val errorMessageLiveData: LiveData<String>
    val progressLoadingLiveData: LiveData<Boolean>
    val pageSizeLiveData: LiveData<Int>
    val addFailedKMSaveDB: LiveData<Unit>
    val taskStatusLiveData: LiveData<String?>
    val failedServerKMListLiveData:LiveData<List<String?>?>
    val errorMessageFailedServerKMListLiveData:LiveData<String>
    val taskMainStatusLiveData : LiveData<Boolean?>

    fun taskItemList(taskItemId:Int?, page:Int, size: Int)
    fun insertKMDB(kmModel: KMModel)
    fun failedServerKMList(taskId:Int?)
    fun taskStatus(transactionId:Int?)

}