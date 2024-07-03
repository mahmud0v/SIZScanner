package uz.duol.sizscanner.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.duol.sizscanner.data.database.entity.GtinEntity
import uz.duol.sizscanner.data.model.LimitTotalWaitingKM

@Dao
interface GtinDao {

    @Query("SELECT  (total_km-sold_km) as difference_km, waiting_km FROM GTIN WHERE gtin=:gtin AND task_id =:taskId")
    suspend fun getWaitingKMCount(gtin: String?, taskId: Int?): LimitTotalWaitingKM?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGtin(gtinEntity: GtinEntity)

    @Query("SELECT EXISTS(SELECT 1 from GTIN WHERE gtin =:gtin AND task_id =:taskId)")
    suspend fun existsGtin(gtin: String?, taskId: Int?): Int

    @Query("UPDATE GTIN SET total_km = :totalKM, sold_km =:sold WHERE id=:id")
    suspend fun editGtinTotalSoldKM(id: Int?, totalKM: Int?, sold: Int?)

    @Query("SELECT * FROM GTIN WHERE task_id=:taskId")
    suspend fun getAllGtinDB(taskId: Int?): List<GtinEntity>

    @Query("UPDATE GTIN SET waiting_km =:waitingKM WHERE gtin =:gtin AND task_id =:taskId")
    suspend fun editWaitingKM(waitingKM: Int?, gtin: String?, taskId: Int?):Int

    @Query("SELECT EXISTS(SELECT 1 FROM KMModel WHERE km =:km)")
    suspend fun existsKM(km:String?): Int




}