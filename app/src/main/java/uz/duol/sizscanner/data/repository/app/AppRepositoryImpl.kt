package uz.duol.sizscanner.data.repository.app

import retrofit2.Response
import uz.duol.sizscanner.data.remote.ApiService
import uz.duol.sizscanner.data.remote.response.ApiResponse
import uz.duol.sizscanner.data.remote.response.CheckKMResponse
import uz.duol.sizscanner.data.remote.response.CheckPinResponse
import uz.duol.sizscanner.data.remote.response.PageList
import uz.duol.sizscanner.data.remote.response.TaskItemResponse
import uz.duol.sizscanner.data.remote.response.TaskResponse
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AppRepository {


    override suspend fun checkPin(pin: String): Response<ApiResponse<CheckPinResponse>> {
        return apiService.checkPin(
            "osm/mobile-user/login",
            pin
        )
    }

    override suspend fun newTaskList(
        page: Int,
        size: Int
    ): Response<ApiResponse<PageList<List<TaskResponse>>>> {
        return apiService.newTaskList(
            "osm/mobile-user/task-list",
            page,
            size
        )
    }

    override suspend fun taskItemList(
        taskItemId: Int?,
        page: Int,
        size: Int
    ): Response<ApiResponse<PageList<List<TaskItemResponse>>>> {
        return apiService.taskItemList(
            "osm/mobile-user/transaction-item-km",
            taskItemId,
            page,
            size
        )
    }

    override suspend fun checkKMFromServer(kmList: List<String?>, transactionId:Int?): Response<ApiResponse<CheckKMResponse>> {
        return apiService.checkKMFromServer(
            "osm/mobile-user/sync-status",
            kmList,
            transactionId
        )
    }

    override suspend fun taskState(transactionId: Int?): Response<ApiResponse<Boolean>> {
        return apiService.taskState(
            "osm/mobile-user/sync-closed",
            transactionId
        )
    }


}