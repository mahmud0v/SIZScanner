package uz.duol.sizscanner.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class KMModel(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val km:String? = null,
    @ColumnInfo(name = "task_id")
    val taskId:Int? = null,
    @ColumnInfo(name = "km_status_server")
    val kmStatusServer:String? = null
)
