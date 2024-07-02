package uz.duol.sizscanner.data.repository.db

import uz.duol.sizscanner.data.database.entity.KMModel

interface AppDatabaseRepository {

    suspend fun insertKM(kmModel: KMModel)

    suspend fun updateKM(kmModel: KMModel)

    suspend fun failedServerKmList(taskId:Int?):List<String?>
}