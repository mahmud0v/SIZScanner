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
import uz.duol.sizscanner.data.database.entity.GtinEntity
import uz.duol.sizscanner.data.database.entity.KMModel
import uz.duol.sizscanner.data.model.ExistsKMInfo
import uz.duol.sizscanner.data.model.InsertKMInfo
import uz.duol.sizscanner.data.remote.response.CheckKMResponse
import uz.duol.sizscanner.data.remote.response.TaskItemResponse
import uz.duol.sizscanner.databinding.ScanningProcessScreenBinding
import uz.duol.sizscanner.presentation.ui.adapter.TaskItemListAdapter
import uz.duol.sizscanner.presentation.viewmodel.checkKM.CheckKMViewModel
import uz.duol.sizscanner.presentation.viewmodel.checkKM.CheckKMViewModelImpl
import uz.duol.sizscanner.presentation.viewmodel.task.TaskItemListViewModel
import uz.duol.sizscanner.presentation.viewmodel.task.TaskItemListViewModelImpl
import uz.duol.sizscanner.utils.KMStatusServer
import uz.duol.sizscanner.utils.MarkingProductStatus
import uz.duol.sizscanner.utils.TaskStatus
import uz.duol.sizscanner.utils.gone
import uz.duol.sizscanner.utils.snackBar
import uz.duol.sizscanner.utils.visible


@AndroidEntryPoint
class ScanningProcessScreen : Fragment(R.layout.scanning_process_screen) {
    private val binding by viewBinding(ScanningProcessScreenBinding::bind)
    private val viewModel: TaskItemListViewModel by viewModels<TaskItemListViewModelImpl>()
    private val viewModel2: CheckKMViewModel by viewModels<CheckKMViewModelImpl>()
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
    private var taskListLastItemListener: (() -> Unit)? = null
    private val kmList = ArrayList<String?>()
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

