package uz.duol.sizscanner.data.repository.db

import uz.duol.sizscanner.data.database.dao.GtinDao
import uz.duol.sizscanner.data.database.dao.ProductDao
import uz.duol.sizscanner.data.database.entity.GtinEntity
import uz.duol.sizscanner.data.database.entity.KMModel
import uz.duol.sizscanner.data.model.LimitTotalWaitingKM
import javax.inject.Inject

class AppDatabaseRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val gtinDao: GtinDao
) : AppDatabaseRepository {


    override suspend fun insertKM(kmModel: KMModel): Long {
        return productDao.insertKM(kmModel)
    }

    override suspend fun updateKM(kmModel: KMModel) {
        productDao.updateKM(kmModel)
    }

    override suspend fun failedServerKmList(taskId: Int?): List<String?> {
        return productDao.failedServerKMList(taskId)
    }

    override suspend fun getWaitingKMCount(gtin: String?, taskId: Int?): LimitTotalWaitingKM? {
        return gtinDao.getWaitingKMCount(gtin, taskId)
    }

    override suspend fun inertGtin(gtinEntity: GtinEntity) {
        return gtinDao.insertGtin(gtinEntity)
    }

    override suspend fun allTaskGtinKM(taskId: Int?, gtin: String?): Int? {
        return productDao.allTaskGtinKM(taskId, gtin)
    }

    override suspend fun existsGtin(gtin: String?, taskId: Int?): Int {
        return gtinDao.existsGtin(gtin, taskId)
    }

    override suspend fun editGtinTotalSoldKM(id: Int?, totalKM: Int?, sold: Int?) {
        return gtinDao.editGtinTotalSoldKM(id, totalKM, sold)
    }

    override suspend fun getAllGtinDB(taskId: Int?): List<GtinEntity> {
        return gtinDao.getAllGtinDB(taskId)
    }

    override suspend fun editWaitingKM(waitingKM: Int?, gtin: String?, taskId: Int?): Int {
        return gtinDao.editWaitingKM(waitingKM, gtin, taskId)
    }

    override suspend fun existsKM(km: String?): Int {
        return gtinDao.existsKM(km)
    }

    override suspend fun kmChangeStatusScannedVerified(km: String?) {
        return productDao.kmChangeStatusScannedVerified(km)
    }

    override suspend fun countNotVerifiedTaskGtinKM(gtin: String?, taskId: Int?): Int? {
        return productDao.countNotVerifiedTaskGtinKM(gtin, taskId)
    }

    override suspend fun deleteKM(km: String?) {
        return productDao.deleteKM(km)
    }
}