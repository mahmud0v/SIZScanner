package uz.duol.sizscanner.data.remote.response

data class ApiResponse<T>(
    val success: Boolean? = null,
    val msg: String? = null,
    val status: Int? = null,
    val obj: T? = null,
    val hasNewToken: Boolean? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null
)
