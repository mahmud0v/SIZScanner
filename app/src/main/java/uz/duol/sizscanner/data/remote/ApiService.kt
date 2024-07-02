package uz.duol.sizscanner.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url
import uz.duol.sizscanner.data.remote.response.ApiResponse
import uz.duol.sizscanner.data.remote.response.CheckKMResponse
import uz.duol.sizscanner.data.remote.response.CheckPinResponse
import uz.duol.sizscanner.data.remote.response.PageList
import uz.duol.sizscanner.data.remote.response.TaskItemResponse
import uz.duol.sizscanner.data.remote.response.TaskResponse
import uz.duol.sizscanner.data.remote.response.TaskSizeResponse

interface ApiService {

    @POST
    suspend fun checkPin(
        @Url url: String,
        @Query("pinCode") pin: String
    ): Response<ApiResponse<CheckPinResponse>>


    @POST
    suspend fun newTaskList(
        @Url url: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ApiResponse<PageList<List<TaskResponse>>>>


    @POST
    suspend fun taskItemList(
        @Url url: String,
        @Query("transactionId") taskId: Int?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ApiResponse<PageList<List<TaskItemResponse>>>>


    @POST
    suspend fun checkKMFromServer(
        @Url url: String,
        @Query("kmList") kmList: List<String?>,
        @Query("transactionId") transactionId: Int?
    ): Response<ApiResponse<CheckKMResponse>>


    @POST
    suspend fun taskState(
        @Url url: String,
        @Query("transactionId") taskId: Int?
    ):Response<ApiResponse<Boolean>>


}