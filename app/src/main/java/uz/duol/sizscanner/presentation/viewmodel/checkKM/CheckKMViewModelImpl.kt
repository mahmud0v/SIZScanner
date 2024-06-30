package uz.duol.sizscanner.presentation.viewmodel.checkKM

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.duol.sizscanner.domain.usecase.CheckKMUsaCase
import javax.inject.Inject

@HiltViewModel
class CheckKMViewModelImpl @Inject constructor(
    private val checkKMUsaCase: CheckKMUsaCase
) : CheckKMViewModel, ViewModel() {
    override val errorMessageLiveData = MutableLiveData<String>()
    override val successCheckKMLiveData = MutableLiveData<Boolean?>()

    override fun checkKMFromServer(km: String?) {
        checkKMUsaCase.checkKMFromServer(km).onEach {
            it.onSuccess {
                successCheckKMLiveData.value = it
            }

            it.onFailure {
                errorMessageLiveData.value = it.message
            }
        }.launchIn(viewModelScope)
    }
}