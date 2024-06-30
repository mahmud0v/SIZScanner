package uz.duol.sizscanner.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.duol.sizscanner.domain.usecase.CheckKMUsaCase
import uz.duol.sizscanner.domain.usecase.CheckPinUseCase
import uz.duol.sizscanner.domain.usecase.NewTaskListUseCase
import uz.duol.sizscanner.domain.usecase.TaskItemListUseCase
import uz.duol.sizscanner.domain.usecase.impl.CheckKMUsaCaseImpl
import uz.duol.sizscanner.domain.usecase.impl.CheckPinUseCaseImpl
import uz.duol.sizscanner.domain.usecase.impl.NewTaskListUseCaseImpl
import uz.duol.sizscanner.domain.usecase.impl.TaskItemListUseCaseImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {

    @[Binds Singleton]
    fun checkPinUseCase(impl: CheckPinUseCaseImpl): CheckPinUseCase

    @[Binds Singleton]
    fun newTaskListUseCase(impl: NewTaskListUseCaseImpl): NewTaskListUseCase

    @[Binds Singleton]
    fun taskItemListUseCase(impl: TaskItemListUseCaseImpl): TaskItemListUseCase

    @[Binds Singleton]
    fun checkKMUseCase(impl:CheckKMUsaCaseImpl): CheckKMUsaCase

}