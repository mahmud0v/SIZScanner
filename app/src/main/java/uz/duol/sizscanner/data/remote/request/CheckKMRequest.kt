package uz.duol.sizscanner.data.remote.request

data class CheckKMRequest(
    val kmList:List<String?>? = null,
    val transactionId:Int? = null
)
