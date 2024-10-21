package uz.duol.sizscanner.presentation.ui.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import uz.duol.sizscanner.R
import uz.duol.sizscanner.data.remote.response.TaskResponse
import uz.duol.sizscanner.databinding.TasksScreenBinding
import uz.duol.sizscanner.presentation.ui.adapter.TasksListAdapter
import uz.duol.sizscanner.presentation.viewmodel.task.NewTaskLisViewModelImpl
import uz.duol.sizscanner.presentation.viewmodel.task.NewTaskListViewModel
import uz.duol.sizscanner.utils.TaskType
import uz.duol.sizscanner.utils.gone
import uz.duol.sizscanner.utils.snackBar
import uz.duol.sizscanner.utils.visible

@AndroidEntryPoint
class TasksScreen : Fragment(R.layout.tasks_screen) {
    private val binding by viewBinding(TasksScreenBinding::bind)
    private val viewModel: NewTaskListViewModel by viewModels<NewTaskLisViewModelImpl>()
    private val taskAdapter by lazy { TasksListAdapter() }
    private val salesTaskList = ArrayList<TaskResponse>()
    private val returnTaskList = ArrayList<TaskResponse>()
    private var page = 0
    private var pageSize = 10
    private var maxPage: Int = 1


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.rv.adapter = taskAdapter
        binding.rv.layoutManager = LinearLayoutManager(requireContext())

        taskAdapter.clickItem = {
            findNavController().navigate(TasksScreenDirections.actionTasksScreenToTaskItemsScreen(it))
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
            salesTaskList.clear()
            returnTaskList.clear()
        }


        page = 0
        salesTaskList.clear()
        returnTaskList.clear()
        viewModel.newTaskList(page++, pageSize)

        taskAdapter.setLoader {
            if (page <= maxPage) //
                viewModel.newTaskList(page++, pageSize)
        }

        binding.swipeRefresh.setOnRefreshListener {
            page = 0
            salesTaskList.clear()
            returnTaskList.clear()
            viewModel.newTaskList(page++, pageSize)

        }

        binding.tabLayoutInvoice.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                when (p0?.position) {
                    0 -> {
                        taskAdapter.differ.submitList(salesTaskList)
                        if (salesTaskList.isEmpty()){
                            binding.llEmptyBackground.visible()
                        }else {
                            binding.llEmptyBackground.gone()
                        }
                    }
                    1 -> {
                        taskAdapter.differ.submitList(returnTaskList)
                        if (returnTaskList.isEmpty()){
                            binding.llEmptyBackground.visible()
                        }else {
                            binding.llEmptyBackground.gone()
                        }
                    }
                }

                taskAdapter.notifyDataSetChanged()

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

        })

        viewModel.newTaskListLiveData.observe(viewLifecycleOwner, newTaskListObserver)
        viewModel.pageSizeLiveData.observe(viewLifecycleOwner, pageSizeObserver)
        viewModel.errorMessageLiveData.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.progressLoadingLiveData.observe(viewLifecycleOwner, progressObserver)


    }

    @SuppressLint("NotifyDataSetChanged")
    private val newTaskListObserver = Observer<List<TaskResponse>?> {
        it?.let {
            it.map { taskResponse ->
                if (taskResponse.type == TaskType.DEBIT_NOTE) {
                    returnTaskList.add(taskResponse)
                } else {
                    salesTaskList.add(taskResponse)
                }
            }
        }
        if (binding.tabLayoutInvoice.selectedTabPosition == 0){
            taskAdapter.differ.submitList(salesTaskList)
            taskAdapter.notifyDataSetChanged()
            if (salesTaskList.isEmpty()){
                binding.llEmptyBackground.visible()
            }else {
                binding.llEmptyBackground.gone()
            }
        }else {
            taskAdapter.differ.submitList(returnTaskList)
            taskAdapter.notifyDataSetChanged()
            if (returnTaskList.isEmpty()){
                binding.llEmptyBackground.visible()
            }else {
                binding.llEmptyBackground.gone()
            }
        }

        binding.swipeRefresh.isRefreshing = false


    }

    @SuppressLint("NotifyDataSetChanged")
    private val errorMessageObserver = Observer<String> {
        binding.swipeRefresh.isRefreshing = false
        if (it == getString(R.string.unauthorised)) {
            findNavController().navigate(R.id.PINCodeScreen)
        }
        snackBar(it)
        if (binding.tabLayoutInvoice.selectedTabPosition == 0) {
            if (salesTaskList.isEmpty()) {
                taskAdapter.notifyDataSetChanged()
                binding.llEmptyBackground.visible()
            } else {
                binding.llEmptyBackground.gone()
            }
        } else {
            if (returnTaskList.isEmpty()) {
                taskAdapter.notifyDataSetChanged()
                binding.llEmptyBackground.visible()
            } else {
                binding.llEmptyBackground.gone()
            }
        }

    }

    private val pageSizeObserver = Observer<Int> {
        maxPage = it
    }


    private val progressObserver = Observer<Boolean> {
        if (it) {
            binding.progress.visible()
        } else {
            binding.progress.gone()
        }
    }


}