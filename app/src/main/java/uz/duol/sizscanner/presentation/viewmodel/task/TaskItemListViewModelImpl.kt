package uz.duol.sizscanner.presentation.viewmodel.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.duol.sizscanner.data.database.entity.KMModel
import uz.duol.sizscanner.data.remote.response.TaskItemResponse
import uz.duol.sizscanner.domain.usecase.KMSaveDBUseCase
import uz.duol.sizscanner.domain.usecase.TaskItemListUseCase
import uz.duol.sizscanner.domain.usecase.TaskStatusUseCase
import javax.inject.Inject

@HiltViewModel
class TaskItemListViewModelImpl @Inject constructor(
    private val taskItemListUseCase: TaskItemListUseCase,
    private val kmSaveDBUseCase: KMSaveDBUseCase,
    private val taskStatusUseCase: TaskStatusUseCase
) : TaskItemListViewModel, ViewModel() {
    override val taskItemListLiveData = MutableLiveData<List<TaskItemResponse>?>()
    override val errorMessageLiveData = MutableLiveData<String>()
    override val progressLoadingLiveData = MutableLiveData<Boolean>()
    override val pageSizeLiveData = MutableLiveData<Int>()
    override val addFailedKMSaveDB = MutableLiveData<Unit>()
    override val taskStatusLiveData = MutableLiveData<String?>()
    override val failedServerKMListLiveData = MutableLiveData<List<String?>?>()
    override val errorMessageFailedServerKMListLiveData = MutableLiveData<String>()
    override val taskMainStatusLiveData = MutableLiveData<Boolean?>()



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

    override fun insertKMDB(kmModel: KMModel) {
        kmSaveDBUseCase.insertKM(kmModel).onEach {
            it.onFailure {
                errorMessageLiveData.value = it.message
            }

            it.onSuccess {
                addFailedKMSaveDB.value = Unit
            }
        }.launchIn(viewModelScope)
    }

    override fun failedServerKMList(taskId: Int?) {
        kmSaveDBUseCase.failedServerKMList(taskId).onEach {
            it.onSuccess {
                failedServerKMListLiveData.value = it
            }

            it.onFailure {
                errorMessageFailedServerKMListLiveData.value = it.message
            }
        }.launchIn(viewModelScope)
    }

    override fun taskStatus(transactionId: Int?) {
        taskStatusUseCase.taskStatus(transactionId).onEach {
            it.onSuccess {
                taskMainStatusLiveData.value = it
            }

            it.onFailure {
                errorMessageLiveData.value = it.message
            }

        }.launchIn(viewModelScope)
    }


}