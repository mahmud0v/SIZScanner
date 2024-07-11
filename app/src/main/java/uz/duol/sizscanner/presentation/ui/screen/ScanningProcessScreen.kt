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
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import device.common.DecodeResult
import device.common.DecodeStateCallback
import device.common.ScanConst
import device.sdk.ScanManager
import uz.duol.sizscanner.R
import uz.duol.sizscanner.data.database.entity.GtinEntity
import uz.duol.sizscanner.data.database.entity.KMModel
import uz.duol.sizscanner.data.model.ChangeWaitingGtinCountInfo
import uz.duol.sizscanner.data.model.ExistsKMInfo
import uz.duol.sizscanner.data.model.InsertKMInfo
import uz.duol.sizscanner.data.model.WaitingGtinInfo
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
class ScanningProcessScreen : Fragment(R.layout.scanning_process_screen), LifecycleOwner {
    private var _binding:ScanningProcessScreenBinding? = null
    private val binding get() = _binding!!
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
    private var countDownTimer: CountDownTimer? = null
    private val kmList = ArrayList<String?>()
    private var page = 0
    private var pageSize = 10
    private var maxPage: Int = 1
    private var start = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScanningProcessScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

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

        val callBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                countDownTimer?.cancel()
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callBack)


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
            val timer = object : CountDownTimer(1000L, 1000L){
                override fun onTick(p0: Long) {

                }

                override fun onFinish() {
                    viewModel.getAllGtinDB(navArg.taskInfo.id)
                }

            }.start()

        }





        viewModel2.errorMessageLiveData.observe(viewLifecycleOwner, errorMessageObserver2)
        viewModel2.successCheckKMLiveData.observe(
            viewLifecycleOwner,
            successCheckKMLiveDataObserver
        )
        viewModel2.countNotVerifiedTaskGtinKMLiveData.observe(viewLifecycleOwner, countNotVerifiedTaskGtinKMLiveData)

        viewModel.taskItemListLiveData.observe(viewLifecycleOwner, taskItemListObserver)
        viewModel.pageSizeLiveData.observe(viewLifecycleOwner, pageSizeObserver)
        viewModel.errorMessageLiveData.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.progressLoadingLiveData.observe(viewLifecycleOwner, progressObserver)
        viewModel.progressLoading2LiveData.observe(viewLifecycleOwner, progressObserver2)
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
        viewModel.errorTaskMainStatusLiveData.observe(viewLifecycleOwner, errorTaskMainStatusObserver)
        viewModel2.changeWaitingKMCountLiveData.observe(viewLifecycleOwner, changeWaitingKMCountObserver)
        viewModel2.waitingKMForInsertLiveData.observe(viewLifecycleOwner, waitingKMForInsertObserver)



        viewModel2.getWaitingKMCountLiveData.observe(
            viewLifecycleOwner,
            Observer {
                viewModel2.countNotVerifiedTaskGtinKM(it)


            })

        viewModel.existGtinLiveData.observe(viewLifecycleOwner, Observer {
            if (it?.existDB == 1) {
                viewModel.editGtinTotalSoldKM(it.id, it.totalKM, it.soldKM)
            } else {
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

        viewModel2.allTaskGtinKMLiveData.observe(viewLifecycleOwner, allTaskGtinKMLiveDataObserver)




        binding.beginBtn.setOnClickListener {
            start = !start
            if (start) {
                binding.beginBtn.text = getString(R.string.done)
                binding.beginBtn.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red_dark_color)
                // dev commit
                binding.beginBtn.backgroundTintList =
                    ContextCompat.getColorStateList(requireContext(), R.color.red_dark_color)
            } else {
                viewModel.taskStatus(navArg.taskInfo.id)
            }
        }


    }

    private val addWaitingKMSaveDBObserver = Observer<InsertKMInfo> {
        if (it.rowId != null && it.rowId > 0) {
            viewModel2.getWaitingKMCount(
                it.km?.substring(2, 16),
                navArg.taskInfo.id
            )
        }

    }

    private val waitingKMForInsertObserver = Observer<WaitingGtinInfo?> {
        try {
            if (it?.differenceKM!=0 && it?.differenceKM!! > it!!.gtinKMCountNotVerified!!){
                viewModel.insertKMDB(
                    KMModel(
                        km = it?.insertKM!!,
                        taskId = navArg.taskInfo.id,
                        kmStatusServer = MarkingProductStatus.SCANNED_NOT_VERIFIED.name,
                        gtin = it.insertKM!!.substring(2, 16)
                    )
                )
            } else {
                snackBar(getString(R.string.limit_scanner_km, it.gtin))
            }

        } catch (e: Exception) {
            snackBar(getString(R.string.unknown_error))
        }

    }

    private val allTaskGtinKMLiveDataObserver = Observer<ExistsKMInfo?>{
        viewModel2.getWaitingKMForInsert(it?.km?.substring(2, 16), navArg.taskInfo.id, it?.km, it?.gtinKMCount)
    }

    private val errorTaskMainStatusObserver = Observer<String> {
        if (it == getString(R.string.unauthorised)) {
            findNavController().navigate(R.id.PINCodeScreen)
        } else {
            findNavController().popBackStack()
        }
    }

    private val editWaitingKMObserver = Observer<Int> {
        viewModel.getAllGtinDB(navArg.taskInfo.id)
    }

    private val countNotVerifiedTaskGtinKMLiveData = Observer<WaitingGtinInfo?>{
        if (it?.waitingGtinCount !=null && it.kmModelCountKM !=null){
            if (it.differenceKM != 0 && it.waitingGtinCount < it.totalKM!!) {
                val dbKm = it.waitingGtinCount + 1
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

    }

    private val successCheckKMLiveDataObserver = Observer<CheckKMResponse?> {
        it?.rows?.map { kmInfo ->
            kmInfo?.kmsSold?.let { kmList ->
                if (kmList.isNotEmpty()){
                    val gtin = kmList[0]?.substring(2,16)
                    viewModel2.changeWaitingKMCount(kmList.size, gtin, navArg.taskInfo.id)
                    for (i in kmList.indices){
                        viewModel2.kmChangeStatusScannedVerified(kmList[i])
                        if (i == kmList.lastIndex) {
                            page = 0
                            taskItemList.clear()
                            viewModel.taskItemList(navArg.taskInfo.id, page++, pageSize)
                        }
                    }
                }
            }
        }
    }

    private val changeWaitingKMCountObserver = Observer<ChangeWaitingGtinCountInfo?>{
        it?.waitingKM?.let {waitingKM ->
            val resultKMCount = waitingKM- it.countSuccessKM!!
            Log.d("BBBB", "resultKMCount: $resultKMCount  waitingKM: ${waitingKM} countSuccessKM: ${it.countSuccessKM}")
            viewModel.editWaitingKM(resultKMCount, it.gtin, it.taskId)
        }

    }



    private val progressObserver2 = Observer<Boolean> {
        countDownTimer = object :CountDownTimer(3000L, 1000L){
            override fun onTick(p0: Long) {
                binding.horizontalProgress.isIndeterminate = true
            }

            override fun onFinish() {
                binding.horizontalProgress.isIndeterminate = false

            }

        }.start()

    }

    private val existKMLiveDataObserver = Observer<ExistsKMInfo> {
        if (it.exist == 0) {
            try {
                viewModel2.allTaskGtinKM(it.km?.substring(2, 16), navArg.taskInfo.id, it)
            } catch (e: Exception) {
                e.printStackTrace()
            }

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
        findNavController().popBackStack()

    }

    private val failedServerKMListObserver = Observer<List<String?>?> {
        viewModel2.checkKMFromServer(
            it!!,
            navArg.taskInfo.id
        )
    }

    private val taskStatusObserver = Observer<String?> {
        when (it) {
            TaskStatus.PROCESS.name -> {
                viewModel.failedServerKMList(navArg.taskInfo.id)
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



    private val errorMessageObserver2 = Observer<String> {
        if (it == getString(R.string.unauthorised)) {
            findNavController().navigate(getString(R.string.unauthorised))
        }

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
        if (it == getString(R.string.unauthorised)) {
            findNavController().navigate(R.id.PINCodeScreen)
        }
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