package uz.duol.sizscanner.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.duol.sizscanner.data.database.dao.GtinDao
import uz.duol.sizscanner.data.database.dao.ProductDao
import uz.duol.sizscanner.data.database.preference.AppDatabase
import uz.duol.sizscanner.data.sharedpreference.AppSharedPreference
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {

    @[Provides Singleton]
    fun getSharedPreference(@ApplicationContext context: Context): AppSharedPreference =
        AppSharedPreference(context)

    @[Provides Singleton]
    fun getDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "SIZ Scanner")
            .build()

    @[Provides Singleton]
    fun getProductDao(database: AppDatabase): ProductDao = database.getProductDao()

    @[Provides Singleton]
    fun getGtinDao(database: AppDatabase): GtinDao = database.getGtinDao()



}