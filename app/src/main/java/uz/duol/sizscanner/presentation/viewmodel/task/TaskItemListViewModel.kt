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
    val taskStatusLiveData: LiveData<String?>
    val taskMainStatusLiveData : LiveData<Boolean?>
    val errorTaskMainStatusLiveData:LiveData<String>
    val successCheckKMLiveData:LiveData<Unit>
    val errorCheckKMLiveData:LiveData<String>
    val progressLiveData:LiveData<Boolean>

    fun taskItemList(taskItemId:Int?, page:Int, size: Int)
    fun checkTaskStatus(transactionId:Int?)
    fun checkKMFromServer(km: String?, transactionId:Int?)



}