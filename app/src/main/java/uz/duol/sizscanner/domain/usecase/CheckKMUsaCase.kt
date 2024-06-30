package uz.duol.sizscanner.domain.usecase

import kotlinx.coroutines.flow.Flow

interface CheckKMUsaCase {

    fun checkKMFromServer(km:String?): Flow<Result<Boolean?>>

}