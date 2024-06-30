package uz.duol.sizscanner.presentation.ui.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.duol.sizscanner.R
import uz.duol.sizscanner.data.remote.response.TaskItemResponse
import uz.duol.sizscanner.databinding.ScanningProcessScreenBinding
import uz.duol.sizscanner.presentation.ui.adapter.TaskItemListAdapter
import uz.duol.sizscanner.presentation.ui.dialog.ScanningProductDialog
import uz.duol.sizscanner.presentation.viewmodel.task.TaskItemListViewModel
import uz.duol.sizscanner.presentation.viewmodel.task.TaskItemListViewModelImpl
import uz.duol.sizscanner.utils.gone
import uz.duol.sizscanner.utils.snackBar
import uz.duol.sizscanner.utils.visible


@AndroidEntryPoint
class ScanningProcessScreen : Fragment(R.layout.scanning_process_screen) {
    private val binding by viewBinding(ScanningProcessScreenBinding::bind)
    private val viewModel: TaskItemListViewModel by viewModels<TaskItemListViewModelImpl>()
    private val taskItemListAdapter by lazy { TaskItemListAdapter() }
    private val navArg: ScanningProcessScreenArgs by navArgs()
    private val taskItemList = ArrayList<TaskItemResponse>()
    private var page = 0
    private var pageSize = 10
    private var maxPage: Int = 1

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
            if (page<=maxPage-1)
                viewModel.taskItemList(navArg.taskInfo.id, page++, pageSize)

        }

        binding.swipeRefresh.setOnRefreshListener {
            page = 0
            taskItemList.clear()
            viewModel.taskItemList(navArg.taskInfo.id, page++, pageSize)

        }

        viewModel.taskItemListLiveData.observe(viewLifecycleOwner, taskItemListObserver)
        viewModel.pageSizeLiveData.observe(viewLifecycleOwner, pageSizeObserver)
        viewModel.errorMessageLiveData.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.progressLoadingLiveData.observe(viewLifecycleOwner, progressObserver)



        binding.beginBtn.setOnClickListener {
            val scanningDialog = ScanningProductDialog()
            scanningDialog.show(requireActivity().supportFragmentManager, "Dialog")
            scanningDialog.closeIconClick = {
                page = 0
                taskItemList.clear()
                viewModel.taskItemList(navArg.taskInfo.id, page++, pageSize)
                scanningDialog.dismiss()
            }
            scanningDialog.isCancelable = false
        }



    }

    @SuppressLint("NotifyDataSetChanged")
    private val taskItemListObserver = Observer<List<TaskItemResponse>?>{
        binding.swipeRefresh.isRefreshing = false
        it?.let {
            taskItemList.addAll(it)
            Log.d("DDD", "task item list: $taskItemList")
            taskItemListAdapter.differ.submitList(taskItemList)
            taskItemListAdapter.notifyDataSetChanged()

        }

        if (taskItemList.isEmpty() && taskItemListAdapter.differ.currentList.isEmpty()) {
            binding.llEmptyBackground.visible()
        } else {
            binding.llEmptyBackground.gone()
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

    private val progressObserver = Observer<Boolean>{
        if (it){
            binding.progress.visible()
        }else {
            binding.progress.gone()
        }
    }

}