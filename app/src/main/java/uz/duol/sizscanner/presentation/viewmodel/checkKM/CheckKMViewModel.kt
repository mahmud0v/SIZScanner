package uz.duol.sizscanner.presentation.viewmodel.checkKM

import androidx.lifecycle.LiveData

interface CheckKMViewModel {
    val errorMessageLiveData:LiveData<String>
    val successCheckKMLiveData:LiveData<Boolean?>


    fun checkKMFromServer(km:String?)
}