package uz.duol.sizscanner.data.remote.response

import java.io.Serializable

data class CheckPinResponse(
    val firstName: String? = null,
    val lastName: String? = null,
    val middleName: String? = null,
    val pinCode: String? = null
):Serializable
