package uz.duol.sizscanner.presentation.viewmodel.pin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.duol.sizscanner.data.remote.response.CheckPinResponse
import uz.duol.sizscanner.domain.usecase.CheckPinUseCase
import javax.inject.Inject

@HiltViewModel
class CheckPinViewModelImpl @Inject constructor(
    private val checkPinUseCase: CheckPinUseCase
) : CheckPinViewModel, ViewModel() {

    override val checkPinLiveData = MutableLiveData<CheckPinResponse?>()
    override val errorMessageLiveData = MutableLiveData<String>()
    override val progressLoadingLiveData = MutableLiveData<Boolean>()

    override fun checkPin(pin: String, deviceId: String?) {
        progressLoadingLiveData.value = true
        checkPinUseCase.checkPin(pin, deviceId).onEach {
            it.onSuccess {
                checkPinLiveData.value = it
            }

            it.onFailure {
                errorMessageLiveData.value = it.message
            }
            progressLoadingLiveData.value = false
        }.launchIn(viewModelScope)
    }






}