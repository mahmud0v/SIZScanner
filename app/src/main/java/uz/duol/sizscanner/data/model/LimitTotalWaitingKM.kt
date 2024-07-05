package uz.duol.sizscanner.data.model

import androidx.room.ColumnInfo

data class LimitTotalWaitingKM(
    @ColumnInfo(name = "total_km")
    val totalKM: Int? = null,
    @ColumnInfo(name = "difference_km")
    val differenceKM: Int? = null,
    @ColumnInfo(name = "waiting_km")
    val waitingKM: Int? = null
)
