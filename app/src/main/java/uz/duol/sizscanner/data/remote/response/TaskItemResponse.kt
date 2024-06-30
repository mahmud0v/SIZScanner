package uz.duol.sizscanner.data.remote.response

data class TaskItemResponse(
    val id: Int? = null,
    val gtin: String? = null,
    val productName: String? = null,
    val totalKM: Int? = null,
    val soldKM: Int? = null
)
