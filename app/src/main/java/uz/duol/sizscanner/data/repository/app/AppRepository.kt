package uz.duol.sizscanner.data.repository.app

import retrofit2.Response
import uz.duol.sizscanner.data.remote.response.ApiResponse
import uz.duol.sizscanner.data.remote.response.CheckKMResponse
import uz.duol.sizscanner.data.remote.response.CheckPinResponse
import uz.duol.sizscanner.data.remote.response.PageList
import uz.duol.sizscanner.data.remote.response.TaskItemResponse
import uz.duol.sizscanner.data.remote.response.TaskResponse


interface AppRepository {

    suspend fun checkPin(pin:String, deviceId:String?) : Response<ApiResponse<CheckPinResponse>>

    suspend fun newTaskList(page:Int, size:Int) : Response<ApiResponse<PageList<List<TaskResponse>>>>

    suspend fun taskItemList(taskItemId:Int?, page: Int, size: Int): Response<ApiResponse<PageList<List<TaskItemResponse>>>>

    suspend fun checkKMFromServer(km: String?, transactionId:Int?): Response<ApiResponse<CheckKMResponse>>

    suspend fun checkTaskStatus(transactionId:Int?): Response<ApiResponse<Boolean>>

    suspend fun logout(): Response<ApiResponse<Unit>>

}