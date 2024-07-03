package uz.duol.sizscanner.domain.usecase

import kotlinx.coroutines.flow.Flow
import uz.duol.sizscanner.data.database.entity.GtinEntity
import uz.duol.sizscanner.data.model.LimitTotalWaitingKM


interface GtinUseCase {

    fun getWaitingKMCount(gtin: String?, taskId:Int?): Flow<Result<LimitTotalWaitingKM?>>

    fun insertGtin(gtinEntity: GtinEntity): Flow<Result<Unit>>

    fun existsGtin(gtin: String?, taskId: Int?): Flow<Result<Int>>

    fun editGtinTotalSoldKM(id:Int?, totalKM:Int?, sold:Int?): Flow<Result<Unit>>

    fun getAllGtinDB(taskId: Int?): Flow<Result<List<GtinEntity>>>

    fun editWaitingKM(waitingKM: Int?, gtin: String?, taskId: Int?):Flow<Result<Int>>

    fun existsKM(km:String?): Flow<Result<Int>>



}