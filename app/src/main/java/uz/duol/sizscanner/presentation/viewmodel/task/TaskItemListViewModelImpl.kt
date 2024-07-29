package uz.duol.sizscanner.presentation.viewmodel.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.duol.sizscanner.data.remote.response.TaskItemResponse
import uz.duol.sizscanner.domain.usecase.CheckKMUsaCase
import uz.duol.sizscanner.domain.usecase.TaskItemListUseCase
import uz.duol.sizscanner.domain.usecase.TaskStatusUseCase
import javax.inject.Inject

@HiltViewModel
class TaskItemListViewModelImpl @Inject constructor(
    private val taskItemListUseCase: TaskItemListUseCase,
    private val taskStatusUseCase: TaskStatusUseCase,
    private val checkKMUsaCase: CheckKMUsaCase
) : TaskItemListViewModel, ViewModel() {
    override val taskItemListLiveData = MutableLiveData<List<TaskItemResponse>?>()
    override val errorMessageLiveData = MutableLiveData<String>()
    override val progressLoadingLiveData = MutableLiveData<Boolean>()
    override val pageSizeLiveData = MutableLiveData<Int>()
    override val taskStatusLiveData = MutableLiveData<String?>()
    override val taskMainStatusLiveData = MutableLiveData<Boolean?>()
    override val errorTaskMainStatusLiveData = MutableLiveData<String>()
    override val successCheckKMLiveData = MutableLiveData<Unit>()
    override val errorCheckKMLiveData = MutableLiveData<String>()
    override val progressLiveData = MutableLiveData<Boolean>()



    private var maxPage: Int = 0


    override fun taskItemList(taskItemId: Int?, page: Int, size: Int) {
        progressLoadingLiveData.value = true
        taskItemListUseCase.taskItemList(taskItemId, page, size).onEach {
            it.onSuccess {
                taskItemListLiveData.value = it?.rows
                it?.total?.let { recordsTotal ->
                    maxPage = recordsTotal / 10
                    if (recordsTotal % 10 != 0) maxPage++
                    pageSizeLiveData.value = maxPage
                }
                taskStatusLiveData.value = it?.taskStatus

            }

            it.onFailure {
                errorMessageLiveData.value = it.message
            }
            progressLoadingLiveData.value = false
        }.launchIn(viewModelScope)

    }



    override fun checkTaskStatus(transactionId: Int?) {
        taskStatusUseCase.checkTaskStatus(transactionId).onEach {
            it.onSuccess {
                taskMainStatusLiveData.value = it
            }

            it.onFailure {
                errorTaskMainStatusLiveData.value = it.message
            }

        }.launchIn(viewModelScope)
    }

    override fun checkKMFromServer(km: String?, transactionId: Int?) {
        progressLiveData.value = true
        checkKMUsaCase.checkKMFromServer(km, transactionId).onEach {

            it.onSuccess {
                successCheckKMLiveData.value = it
            }

            it.onFailure {
                errorCheckKMLiveData.value = it.message

            }
            progressLiveData.value = false
        }.launchIn(viewModelScope)
    }


}