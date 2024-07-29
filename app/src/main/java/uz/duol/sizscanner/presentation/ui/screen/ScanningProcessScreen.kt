package uz.duol.sizscanner.presentation.ui.screen

import android.annotation.SuppressLint
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import device.common.DecodeResult
import device.common.DecodeStateCallback
import device.common.ScanConst
import device.sdk.ScanManager
import uz.duol.sizscanner.R
import uz.duol.sizscanner.data.remote.response.TaskItemResponse
import uz.duol.sizscanner.databinding.ScanningProcessScreenBinding
import uz.duol.sizscanner.presentation.ui.adapter.TaskItemListAdapter
import uz.duol.sizscanner.presentation.viewmodel.task.TaskItemListViewModel
import uz.duol.sizscanner.presentation.viewmodel.task.TaskItemListViewModelImpl
import uz.duol.sizscanner.utils.TaskStatus
import uz.duol.sizscanner.utils.gone
import uz.duol.sizscanner.utils.showCustomSnackbar
import uz.duol.sizscanner.utils.snackBar
import uz.duol.sizscanner.utils.visible


@AndroidEntryPoint
class ScanningProcessScreen : Fragment(R.layout.scanning_process_screen), LifecycleOwner {
    private val binding by viewBinding(ScanningProcessScreenBinding::bind)
    private val viewModel: TaskItemListViewModel by viewModels<TaskItemListViewModelImpl>()
    private var mDecodeResult: DecodeResult? = null
    private var mScanner: ScanManager? = null
    private val mHandler by lazy { Handler() }
    private var mScanResultReceiver: ScanResultReceiver? = null
    private var mStateCallback: DecodeStateCallback? = null
    private var mWaitDialog: ProgressDialog? = null
    private var mDialog: AlertDialog? = null
    private var mBackupResultType = ScanConst.ResultType.DCD_RESULT_COPYPASTE
    private val taskItemListAdapter by lazy { TaskItemListAdapter() }
    private val navArg: ScanningProcessScreenArgs by navArgs()
    private val taskItemList = ArrayList<TaskItemResponse>()
    private var page = 0
    private var pageSize = 10
    private var maxPage: Int = 1
    private var start = false


    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        binding.taskIdNumber.text = "#${navArg.taskInfo.id}"
        binding.date.text = navArg.taskInfo.dateStr?.substring(0, 10) ?: ""

        binding.rvTaskItem.adapter = taskItemListAdapter
        binding.rvTaskItem.layoutManager = LinearLayoutManager(requireContext())

        page = 0
        taskItemList.clear()
        viewModel.taskItemList(navArg.taskInfo.id, page++, pageSize)
        Log.d("LLLL", "onViewCreated: true")

        taskItemListAdapter.setLoader {

            if (page < maxPage){
                Log.d("LLLL", "setLoader: true")
                viewModel.taskItemList(navArg.taskInfo.id, page++, pageSize)

            }

        }

        binding.swipeRefresh.setOnRefreshListener {
            page = 0
            taskItemList.clear()
            viewModel.taskItemList(navArg.taskInfo.id, page++, pageSize)

        }

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

