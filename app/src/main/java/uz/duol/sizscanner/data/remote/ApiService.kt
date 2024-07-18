package uz.duol.sizscanner.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url
import uz.duol.sizscanner.data.remote.request.CheckKMRequest
import uz.duol.sizscanner.data.remote.request.CheckTaskStatus
import uz.duol.sizscanner.data.remote.request.LoginRequest
import uz.duol.sizscanner.data.remote.response.ApiResponse
import uz.duol.sizscanner.data.remote.response.CheckKMResponse
import uz.duol.sizscanner.data.remote.response.CheckPinResponse
import uz.duol.sizscanner.data.remote.response.PageList
import uz.duol.sizscanner.data.remote.response.TaskItemResponse
import uz.duol.sizscanner.data.remote.response.TaskResponse

interface ApiService {

    @POST
    suspend fun checkPin(
        @Url url: String,
        @Body loginRequest: LoginRequest
    ): Response<ApiResponse<CheckPinResponse>>


    @GET
    suspend fun newTaskList(
        @Url url: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ApiResponse<PageList<List<TaskResponse>>>>


    @GET
    suspend fun taskItemList(
        @Url url: String,
        @Query("transactionId") taskId: Int?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ApiResponse<PageList<List<TaskItemResponse>>>>


    @POST
    suspend fun checkKMFromServer(
        @Url url: String,
        @Body checkKMRequest: CheckKMRequest
    ): Response<ApiResponse<CheckKMResponse>>


    @POST
    suspend fun checkTaskStatus(
        @Url url: String,
        @Body checkTaskStatus: CheckTaskStatus
    ):Response<ApiResponse<Boolean>>

    @POST
    suspend fun refreshToken(
        @Url url: String
    ): Response<ApiResponse<CheckPinResponse>>

    @POST
    suspend fun logout(
        @Url url: String
    ): Response<ApiResponse<Unit>>


}