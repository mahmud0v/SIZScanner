package uz.duol.sizscanner.presentation.viewmodel.checkKM

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.duol.sizscanner.data.remote.response.CheckKMResponse
import uz.duol.sizscanner.domain.usecase.CheckKMUsaCase
import javax.inject.Inject

@HiltViewModel
class CheckKMViewModelImpl @Inject constructor(
    private val checkKMUsaCase: CheckKMUsaCase
) : CheckKMViewModel, ViewModel() {
    override val errorMessageLiveData = MutableLiveData<String>()
    override val successCheckKMLiveData = MutableLiveData<CheckKMResponse?>()

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
}