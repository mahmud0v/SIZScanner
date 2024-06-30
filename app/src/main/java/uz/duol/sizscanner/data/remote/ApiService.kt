package uz.duol.sizscanner.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url
import uz.duol.sizscanner.data.remote.response.ApiResponse
import uz.duol.sizscanner.data.remote.response.CheckPinResponse
import uz.duol.sizscanner.data.remote.response.PageList
import uz.duol.sizscanner.data.remote.response.TaskItemResponse
import uz.duol.sizscanner.data.remote.response.TaskResponse
import uz.duol.sizscanner.data.remote.response.TaskSizeResponse

interface ApiService {

    @POST
    suspend fun checkPin(
        @Url url:String,
        @Query("pinCode") pin:String
    ): Response<ApiResponse<CheckPinResponse>>


    @GET
    suspend fun newTaskList(
        @Url url: String,
        @Query("page") page:Int,
        @Query("size") size:Int
    ): Response<ApiResponse<PageList<List<TaskResponse>>>>

    @GET
    suspend fun taskItemList(
        @Url url: String,
        @Query("transactionId") taskId:Int?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ApiResponse<PageList<List<TaskItemResponse>>>>

    @GET
    suspend fun checkKMFromServer(
        @Url url: String,
        @Query("km") km:String?
    ): Response<ApiResponse<Boolean>>



}