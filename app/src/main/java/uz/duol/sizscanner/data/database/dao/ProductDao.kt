package uz.duol.sizscanner.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import uz.duol.sizscanner.data.database.entity.KMModel

@Dao
interface ProductDao {

   @Query("SELECT km FROM KMModel WHERE km_status_server = 'SCANNED_NOT_VERIFIED' AND task_id =:taskId")
   suspend fun failedServerKMList(taskId: Int?): List<String?>

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertKM(kmModel: KMModel):Long

   @Update
   suspend fun updateKM(updateKMModel: KMModel)

   @Query("SELECT Count(*) FROM KMModel WHERE gtin=:gtin AND task_id=:taskId AND km_status_server = 'SCANNED_NOT_VERIFIED'")
   suspend fun allTaskGtinKM(taskId: Int?, gtin: String?):Int?

   @Query("UPDATE KMModel SET km_status_server='SCANNED_VERIFIED' where km=:km")
   suspend fun kmChangeStatusScannedVerified(km:String?)

   @Query("DELETE FROM KMModel WHERE km=:km")
   suspend fun deleteKM(km:String?)

   @Query("SELECT Count(*) FROM KMModel WHERE task_id=:taskId AND gtin=:gtin AND km_status_server = 'SCANNED_VERIFIED'")
   suspend fun countNotVerifiedTaskGtinKM(gtin: String?, taskId: Int?):Int?






}