package uz.duol.sizscanner.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "GTIN")
data class GtinEntity(
    @PrimaryKey
    val id: Int? = null,
    val gtin: String? = null,
    @ColumnInfo(name = "product_name")
    val productName: String? = null,
    @ColumnInfo(name = "total_km")
    val totalKM: Int? = null,
    @ColumnInfo(name = "waiting_km")
    val waitingKM: Int? = 0,
    @ColumnInfo(name = "sold_km")
    val soldKm: Int? = null,
    @ColumnInfo(name = "condition_status")
    val conditionStatus: String? = null,
    @ColumnInfo(name = "task_id")
    val taskId: Int? = null
)
