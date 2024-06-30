package uz.duol.sizscanner.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.duol.sizscanner.data.remote.response.PageList
import uz.duol.sizscanner.data.remote.response.TaskItemResponse

interface TaskItemListUseCase {

    fun taskItemList(taskItemId:Int?, page:Int, size:Int) : Flow<Result<PageList<List<TaskItemResponse>>?>>

}