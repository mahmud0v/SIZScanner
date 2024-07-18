package uz.duol.sizscanner.data.remote

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.duol.sizscanner.BuildConfig
import uz.duol.sizscanner.data.remote.response.ApiResponse
import uz.duol.sizscanner.data.remote.response.CheckPinResponse
import uz.duol.sizscanner.data.sharedpreference.AppSharedPreference
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val sharedPreference: AppSharedPreference,
    @ApplicationContext val context: Context,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {

            val newToken = getNewToken(sharedPreference.refreshToken)
                ?: return@runBlocking null


            if (newToken.accessToken.isNullOrBlank()) {
                sharedPreference.token = ""
            } else {

            }

            var retryCount = response.request.header("RetryCount")?.toInt() ?: 0
            retryCount += 1

            if (retryCount > 2) return@runBlocking null



            newToken.accessToken?.let {
                sharedPreference.token = it
            }
            newToken.refreshToken?.let {
                sharedPreference.refreshToken = it
            }

            response.request.newBuilder()
                .header("RetryCount", "$retryCount")
                .header("Authorization", "Bearer ${sharedPreference.token}")
                .build()
        }
    }


private suspend fun getNewToken(
    refreshToken: String?
): ApiResponse<CheckPinResponse>? {
    return try {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .build()


        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val service = retrofit.create(ApiService::class.java)
        val response = service.refreshToken(
            "osm/mobile-user/auth/refresh"
        )
        when (response.code()) {
            in 200..201 -> response.body()
            else -> {
                sharedPreference.token = ""
                sharedPreference.refreshToken = ""
                null
            }
        }

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

}


}