package uz.duol.sizscanner.presentation.viewmodel.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.duol.sizscanner.data.remote.response.TaskItemResponse
import uz.duol.sizscanner.domain.usecase.TaskItemListUseCase
import javax.inject.Inject

@HiltViewModel
class TaskItemListViewModelImpl @Inject constructor(
    private val taskItemListUseCase: TaskItemListUseCase
) : TaskItemListViewModel, ViewModel() {
    override val taskItemListLiveData = MutableLiveData<List<TaskItemResponse>?>()
    override val errorMessageLiveData = MutableLiveData<String>()
    override val progressLoadingLiveData = MutableLiveData<Boolean>()
    override val pageSizeLiveData = MutableLiveData<Int>()

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
            }

            it.onFailure {
                errorMessageLiveData.value = it.message
            }
            progressLoadingLiveData.value = false
        }.launchIn(viewModelScope)

    }
}