        taskItemListAdapter.setLoader {
            if (page < maxPage)
                viewModel.taskItemList(navArg.taskInfo.id, page++, pageSize)

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

        taskListLastItemListener = {
            viewModel.getAllGtinDB(navArg.taskInfo.id)
        }


        viewModel2.successCheckKMLiveData.observe(viewLifecycleOwner, successCheckKMObserver)
        viewModel2.errorMessageLiveData.observe(viewLifecycleOwner, errorMessageObserver2)

        viewModel.taskItemListLiveData.observe(viewLifecycleOwner, taskItemListObserver)
        viewModel.pageSizeLiveData.observe(viewLifecycleOwner, pageSizeObserver)
        viewModel.errorMessageLiveData.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.progressLoadingLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.taskStatusLiveData.observe(viewLifecycleOwner, taskStatusObserver)
        viewModel.failedServerKMListLiveData.observe(viewLifecycleOwner, failedServerKMListObserver)
        viewModel.taskMainStatusLiveData.observe(viewLifecycleOwner, taskMainStatusObserver)
        viewModel.errorMessageFailedServerKMListLiveData.observe(
            viewLifecycleOwner,
            errorMessageFailedServerKMListObserver
        )
        viewModel.editWaitingKMLiveData.observe(viewLifecycleOwner, editWaitingKMObserver)
        viewModel.getAllGtinDBLiveData.observe(viewLifecycleOwner, getAllGtinDBObserver)
        viewModel.addWaitingKMSaveDB.observe(viewLifecycleOwner, addWaitingKMSaveDBObserver)
        viewModel.existKMLiveData.observe(viewLifecycleOwner, existKMLiveDataObserver)

        viewModel2.getWaitingKMCountLiveData.observe(
            viewLifecycleOwner,
            Observer {
                if (it.waitingGtinCount != null && it.differenceKM !=null){
                    if (it.differenceKM > it.waitingGtinCount){
                        val dbKm = it.waitingGtinCount+1
                        viewModel.editWaitingKM(
                            waitingKM = dbKm,
                            it.gtin,
                            it.taskId
                        )
                    } else {
                        snackBar(getString(R.string.limit_scanner_km, it.gtin))
                    }
                } else {
                    snackBar(getString(R.string.unknown_error))
                }

            })

        viewModel.existGtinLiveData.observe(viewLifecycleOwner, Observer {
            if (it?.existDB == 1) {
//                Log.d("RRRR", "$it")
                viewModel.editGtinTotalSoldKM(it.id, it.totalKM, it.soldKM)
            } else {
//                Log.d("RRRR", "$it")
                viewModel2.insertGtin(
                    GtinEntity(
                        id = it?.id,
                        gtin = it?.gtin,
                        productName = it?.productName,
                        totalKM = it?.totalKM,
                        soldKm = it?.soldKM,
                        conditionStatus = it?.conditionStatus,
                        taskId = navArg.taskInfo.id
                    )
                )
            }
        })



        binding.beginBtn.setOnClickListener {
            start = !start
            if (start) {
                binding.beginBtn.text = getString(R.string.done)
                binding.beginBtn.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red_dark_color)
            } else {
                viewModel.taskStatus(navArg.taskInfo.id)
            }
        }


    }

    private val addWaitingKMSaveDBObserver = Observer<InsertKMInfo>{
        if (it.rowId != null && it.rowId > 0) {
            Log.d("YYYY", "rowId: ${it.rowId}")
            viewModel2.getWaitingKMCount(
                it.km?.substring(0, 16),
                navArg.taskInfo.id
            )
        }

    }

    private val editWaitingKMObserver = Observer<Int>{
        viewModel.getAllGtinDB(navArg.taskInfo.id)
    }

    private val existKMLiveDataObserver = Observer<ExistsKMInfo>{
        if (it.exist == 0){ // try catch
            viewModel.insertKMDB(
                KMModel(
                    km = it.km!!,
                    taskId = navArg.taskInfo.id,
                    kmStatusServer = MarkingProductStatus.SCANNED_NOT_VERIFIED.name,
                    gtin = it.km!!.substring(0, 16)
                )
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private val getAllGtinDBObserver = Observer<List<GtinEntity>> {
        taskItemList.clear()
        it.map {
            taskItemList.add(
                TaskItemResponse(
                    id = it.id,
                    gtin = it.gtin,
                    productName = it.productName,
                    totalKM = it.totalKM,
                    soldKM = it.soldKm,
                    waitingKm = it.waitingKM,
                    conditionStatus = it.conditionStatus
                )
            )
        }
        taskItemListAdapter.differ.submitList(taskItemList)
        taskItemListAdapter.notifyDataSetChanged()

        if (taskItemList.isEmpty() && taskItemListAdapter.differ.currentList.isEmpty()) {
            binding.llEmptyBackground.visible()
        } else {
            binding.llEmptyBackground.gone()
        }

    }

    private val errorMessageFailedServerKMListObserver = Observer<String> {
        findNavController().popBackStack()
    }

    private val taskMainStatusObserver = Observer<Boolean?> {

        it?.let {
            if (!it) {
                val alertDialog = AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.synchronize))
                    .setMessage(getString(R.string.ask_synchronize_failed_km))
                    .setCancelable(false)
                    .setNegativeButton(getString(R.string.no)) { _, _ -> findNavController().popBackStack() }
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        viewModel2.checkKMFromServer(
                            kmList,
                            navArg.taskInfo.id
                        )
                    }
                    .create()

                alertDialog.show()
            } else {
                findNavController().popBackStack()
            }
        }

    }

    private val failedServerKMListObserver = Observer<List<String?>?> {
        if (!it.isNullOrEmpty()) {
            viewModel.taskStatus(navArg.taskInfo.id)
            kmList.clear()
            kmList.addAll(it)
        }
    }

    private val taskStatusObserver = Observer<String?> {
        when (it) {
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

    private val successCheckKMObserver = Observer<CheckKMResponse?> {
        page = 0
        taskItemList.clear()
        viewModel.taskItemList(navArg.taskInfo.id, page++, pageSize)
        it?.let {
            it.rows?.map { km ->
                viewModel.insertKMDB(
                    KMModel(
                        km = km,
                        taskId = navArg.taskInfo.id,
                        kmStatusServer = KMStatusServer.FAILED.name
                    )
                )
            }
        }
    }

    private val errorMessageObserver2 = Observer<String> {
        kmList.map {
            try {
                viewModel.insertKMDB(
                    KMModel(
                        km = it!!,
                        taskId = navArg.taskInfo.id,
                        kmStatusServer = KMStatusServer.FAILED.name
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private val taskItemListObserver = Observer<List<TaskItemResponse>?> {
        binding.swipeRefresh.isRefreshing = false

        it?.let { taskList ->

            for (i in taskList.indices) {
                viewModel.existGtin(taskList[i].gtin, navArg.taskInfo.id, taskList[i])
                if (i == taskList.size - 1) {
                    taskListLastItemListener?.invoke()
                }

            }


        }


    }


    private val pageSizeObserver = Observer<Int> {
        maxPage = it
    }

    @SuppressLint("NotifyDataSetChanged")
    private val errorMessageObserver = Observer<String> {
        binding.swipeRefresh.isRefreshing = false
        snackBar(it)
        if (taskItemList.isEmpty()) {
            taskItemListAdapter.notifyDataSetChanged()
            binding.llEmptyBackground.visible()
        } else {
            binding.llEmptyBackground.gone()
        }
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

                            viewModel.existKM(mDecodeResult.toString())


                        }
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
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

}