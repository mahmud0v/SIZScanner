package uz.duol.sizscanner.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class KMModel(
    @PrimaryKey
    val km:String,
    @ColumnInfo(name = "task_id")
    val taskId:Int? = null,
    @ColumnInfo(name = "km_status_server")
    val kmStatusServer:String? = null,
    val gtin:String? = null
)
