package uz.duol.sizscanner.presentation.viewmodel.task

import androidx.lifecycle.LiveData
import uz.duol.sizscanner.data.remote.response.TaskResponse

interface NewTaskListViewModel {

    val newTaskListLiveData: LiveData<List<TaskResponse>?>
    val errorMessageLiveData: LiveData<String>
    val progressLoadingLiveData: LiveData<Boolean>
    val pageSizeLiveData: LiveData<Int>
    val totalSizeLiveData:LiveData<Int>


    fun newTaskList(page:Int, size:Int)

}