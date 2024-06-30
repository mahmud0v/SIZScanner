package uz.duol.sizscanner.data.model

import java.io.Serializable

data class TaskInfo(
    val id:Int,
    val title:String,
    val def:String,
    val date:String,
    val qty:Int
):Serializable