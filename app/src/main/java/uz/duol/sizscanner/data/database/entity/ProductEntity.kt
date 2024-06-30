package uz.duol.sizscanner.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ProductEntity(
    @PrimaryKey
    val id:Int? = null,
    val productName:String? = null,
    val productType:String? = null,
    val productMark:String? = null,
    val invoiceId:Int? = null,
    val scannedStatus:String? = null
)
