package uz.duol.sizscanner.data.remote.request

data class LoginRequest(
    val pincode:String? = null,
    val deviceId:String? = null,
    val fcmToken:String? = null
)
