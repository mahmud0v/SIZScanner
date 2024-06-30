package uz.duol.sizscanner.presentation.viewmodel.task

import androidx.lifecycle.LiveData
import uz.duol.sizscanner.data.remote.response.TaskItemResponse

interface TaskItemListViewModel {
    val taskItemListLiveData:LiveData<List<TaskItemResponse>?>
    val errorMessageLiveData: LiveData<String>
    val progressLoadingLiveData: LiveData<Boolean>
    val pageSizeLiveData: LiveData<Int>

    fun taskItemList(taskItemId:Int?, page:Int, size: Int)

}