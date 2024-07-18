package uz.duol.sizscanner.presentation.viewmodel.task

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.duol.sizscanner.data.database.entity.GtinEntity
import uz.duol.sizscanner.data.database.entity.KMModel
import uz.duol.sizscanner.data.model.ExistsKMInfo
import uz.duol.sizscanner.data.model.InsertKMInfo
import uz.duol.sizscanner.data.remote.response.TaskItemResponse
import uz.duol.sizscanner.domain.usecase.GtinUseCase
import uz.duol.sizscanner.domain.usecase.KMSaveDBUseCase
import uz.duol.sizscanner.domain.usecase.TaskItemListUseCase
import uz.duol.sizscanner.domain.usecase.TaskStatusUseCase
import javax.inject.Inject

@HiltViewModel
class TaskItemListViewModelImpl @Inject constructor(
    private val taskItemListUseCase: TaskItemListUseCase,
    private val kmSaveDBUseCase: KMSaveDBUseCase,
    private val taskStatusUseCase: TaskStatusUseCase,
    private val gtinUseCase: GtinUseCase
) : TaskItemListViewModel, ViewModel() {
    override val taskItemListLiveData = MutableLiveData<List<TaskItemResponse>?>()
    override val errorMessageLiveData = MutableLiveData<String>()
    override val progressLoadingLiveData = MutableLiveData<Boolean>()
    override val pageSizeLiveData = MutableLiveData<Int>()
    override val addWaitingKMSaveDB = MutableLiveData<InsertKMInfo>()
    override val taskStatusLiveData = MutableLiveData<String?>()
    override val scannedNotVerifiedKMListLiveData = MutableLiveData<List<String?>?>()
    override val errorScannedNotVerifiedKMListLiveData = MutableLiveData<String>()
    override val taskMainStatusLiveData = MutableLiveData<Boolean?>()
    override val errorTaskMainStatusLiveData = MutableLiveData<String>()
    override val existGtinLiveData = MutableLiveData<TaskItemResponse?>()
    override val editGtinTotalSoldKMLiveData = MutableLiveData<Unit>()
    override val getAllGtinDBLiveData = MutableLiveData<List<GtinEntity>>()
    override val editWaitingKMLiveData = MutableLiveData<Int>()
    override val existKMLiveData = MutableLiveData<ExistsKMInfo>()
    override val horizontalProgressLiveData = MutableLiveData<Boolean>()


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

            it.onSuccess {
                addWaitingKMSaveDB.value = InsertKMInfo(it, kmModel.km)
            }

            it.onFailure {
                errorMessageLiveData.value = it.message
            }

        }.launchIn(viewModelScope)
    }

    override fun scannedNotVerifiedKMList(taskId: Int?) {
        horizontalProgressLiveData.value = false

        kmSaveDBUseCase.scannedNotVerifiedKMList(taskId).onEach {
            horizontalProgressLiveData.value = true
            it.onSuccess {
                scannedNotVerifiedKMListLiveData.value = it
            }

            it.onFailure {
                errorScannedNotVerifiedKMListLiveData.value = it.message
            }

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

    override fun existGtin(gtin: String?, taskId: Int?, taskItem: TaskItemResponse?) {
        gtinUseCase.existsGtin(gtin, taskId).onEach {
            it.onSuccess {
                taskItem?.existDB = it
                existGtinLiveData.value = taskItem
            }

            it.onFailure { }
        }.launchIn(viewModelScope)
    }

    override fun editGtinTotalSoldKM(id: Int?, totalKM: Int?, soldKM: Int?) {
        gtinUseCase.editGtinTotalSoldKM(id, totalKM, soldKM).onEach {
            it.onSuccess {
                editGtinTotalSoldKMLiveData.value = Unit
            }

            it.onFailure {

            }
        }.launchIn(viewModelScope)
    }

    override fun getAllGtinDB(taskId: Int?) {
        gtinUseCase.getAllGtinDB(taskId).onEach {
            it.onSuccess {
                getAllGtinDBLiveData.value = it
            }

            it.onFailure {

            }
        }.launchIn(viewModelScope)
    }

    override fun editWaitingKM(waitingKM: Int?, gtin: String?, taskId: Int?) {
        gtinUseCase.editWaitingKM(waitingKM, gtin, taskId).onEach {
            it.onSuccess {
                editWaitingKMLiveData.value = it
            }

            it.onFailure {

            }
        }.launchIn(viewModelScope)
    }

    override fun existKM(km: String?) {
        gtinUseCase.existsKM(km).onEach {
            it.onSuccess {
                existKMLiveData.value = ExistsKMInfo(km,it)
            }

            it.onFailure {}
        }.launchIn(viewModelScope)
    }


}