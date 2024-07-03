package uz.duol.sizscanner.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.duol.sizscanner.domain.usecase.CheckKMUsaCase
import uz.duol.sizscanner.domain.usecase.CheckPinUseCase
import uz.duol.sizscanner.domain.usecase.GtinUseCase
import uz.duol.sizscanner.domain.usecase.KMSaveDBUseCase
import uz.duol.sizscanner.domain.usecase.NewTaskListUseCase
import uz.duol.sizscanner.domain.usecase.TaskItemListUseCase
import uz.duol.sizscanner.domain.usecase.TaskStatusUseCase
import uz.duol.sizscanner.domain.usecase.impl.CheckKMUsaCaseImpl
import uz.duol.sizscanner.domain.usecase.impl.CheckPinUseCaseImpl
import uz.duol.sizscanner.domain.usecase.impl.GtinUseCaseImpl
import uz.duol.sizscanner.domain.usecase.impl.KMSaveDBUseCaseImpl
import uz.duol.sizscanner.domain.usecase.impl.NewTaskListUseCaseImpl
import uz.duol.sizscanner.domain.usecase.impl.TaskItemListUseCaseImpl
import uz.duol.sizscanner.domain.usecase.impl.TaskStatusUseCaseImpl

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {

    @[Binds]
    fun checkPinUseCase(impl: CheckPinUseCaseImpl): CheckPinUseCase

    @[Binds]
    fun newTaskListUseCase(impl: NewTaskListUseCaseImpl): NewTaskListUseCase

    @[Binds]
    fun taskItemListUseCase(impl: TaskItemListUseCaseImpl): TaskItemListUseCase

    @[Binds]
    fun checkKMUseCase(impl:CheckKMUsaCaseImpl): CheckKMUsaCase

    @[Binds]
    fun insertKM(impl: KMSaveDBUseCaseImpl): KMSaveDBUseCase

    @[Binds]
    fun taskStatusUseCase(impl:TaskStatusUseCaseImpl): TaskStatusUseCase

    @[Binds]
    fun getWaitingKMCount(impl:GtinUseCaseImpl): GtinUseCase

}