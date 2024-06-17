package uz.duol.sizscanner.presentation.ui

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import device.common.DecodeResult
import device.common.DecodeStateCallback
import device.common.ScanConst
import device.sdk.ScanManager
import uz.duol.sizscanner.R
import uz.duol.sizscanner.databinding.ScannerScreenBinding

@AndroidEntryPoint
class ScannerScreen : Fragment(R.layout.scanner_screen) {
    private val binding by viewBinding(ScannerScreenBinding::bind)
    private var mDecodeResult: DecodeResult? = null
    private var mScanner: ScanManager? = null
    private val mHandler by lazy { Handler() }
    private var mScanResultReceiver: ScanResultReceiver? = null
    private var mStateCallback: DecodeStateCallback? = null
    private var mWaitDialog: ProgressDialog? = null
    private var mDialog: AlertDialog? = null
    private var mKeyLock = false
    private var mBackupResultType = ScanConst.ResultType.DCD_RESULT_COPYPASTE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        mScanner = ScanManager()
        mDecodeResult = DecodeResult()
        mScanResultReceiver = ScanResultReceiver()

        if (!isPMModel()) {
            return
        }


        mStateCallback = object : DecodeStateCallback(mHandler) {
            override fun onChangedState(state: Int) {
                when (state) {
                    ScanConst.STATE_ON, ScanConst.STATE_TURNING_ON -> if (getEnableDialog()!!.isShowing) {
                        getEnableDialog()!!.dismiss()
                    }

                    ScanConst.STATE_OFF, ScanConst.STATE_TURNING_OFF -> if (!getEnableDialog()!!.isShowing) {
                        getEnableDialog()!!.show()
                    }
                }
            }
        }



        binding.checkAutoscan.setOnCheckedChangeListener { buttonView, isChecked ->
            if (mScanner != null) {
                if (isChecked) {
                    mScanner!!.aDecodeSetTriggerMode(ScanConst.TriggerMode.DCD_TRIGGER_MODE_AUTO)
                } else {
                    mScanner!!.aDecodeSetTriggerMode(ScanConst.TriggerMode.DCD_TRIGGER_MODE_ONESHOT)
                }
            }
        }

        binding.checkEvent.setOnCheckedChangeListener { buttonView, isChecked ->
            if (mScanner != null) {
                if (isChecked) {
                    mScanner!!.aDecodeSetResultType(ScanConst.ResultType.DCD_RESULT_EVENT)
                } else {
                    mScanner!!.aDecodeSetResultType(ScanConst.ResultType.DCD_RESULT_USERMSG)
                }
            }
        }

        binding.checkBeep.setOnCheckedChangeListener { buttonView, isChecked ->
            if (mScanner != null) {
                if (isChecked) {
                    mScanner!!.aDecodeSetBeepEnable(1)
                } else {
                    mScanner!!.aDecodeSetBeepEnable(0)
                }
            }
        }


        binding.buttonScanOn.setOnClickListener {
            if (mScanner != null) {
                mScanner!!.aDecodeSetTriggerOn(1)
            }
        }


        binding.buttonScanOff.setOnClickListener {
            if (mScanner != null) {
                if (binding.checkAutoscan.isChecked) {
                    mScanner!!.aDecodeSetTriggerMode(ScanConst.TriggerMode.DCD_TRIGGER_MODE_ONESHOT)
                }

                mScanner!!.aDecodeSetTriggerOn(0)

                if (binding.checkAutoscan.isChecked) {
                    mScanner!!.aDecodeSetTriggerMode(ScanConst.TriggerMode.DCD_TRIGGER_MODE_AUTO)
                }
            }
        }


        binding.buttonEnalbeUpc.setOnClickListener {
            if (mScanner != null) {
                mScanner!!.aDecodeSymSetEnable(ScanConst.SymbologyID.DCD_SYM_UPCA, 1)
            }
        }


        binding.buttonDisalbeUpc.setOnClickListener {
            if (mScanner != null) {
                mScanner!!.aDecodeSymSetEnable(ScanConst.SymbologyID.DCD_SYM_UPCA, 0)
            }
        }

