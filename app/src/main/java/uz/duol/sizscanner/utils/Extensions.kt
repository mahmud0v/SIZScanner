package uz.duol.sizscanner.utils

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.Visibility
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.internal.findRootView
import com.google.android.material.snackbar.Snackbar
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
