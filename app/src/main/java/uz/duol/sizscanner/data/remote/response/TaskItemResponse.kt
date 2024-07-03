package uz.duol.sizscanner.data.remote.response

data class TaskItemResponse(
    val id: Int? = null,
    val gtin: String? = null,
    val productName: String? = null,
    val totalKM: Int? = null,
    val soldKM: Int? = null,
    var waitingKm:Int? = 0,
    val conditionStatus: String? = null,
    var existDB:Int? = null
)
