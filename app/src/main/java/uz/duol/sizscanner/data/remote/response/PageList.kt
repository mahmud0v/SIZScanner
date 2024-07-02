package uz.duol.sizscanner.data.remote.response

data class PageList<T>(
    val total: Int? = null,
    val rows: T? = null,
    val taskStatus:String? = null
)
