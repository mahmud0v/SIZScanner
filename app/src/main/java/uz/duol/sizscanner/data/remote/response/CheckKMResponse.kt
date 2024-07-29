package uz.duol.sizscanner.data.remote.response

data class CheckKMResponse(
    val rows:List<String?>? = null,
    val total:Int? = null
)

data class KMSoldInfo(
    val kmsNotSold:List<String?>? = null,
    val kmsSold:List<String?>? = null
)
