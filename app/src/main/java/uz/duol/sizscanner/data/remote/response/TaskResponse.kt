package uz.duol.sizscanner.data.remote.response

import java.io.Serializable


data class TaskResponse(
    val id: Int? = null,
    val dateStr: String? = null,
    val taskName: String? = null,
    val countKM:Int? = null,
    val conditionStatus:String? = null
): Serializable
