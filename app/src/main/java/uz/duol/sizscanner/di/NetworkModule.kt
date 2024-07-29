package uz.duol.sizscanner.di

import android.content.Context
import android.util.Log
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.duol.sizscanner.BuildConfig
import uz.duol.sizscanner.data.remote.ApiService
import uz.duol.sizscanner.data.remote.AuthAuthenticator
import uz.duol.sizscanner.data.sharedpreference.AppSharedPreference
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @[Provides]
    fun tokenInterceptor(sharedPreference: AppSharedPreference): Interceptor {
        return Interceptor { chain ->
            val url = chain.request().url
            if (!url.toString().contains("login")) {
                val newRequest = chain.request().newBuilder()
                newRequest.addHeader("Authorization", sharedPreference.token)
                Log.d("DDDD", "url: ${url}\n Authorization: ${sharedPreference.token}")
                val response = chain.proceed(newRequest.build())
                return@Interceptor response
            } else {
                val request = chain.request().newBuilder()
                val response = chain.proceed(request.build())
                return@Interceptor response
            }
        }
    }


    @[Provides Singleton]
    fun getOkHttp(
        @ApplicationContext context: Context,
        authAuthenticator: AuthAuthenticator,
        appSharedPreference: AppSharedPreference
    ) = OkHttpClient().newBuilder().readTimeout(5, TimeUnit.MINUTES)
        .connectTimeout(5, TimeUnit.MINUTES).callTimeout(5, TimeUnit.MINUTES)
        .writeTimeout(5, TimeUnit.MINUTES)
        .addInterceptor(tokenInterceptor(appSharedPreference))
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor(ChuckerInterceptor.Builder(context).build())
        .authenticator(authAuthenticator)
        .build()


    @[Provides Singleton]
    fun getRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @[Provides Singleton]
    fun getApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)




}