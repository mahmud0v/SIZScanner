package uz.duol.sizscanner.presentation.viewmodel.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.duol.sizscanner.data.remote.response.TaskResponse
import uz.duol.sizscanner.domain.usecase.LogoutUseCase
import uz.duol.sizscanner.domain.usecase.NewTaskListUseCase
import javax.inject.Inject

@HiltViewModel
class NewTaskLisViewModelImpl @Inject constructor(
  private val newTaskListUseCase: NewTaskListUseCase,
    private val logoutUseCase: LogoutUseCase
) : NewTaskListViewModel, ViewModel() {
    override val newTaskListLiveData = MutableLiveData<List<TaskResponse>?>()
    override val errorMessageLiveData = MutableLiveData<String>()
    override val progressLoadingLiveData = MutableLiveData<Boolean>()
    override val pageSizeLiveData = MutableLiveData<Int>()
    override val totalSizeLiveData = MutableLiveData<Int>()
    override val logoutLiveData = MutableLiveData<Unit>()


    private var maxPage: Int = 0


    override fun newTaskList(page: Int, size: Int) {
        progressLoadingLiveData.value = true
        newTaskListUseCase.newTaskList(page, size).onEach {
            it.onSuccess {
                newTaskListLiveData.value = it?.rows
                it?.total?.let { recordsTotal ->
                    maxPage = recordsTotal / 10
                    if (recordsTotal % 10 != 0) maxPage++
                    pageSizeLiveData.value = maxPage
                    totalSizeLiveData.value = recordsTotal
                }
            }

            it.onFailure {
                errorMessageLiveData.value = it.message
            }
            progressLoadingLiveData.value = false
        }.launchIn(viewModelScope)
    }

    override fun logout() {
       logoutUseCase.logout().onEach {
           it.onSuccess {
               logoutLiveData.value = it
           }

           it.onFailure {
               errorMessageLiveData.value = it.message
           }
       }.launchIn(viewModelScope)
    }
}