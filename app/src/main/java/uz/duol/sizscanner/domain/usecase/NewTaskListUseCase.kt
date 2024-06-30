package uz.duol.sizscanner.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.duol.sizscanner.data.remote.response.PageList
import uz.duol.sizscanner.data.remote.response.TaskResponse

interface NewTaskListUseCase {

    fun newTaskList(page:Int, size:Int): Flow<Result<PageList<List<TaskResponse>>?>>

}