package uz.duol.sizscanner.presentation.viewmodel.checkKM

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.duol.sizscanner.data.database.entity.GtinEntity
import uz.duol.sizscanner.data.model.WaitingGtinInfo
import uz.duol.sizscanner.data.remote.response.CheckKMResponse
import uz.duol.sizscanner.domain.usecase.CheckKMUsaCase
import uz.duol.sizscanner.domain.usecase.GtinUseCase
import javax.inject.Inject

@HiltViewModel
class CheckKMViewModelImpl @Inject constructor(
    private val checkKMUsaCase: CheckKMUsaCase,
    private val gtinUseCase: GtinUseCase
) : CheckKMViewModel, ViewModel() {
    override val errorMessageLiveData = MutableLiveData<String>()
    override val successCheckKMLiveData = MutableLiveData<CheckKMResponse?>()
    override val getWaitingKMCountLiveData = MutableLiveData<WaitingGtinInfo>()
    override val inertGtinLiveData = MutableLiveData<Unit>()
    override val allTaskGtinKMErrorLiveData = MutableLiveData<String>()
    override val allTaskGtinKMLiveData2 = MutableLiveData<Int?>()
    override val allTaskGtinKMErrorLiveData2 = MutableLiveData<String>()


    override fun checkKMFromServer(kmList: List<String?>, transactionId:Int?) {
        checkKMUsaCase.checkKMFromServer(kmList, transactionId).onEach {
            it.onSuccess {
                successCheckKMLiveData.value = it
            }

            it.onFailure {
                errorMessageLiveData.value = it.message
            }
        }.launchIn(viewModelScope)
    }

    override fun getWaitingKMCount(gtin: String?, taskId: Int?) {
        gtinUseCase.getWaitingKMCount(gtin, taskId).onEach {
            it.onSuccess {
                getWaitingKMCountLiveData.value = WaitingGtinInfo(
                    waitingGtinCount = it?.waitingKM,
                    differenceKM = it?.differenceKM,
                    gtin = gtin,
                    taskId = taskId
                )
            }

            it.onFailure {}
        }.launchIn(viewModelScope)
    }

    override fun insertGtin(gtinEntity: GtinEntity) {
        gtinUseCase.insertGtin(gtinEntity).onEach {
            it.onSuccess {
                inertGtinLiveData.value = it
            }

            it.onFailure {}
        }.launchIn(viewModelScope)
    }



    override fun allTaskGtinKM2(gtin: String?, taskId: Int?) {
        checkKMUsaCase.allTaskGtinKM(taskId, gtin).onEach {
            it.onSuccess {
                allTaskGtinKMLiveData2.value = it
            }

            it.onFailure {
                allTaskGtinKMErrorLiveData2.value = it.message
            }
        }.launchIn(viewModelScope)
    }


}