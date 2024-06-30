package uz.duol.sizscanner.data.repository.app

import retrofit2.Response
import uz.duol.sizscanner.data.remote.response.ApiResponse
import uz.duol.sizscanner.data.remote.response.CheckPinResponse
import uz.duol.sizscanner.data.remote.response.PageList
import uz.duol.sizscanner.data.remote.response.TaskItemResponse
import uz.duol.sizscanner.data.remote.response.TaskResponse
import uz.duol.sizscanner.data.remote.response.TaskSizeResponse


interface AppRepository {

    suspend fun checkPin(pin:String) : Response<ApiResponse<CheckPinResponse>>

    suspend fun newTaskList(page:Int, size:Int) : Response<ApiResponse<PageList<List<TaskResponse>>>>

    suspend fun taskItemList(taskItemId:Int?, page: Int, size: Int): Response<ApiResponse<PageList<List<TaskItemResponse>>>>

    suspend fun checkKMFromServer(km:String?): Response<ApiResponse<Boolean>>

}