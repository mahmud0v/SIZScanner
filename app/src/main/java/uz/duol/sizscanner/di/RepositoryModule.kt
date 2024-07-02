package uz.duol.sizscanner.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.duol.sizscanner.data.repository.app.AppRepository
import uz.duol.sizscanner.data.repository.app.AppRepositoryImpl
import uz.duol.sizscanner.data.repository.db.AppDatabaseRepository
import uz.duol.sizscanner.data.repository.db.AppDatabaseRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @[Binds Singleton]
    fun getAppRepository(impl: AppRepositoryImpl):AppRepository

    @[Binds Singleton]
    fun getAppDBRepository(impl:AppDatabaseRepositoryImpl): AppDatabaseRepository


}