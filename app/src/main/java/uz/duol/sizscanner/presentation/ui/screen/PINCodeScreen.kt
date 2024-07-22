package uz.duol.sizscanner.presentation.ui.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings.Secure
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import uz.duol.sizscanner.R
import uz.duol.sizscanner.data.remote.response.CheckPinResponse
import uz.duol.sizscanner.databinding.PinCodeScreenBinding
import uz.duol.sizscanner.presentation.viewmodel.pin.CheckPinViewModel
import uz.duol.sizscanner.presentation.viewmodel.pin.CheckPinViewModelImpl
import uz.duol.sizscanner.utils.backPressDispatcher
import uz.duol.sizscanner.utils.disable
import uz.duol.sizscanner.utils.enable
import uz.duol.sizscanner.utils.gone
import uz.duol.sizscanner.utils.isEnabled
import uz.duol.sizscanner.utils.snackBar
import uz.duol.sizscanner.utils.systemVibrate

@AndroidEntryPoint
class PINCodeScreen : Fragment(R.layout.pin_code_screen) {
    private val binding by viewBinding(PinCodeScreenBinding::bind)
    private val viewModel: CheckPinViewModel by viewModels<CheckPinViewModelImpl>()
    private var fcmToken:String? = null
    private var pinText = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getFCMToken()
        clickNumber()
        clickEraseBtn()
        backPressDispatcher()

        viewModel.checkPinLiveData.observe(viewLifecycleOwner, checkPinObserver)
        viewModel.progressLoadingLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.errorMessageLiveData.observe(viewLifecycleOwner, errorMessageObserver)
    }

    private val checkPinObserver = Observer<CheckPinResponse?>{
        findNavController().navigate(PINCodeScreenDirections.actionPINCodeScreenToHomeScreen(it))
    }

    private val progressObserver = Observer<Boolean>{
        if (it){
            binding.eraseBtn.disable()
            binding.progress.visibility = View.VISIBLE

        }else {
            binding.eraseBtn.enable()
            binding.progress.visibility = View.GONE

        }
        isEnableNumberText(false)

    }

    private val errorMessageObserver = Observer<String>{
        pinText = ""
        snackBar(it)
        val anim = AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.shake
        )
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                allDotDefault()
                isEnableNumberText(true)
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

        })
        binding.llPinDot.startAnimation(anim)
        systemVibrate()
    }


    private fun clickNumber() {
        for (i in 0 until binding.clKeyboard.size - 1) {
            val numberText = binding.clKeyboard[i] as MaterialTextView
            numberText.setOnClickListener {
                dotFill()
                pinText += numberText.text.toString()
                checkAdd()
            }
        }
    }

    private fun clickEraseBtn() {
        binding.eraseBtn.setOnClickListener {
            checkRemove()
        }
    }


    private fun dotFill() {
        val dot = binding.llPinDot[pinText.length] as ImageView
        dot.setImageResource(R.drawable.pin_fill_dot_item)
    }

    private fun dotDefault() {
        val dot = binding.llPinDot[pinText.length] as ImageView
        dot.setImageResource(R.drawable.pin_dot_item)
    }

    @SuppressLint("HardwareIds")
    private fun checkAdd() {
        if (pinText.length == 6) {
            val deviceId = Secure.getString(requireContext().contentResolver, Secure.ANDROID_ID)
            Log.d("DDDD", "deviceId: $deviceId")
            viewModel.checkPin(pinText, deviceId, fcmToken)
            binding.eraseBtn.disable()
        }else {
            binding.eraseBtn.enable()
        }

    }

    private fun checkRemove() {
        if (pinText.length == 5){
            binding.progress.gone()
            isEnableNumberText(true)
        }
        if (pinText.isBlank()) {
            binding.eraseBtn.disable()
        } else if (pinText.length == 1) {
            pinText = pinText.substring(0, pinText.length - 1)
            binding.eraseBtn.disable()
            dotDefault()
        } else {
            binding.eraseBtn.enable()
            pinText = pinText.substring(0, pinText.length - 1)
            dotDefault()
        }

    }


    private fun isEnableNumberText(bool: Boolean) {
        for (i in 0 until binding.clKeyboard.size - 1) {
            binding.clKeyboard[i].isEnabled(bool)
        }
    }

    private fun allDotDefault(){
        for (i in binding.llPinDot.size-1 downTo 0){
            val imageView = binding.llPinDot[i] as ImageView
            imageView.setImageResource(R.drawable.pin_dot_item)
        }
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("DDDD", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            fcmToken = task.result
            Log.d("DDDD", "getFCMToken: $fcmToken")
        })
    }


}