        binding.beginBtn.setOnClickListener {
            start = !start
            if (start) {
                binding.beginBtn.text = getString(R.string.done)
                binding.beginBtn.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red_dark_color)
                binding.beginBtn.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red_dark_color)
            } else {
                viewModel.checkTaskStatus(navArg.taskInfo.id)
            }
        }





        viewModel.errorCheckKMLiveData.observe(viewLifecycleOwner, errorCheckKMObserver)
        viewModel.successCheckKMLiveData.observe(
            viewLifecycleOwner,
            successCheckKMLiveDataObserver
        )
        viewModel.taskItemListLiveData.observe(viewLifecycleOwner, taskItemListObserver)
        viewModel.pageSizeLiveData.observe(viewLifecycleOwner, pageSizeObserver)
        viewModel.errorMessageLiveData.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.progressLoadingLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.taskStatusLiveData.observe(viewLifecycleOwner, taskStatusObserver)
        viewModel.taskMainStatusLiveData.observe(viewLifecycleOwner, taskMainStatusObserver)
        viewModel.errorTaskMainStatusLiveData.observe(
            viewLifecycleOwner,
            errorTaskMainStatusObserver
        )


    }


    private val errorTaskMainStatusObserver = Observer<String> {
        if (it == getString(R.string.unauthorised)) {
            findNavController().navigate(R.id.PINCodeScreen)
        } else {
            findNavController().popBackStack()
        }
    }


    @SuppressLint("RestrictedApi")
    private val successCheckKMLiveDataObserver = Observer<Unit> {
        showCustomSnackbar(R.drawable.success_icon, getString(R.string.success_scanned))
        page = 0
        taskItemList.clear()
        viewModel.taskItemList(navArg.taskInfo.id, page++, pageSize)
    }


    private val taskMainStatusObserver = Observer<Boolean?> {
        findNavController().popBackStack()

    }


    private val taskStatusObserver = Observer<String?> {
        when (it) {

            TaskStatus.NEW.name -> {
                binding.taskStatus.visible()
                binding.taskStatus.setBackgroundResource(R.drawable.new_status_back)
                binding.taskStatus.text = binding.root.context.getString(R.string.new_status)
                binding.taskStatus.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.new_status_text
                    )
                )

            }

            TaskStatus.PROCESS.name -> {
                binding.taskStatus.visible()
                binding.taskStatus.setBackgroundResource(R.drawable.process_status_back)
                binding.taskStatus.setText(R.string.process)
                binding.taskStatus.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.process_status_text
                    )
                )
            }

            TaskStatus.CLOSED.name -> {
                binding.taskStatus.visible()
                binding.taskStatus.setBackgroundResource(R.drawable.closed_status_back)
                binding.taskStatus.setText(R.string.closed)
                binding.taskStatus.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.closed_status_text
                    )
                )
            }

            else -> {
                binding.taskStatus.gone()
            }

        }
    }


    @SuppressLint("RestrictedApi", "NotifyDataSetChanged")
    private val errorCheckKMObserver = Observer<String> {
        binding.swipeRefresh.isRefreshing = false
        if (taskItemList.isEmpty()) {
            taskItemListAdapter.notifyDataSetChanged()
            binding.llEmptyBackground.visible()
        } else {
            binding.llEmptyBackground.gone()
        }
        showCustomSnackbar(R.drawable.error_icon, it)


    }

    @SuppressLint("NotifyDataSetChanged")
    private val taskItemListObserver = Observer<List<TaskItemResponse>?> {
        binding.swipeRefresh.isRefreshing = false
        if (it != null){
            taskItemList.addAll(it)
            taskItemListAdapter.differ.submitList(taskItemList)
            taskItemListAdapter.notifyDataSetChanged()
            Log.d("LLLL", "size: ${ taskItemListAdapter.differ.currentList.size} ")
            if (taskItemList.isNotEmpty()) {
                binding.llEmptyBackground.gone()
            } else {
                binding.llEmptyBackground.visible()
            }
        }
    }


    private val pageSizeObserver = Observer<Int> {
        maxPage = it
    }

    @SuppressLint("NotifyDataSetChanged", "RestrictedApi")
    private val errorMessageObserver = Observer<String> {
        binding.swipeRefresh.isRefreshing = false
        if (taskItemList.isEmpty()) {
            taskItemListAdapter.notifyDataSetChanged()
            binding.llEmptyBackground.visible()
        } else {
            binding.llEmptyBackground.gone()
        }
        if (it == getString(R.string.unauthorised)) {
            findNavController().navigate(R.id.PINCodeScreen)
        }
        snackBar(it)

    }

    private val progressObserver = Observer<Boolean> {
        if (it) {
            binding.progress.visible()
        } else {
            binding.progress.gone()
        }
    }


    inner class ScanResultReceiver : BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onReceive(context: Context, intent: Intent) {
            if (mScanner != null) {
                try {
                    if (ScanConst.INTENT_USERMSG == intent.action) {
                        mScanner!!.aDecodeGetResult(mDecodeResult!!.recycle())
                        if (getString(R.string.read_fail) != mDecodeResult.toString() && start) {
                            val dataMatrix =
                                mDecodeResult.toString().replace(29.toChar().toString(), "\u001D")
                            viewModel.checkKMFromServer(dataMatrix, navArg.taskInfo.id)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    private fun initScanner() {
        if (mScanner != null) {
            mScanner!!.aRegisterDecodeStateCallback(mStateCallback)
            mBackupResultType = mScanner!!.aDecodeGetResultType()
            mScanner!!.aDecodeSetResultType(ScanConst.ResultType.DCD_RESULT_USERMSG)
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
            dialog.setMessage(getString(R.string.enable_scanner_dialog))

            dialog.setButton(
                AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel)
            ) { _, _ -> requireActivity().finish() }
            dialog.setButton(
                AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok)
            ) { _, _ ->
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


}