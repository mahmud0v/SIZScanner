package uz.duol.sizscanner.presentation.ui.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.duol.sizscanner.R
import uz.duol.sizscanner.databinding.HomeScreenBinding
import uz.duol.sizscanner.presentation.viewmodel.task.NewTaskLisViewModelImpl
import uz.duol.sizscanner.presentation.viewmodel.task.NewTaskListViewModel


@AndroidEntryPoint
class HomeScreen : Fragment(R.layout.home_screen){
    private val binding by viewBinding(HomeScreenBinding::bind)
    private val viewModel : NewTaskListViewModel by viewModels<NewTaskLisViewModelImpl>()
    private val navArgs by navArgs<HomeScreenArgs>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.userText.text = "${navArgs.userInfo?.firstName?:""} ${navArgs.userInfo?.lastName?:""}"

        binding.taskMenu.setOnClickListener {
            findNavController().navigate(HomeScreenDirections.actionHomeScreenToTasksScreen())
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.newTaskList(0,10)

        viewModel.totalSizeLiveData.observe(viewLifecycleOwner, totalSizeObserver)
        viewModel.errorMessageLiveData.observe(viewLifecycleOwner, errorMessageObserver)

    }

    private val totalSizeObserver = Observer<Int>{
        binding.taskSize.text = it.toString()
    }

    private val errorMessageObserver = Observer<String>{
        if (it == getString(R.string.unauthorised)){
            findNavController().navigate(R.id.PINCodeScreen)
        }
    }

}