        binding.buttonPropEnable.setOnClickListener {
            if (mScanner != null) {
                val symID = ScanConst.SymbologyID.DCD_SYM_UPCA
                val propCnt = mScanner!!.aDecodeSymGetLocalPropCount(symID)
                var propIndex = 0

                for (i in 0 until propCnt) {
                    val propName = mScanner!!.aDecodeSymGetLocalPropName(symID, i)
                    if (propName == "Send Check Character") {
                        propIndex = i
                        break
                    }
                }

                if (mKeyLock == false) {
                    binding.buttonPropEnable.setText(R.string.property_enable)
                    mKeyLock = true
                    mScanner!!.aDecodeSymSetLocalPropEnable(symID, propIndex, 0)
                } else {
                    binding.buttonPropEnable.setText(R.string.property_disable)
                    mKeyLock = false
                    mScanner!!.aDecodeSymSetLocalPropEnable(symID, propIndex, 1)
                }
            }
        }
    }


    private fun initScanner() {
        if (mScanner != null) {
            mScanner!!.aRegisterDecodeStateCallback(mStateCallback)
            mBackupResultType = mScanner!!.aDecodeGetResultType()
            mScanner!!.aDecodeSetResultType(ScanConst.ResultType.DCD_RESULT_USERMSG)
            binding.checkEvent.isChecked = false
            if (mScanner!!.aDecodeGetTriggerMode() == ScanConst.TriggerMode.DCD_TRIGGER_MODE_AUTO) {
                binding.checkAutoscan.isChecked = true
            } else {
                binding.checkAutoscan.isChecked = false
            }
            if (mScanner!!.aDecodeGetBeepEnable() == 1) {
                binding.checkBeep.isChecked = true
            } else {
                binding.checkBeep.isChecked = false
            }
        }
    }

    private val mStartOnResume = Runnable {
        requireActivity().runOnUiThread {
            initScanner()
            if (mWaitDialog != null && mWaitDialog!!.isShowing) {
                mWaitDialog?.dismiss()
            }
        }
    }

    private fun getEnableDialog(): AlertDialog? {
        if (mDialog == null) {
            val dialog = AlertDialog.Builder(requireContext()).create()
            dialog.setTitle(R.string.app_name)
            dialog.setMessage("Your scanner is disabled. Do you want to enable it?")

            dialog.setButton(
                AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel)
            ) { dialog, which -> requireActivity().finish() }
            dialog.setButton(
                AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok)
            ) { dialog, which ->
                val intent = Intent(ScanConst.LAUNCH_SCAN_SETTING_ACITON)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                dialog.dismiss()
            }
            dialog.setCancelable(false)
            mDialog = dialog
        }
        return mDialog
    }

    override fun onResume() {
        super.onResume()

        if (!isPMModel()) {
            return
        }

        mWaitDialog = ProgressDialog.show(requireContext(), "", getString(R.string.msg_wait), true)
        mHandler.postDelayed(mStartOnResume, 1000)
        val filter = IntentFilter()
        filter.addAction(ScanConst.INTENT_USERMSG)
        filter.addAction(ScanConst.INTENT_EVENT)
        requireContext().registerReceiver(mScanResultReceiver, filter)
    }

    override fun onPause() {
        if (!isPMModel()) {
            super.onPause()
            return
        }
        if (mScanner != null) {
            mScanner!!.aDecodeSetResultType(mBackupResultType)
            mScanner!!.aUnregisterDecodeStateCallback(mStateCallback)
        }
        requireContext().unregisterReceiver(mScanResultReceiver)
        super.onPause()
    }

    override fun onDestroy() {
        if (mScanner != null) {
            mScanner!!.aDecodeSetResultType(mBackupResultType)
        }
        mScanner = null
        super.onDestroy()
    }

    private fun isPMModel(): Boolean {
        return if (Build.MODEL.startsWith(("PM"))) true
        else false
    }

    inner class ScanResultReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("TTT", "onReceive: Log working")

            if (mScanner != null) {
                try {
                    if (ScanConst.INTENT_USERMSG == intent.action) {
                        mScanner!!.aDecodeGetResult(mDecodeResult!!.recycle())
                        binding.textviewBarType.text = mDecodeResult!!.symName
                        binding.textviewScanResult.text = mDecodeResult.toString()
                        Log.d("TTT", "decodeResult: ${mDecodeResult.toString()}")

                    } else if (ScanConst.INTENT_EVENT == intent.action) {
                        val result =
                            intent.getBooleanExtra(ScanConst.EXTRA_EVENT_DECODE_RESULT, false)
                        val decodeBytesLength =
                            intent.getIntExtra(ScanConst.EXTRA_EVENT_DECODE_LENGTH, 0)
                        val decodeBytesValue =
                            intent.getByteArrayExtra(ScanConst.EXTRA_EVENT_DECODE_VALUE)
                        val decodeValue = String(decodeBytesValue!!, 0, decodeBytesLength)
                        val decodeLength = decodeValue.length
                        val symbolName = intent.getStringExtra(ScanConst.EXTRA_EVENT_SYMBOL_NAME)
                        val symbolId =
                            intent.getByteExtra(ScanConst.EXTRA_EVENT_SYMBOL_ID, 0.toByte())
                        val symbolType = intent.getIntExtra(ScanConst.EXTRA_EVENT_SYMBOL_TYPE, 0)
                        val letter =
                            intent.getByteExtra(ScanConst.EXTRA_EVENT_DECODE_LETTER, 0.toByte())
                        val modifier =
                            intent.getByteExtra(ScanConst.EXTRA_EVENT_DECODE_MODIFIER, 0.toByte())
                        val decodingTime = intent.getIntExtra(ScanConst.EXTRA_EVENT_DECODE_TIME, 0)
                        Log.d("TTT", "1. result: $result")
                        Log.d("TTT", "2. bytes length: $decodeBytesLength")
                        Log.d("TTT", "3. bytes value: $decodeBytesValue")
                        Log.d("TTT", "4. decoding length: $decodeLength")
                        Log.d("TTT", "5. decoding value: $decodeValue")
                        Log.d("TTT", "6. symbol name: $symbolName")
                        Log.d("TTT", "7. symbol id: $symbolId")
                        Log.d("TTT", "8. symbol type: $symbolType")
                        Log.d("TTT", "9. decoding letter: $letter")
                        Log.d("TTT", "10.decoding modifier: $modifier")
                        Log.d("TTT", "11.decoding time: $decodingTime")
                        binding.textviewBarType.text = symbolName
                        binding.textviewScanResult.text = decodeValue
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


}