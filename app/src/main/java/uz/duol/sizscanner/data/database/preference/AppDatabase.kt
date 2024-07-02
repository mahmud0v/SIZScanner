package uz.duol.sizscanner.data.database.preference

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.duol.sizscanner.data.database.dao.ProductDao
import uz.duol.sizscanner.data.database.entity.KMModel

@Database(
    entities = [KMModel::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getProductDao(): ProductDao

}