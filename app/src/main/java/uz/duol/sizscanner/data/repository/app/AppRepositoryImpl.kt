package uz.duol.sizscanner.data.repository.app

import retrofit2.Response
import uz.duol.sizscanner.BuildConfig.BASE_URL
import uz.duol.sizscanner.data.remote.ApiService
import uz.duol.sizscanner.data.remote.response.ApiResponse
import uz.duol.sizscanner.data.remote.response.CheckPinResponse
import uz.duol.sizscanner.data.remote.response.PageList
import uz.duol.sizscanner.data.remote.response.TaskItemResponse
import uz.duol.sizscanner.data.remote.response.TaskResponse
import uz.duol.sizscanner.data.remote.response.TaskSizeResponse
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

    override suspend fun checkKMFromServer(km: String?): Response<ApiResponse<Boolean>> {
        return apiService.checkKMFromServer(
            "osm/mobile-user/sync-status",
            km
        )
    }



}