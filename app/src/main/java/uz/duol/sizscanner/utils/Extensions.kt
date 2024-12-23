package uz.duol.sizscanner.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.internal.findRootView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import uz.duol.sizscanner.R
import java.text.SimpleDateFormat
import java.util.Date

fun View.isEnabled(isEnable:Boolean){
    isEnabled = isEnable
}

fun View.disable(){
    isEnabled = false
}

fun View.enable(){
    isEnabled = true
}

fun Fragment.systemVibrate(){
    val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    }else {
        vibrator.vibrate(200)
    }
}

fun Fragment.snackBar(message:String){
    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
}

fun View.visible(){
    visibility = View.VISIBLE
}

fun View.gone(){
    visibility = View.GONE
}

@SuppressLint("SimpleDateFormat")
fun dateFormat(date: Date?): String {
    return if (date != null) {
        val simple = SimpleDateFormat("dd.MM.yyyy")
        simple.format(date)
    } else {
        ""
    }
}

fun Fragment.backPressDispatcher() {
    val callBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            requireActivity().supportFragmentManager.popBackStack(
                "Screen",
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            requireActivity().finish()
        }
    }
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callBack)
}

@SuppressLint("RestrictedApi", "MissingInflatedId")
fun Fragment.showCustomSnackbar(snackIcon: Int, snackText: String) {
    val snackbar = Snackbar.make(requireView(), "", Snackbar.LENGTH_SHORT)
    val layoutInflater = layoutInflater.inflate(R.layout.result_snack_bar, null)
    snackbar.view.setBackgroundColor(Color.TRANSPARENT)
    val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
    snackbarLayout.setPadding(0, 0, 0, 0)
    val layoutBack = layoutInflater.findViewById<ConstraintLayout>(R.id.result_snack_layout)
    val icon = layoutInflater.findViewById<ImageView>(R.id.snackIcon)
    val text = layoutInflater.findViewById<MaterialTextView>(R.id.snackText)
    if (snackIcon == R.drawable.error_icon){
        layoutBack.setBackgroundResource(R.drawable.snack_error_back)
    }else {
        layoutBack.setBackgroundResource(R.drawable.snack_success_back)
    }
    icon.setBackgroundResource(snackIcon)
    text.text = snackText
    snackbarLayout.addView(layoutInflater, 0)
    snackbar.show()
}


