package uz.duol.sizscanner.presentation.viewmodel.checkKM

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.duol.sizscanner.data.database.entity.GtinEntity
import uz.duol.sizscanner.data.model.ChangeWaitingGtinCountInfo
import uz.duol.sizscanner.data.model.ExistsKMInfo
import uz.duol.sizscanner.data.model.WaitingGtinInfo
import uz.duol.sizscanner.data.remote.response.CheckKMResponse
import uz.duol.sizscanner.domain.usecase.CheckKMUsaCase
import uz.duol.sizscanner.domain.usecase.GtinUseCase
import uz.duol.sizscanner.domain.usecase.KMSaveDBUseCase
import javax.inject.Inject

@HiltViewModel
class CheckKMViewModelImpl @Inject constructor(
    private val checkKMUsaCase: CheckKMUsaCase,
    private val gtinUseCase: GtinUseCase,
    private val kmSaveDBUseCase: KMSaveDBUseCase
) : CheckKMViewModel, ViewModel() {
    override val errorMessageLiveData = MutableLiveData<String>()
    override val successCheckKMLiveData = MutableLiveData<CheckKMResponse?>()
    override val getWaitingKMCountLiveData = MutableLiveData<WaitingGtinInfo>()
    override val inertGtinLiveData = MutableLiveData<Unit>()
    override val allTaskGtinKMErrorLiveData = MutableLiveData<String>()
    override val allTaskGtinKMLiveData = MutableLiveData<ExistsKMInfo?>()
    override val kmChangeStatusScannedVerifiedLiveData = MutableLiveData<Unit>()
    override val countNotVerifiedTaskGtinKMLiveData= MutableLiveData<WaitingGtinInfo?>()
    override val changeWaitingKMCountLiveData = MutableLiveData<ChangeWaitingGtinCountInfo?>()
    override val waitingKMForInsertLiveData = MutableLiveData<WaitingGtinInfo?>()


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
                    totalKM = it?.totalKM,
                    waitingGtinCount = it?.waitingKM,
                    differenceKM = it?.differenceKM,
                    gtin = gtin,
                    taskId = taskId
                )
            }

            it.onFailure {}
        }.launchIn(viewModelScope)
    }

    override fun changeWaitingKMCount(count: Int, gtin: String?, taskId: Int?) {
       gtinUseCase.getWaitingKMCount(gtin, taskId).onEach {
           it.onSuccess {
               changeWaitingKMCountLiveData.value = ChangeWaitingGtinCountInfo(it?.waitingKM, count, gtin, taskId)
           }

           it.onFailure {}

       }.launchIn(viewModelScope)
    }

    override fun getWaitingKMForInsert(
        gtin: String?,
        taskId: Int?,
        km: String?,
        gtinKMCountNotVerified: Int?
    ) {
        gtinUseCase.getWaitingKMCount(gtin, taskId).onEach {
            it.onSuccess {
                waitingKMForInsertLiveData.value = WaitingGtinInfo(
                    totalKM = it?.totalKM,
                    waitingGtinCount = it?.waitingKM,
                    differenceKM = it?.differenceKM,
                    gtin = gtin,
                    taskId = taskId,
                    insertKM = km,
                    gtinKMCountNotVerified = gtinKMCountNotVerified
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



    override fun allTaskGtinKM(gtin: String?, taskId: Int?, existsKMInfo: ExistsKMInfo?) {
        checkKMUsaCase.allTaskGtinKM(taskId, gtin).onEach {
            it.onSuccess {
                allTaskGtinKMLiveData.value = ExistsKMInfo(existsKMInfo?.km, existsKMInfo?.exist, it)
            }

            it.onFailure {
                allTaskGtinKMErrorLiveData.value = it.message
            }
        }.launchIn(viewModelScope)
    }

    override fun kmChangeStatusScannedVerified(km: String?) {
        kmSaveDBUseCase.kmChangeStatusScannedVerified(km).onEach {
            it.onSuccess {
                kmChangeStatusScannedVerifiedLiveData.value = it
            }

            it.onFailure {

            }
        }.launchIn(viewModelScope)
    }

    override fun countNotVerifiedTaskGtinKM(waitingGtinInfo: WaitingGtinInfo?) {
        kmSaveDBUseCase.countNotVerifiedTaskGtinKM(waitingGtinInfo?.gtin, waitingGtinInfo?.taskId).onEach {
            it.onSuccess {
                waitingGtinInfo?.kmModelCountKM = it
                countNotVerifiedTaskGtinKMLiveData.value = waitingGtinInfo
            }

            it.onFailure {

            }
        }.launchIn(viewModelScope)
    }

    override fun deleteKM(km: String?) {
        kmSaveDBUseCase.deleteKM(km)
            .launchIn(viewModelScope)
    }


}