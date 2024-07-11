package uz.duol.sizscanner.data.repository.db

import uz.duol.sizscanner.data.database.entity.GtinEntity
import uz.duol.sizscanner.data.database.entity.KMModel
import uz.duol.sizscanner.data.model.LimitTotalWaitingKM

interface AppDatabaseRepository {

    suspend fun insertKM(kmModel: KMModel):Long

    suspend fun updateKM(kmModel: KMModel)

    suspend fun failedServerKmList(taskId: Int?): List<String?>

    suspend fun getWaitingKMCount(gtin: String?, taskId:Int?): LimitTotalWaitingKM?

    suspend fun inertGtin(gtinEntity: GtinEntity)

    suspend fun allTaskGtinKM(taskId: Int?, gtin: String?):Int?

    suspend fun existsGtin(gtin: String?, taskId: Int?): Int

    suspend fun editGtinTotalSoldKM(id:Int?, totalKM:Int?, sold:Int?)

    suspend fun getAllGtinDB(taskId: Int?):List<GtinEntity>

    suspend fun editWaitingKM(waitingKM: Int?, gtin: String?, taskId: Int?):Int

    suspend fun existsKM(km:String?):Int

    suspend fun kmChangeStatusScannedVerified(km: String?)

    suspend fun countNotVerifiedTaskGtinKM(gtin: String?, taskId: Int?):Int?

    suspend fun deleteKM(km:String?)
}