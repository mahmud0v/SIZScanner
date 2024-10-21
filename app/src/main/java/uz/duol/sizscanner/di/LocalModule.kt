package uz.duol.sizscanner.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.duol.sizscanner.data.sharedpreference.AppSharedPreference
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {

    @[Provides Singleton]
    fun getSharedPreference(@ApplicationContext context: Context): AppSharedPreference =
        AppSharedPreference(context)





}