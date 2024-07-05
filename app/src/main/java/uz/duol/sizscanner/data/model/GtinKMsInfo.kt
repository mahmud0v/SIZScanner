package uz.duol.sizscanner.data.model

data class WaitingGtinInfo(
    val totalKM:Int? = null,
    val waitingGtinCount:Int? = null,
    val differenceKM:Int? = null,
    val gtin:String? = null,
    val taskId:Int? = null,
    var kmModelCountKM:Int? = null
)
