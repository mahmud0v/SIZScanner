package uz.duol.sizscanner.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import uz.duol.sizscanner.data.database.entity.KMModel

@Dao
interface ProductDao {

   @Query("SELECT km FROM KMModel WHERE km_status_server = 'FAILED' AND task_id =:taskId")
   suspend fun failedServerKMList(taskId: Int?): List<String?>

   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertKM(kmModel: KMModel):Long

   @Update
   suspend fun updateKM(updateKMModel: KMModel)

   @Query("SELECT Count(*) FROM KMModel WHERE gtin=:gtin AND task_id=:taskId")
   suspend fun allTaskGtinKM(taskId: Int?, gtin: String?):Int?

}