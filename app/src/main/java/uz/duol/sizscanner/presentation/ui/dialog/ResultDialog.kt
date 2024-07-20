package uz.duol.sizscanner.presentation.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.duol.sizscanner.R
import uz.duol.sizscanner.databinding.ResultDialogScreenBinding
class ResultDialog(private val errorMessage:String?, private val dialogLogo:Int) : DialogFragment() {
    private val binding by viewBinding(ResultDialogScreenBinding::bind)
    var negativeBtnClick: (() -> Unit)? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.result_dialog_screen, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_back)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.dialogLogo.setBackgroundResource(dialogLogo)

        if (errorMessage != null){
            binding.dialogText.text = errorMessage
        }else {
            binding.dialogText.text = getString(R.string.unknown_error)
        }

    }






}