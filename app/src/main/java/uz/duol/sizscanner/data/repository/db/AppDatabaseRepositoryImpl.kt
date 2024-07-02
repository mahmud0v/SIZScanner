package uz.duol.sizscanner.data.repository.db

import uz.duol.sizscanner.data.database.dao.ProductDao
import uz.duol.sizscanner.data.database.entity.KMModel
import javax.inject.Inject

class AppDatabaseRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
): AppDatabaseRepository {


    override suspend fun insertKM(kmModel: KMModel) {
        productDao.insertKM(kmModel)
    }

    override suspend fun updateKM(kmModel: KMModel) {
        productDao.insertKM(kmModel)
    }

    override suspend fun failedServerKmList(taskId: Int?): List<String?> {
        return productDao.failedServerKMList(taskId)
    }
}