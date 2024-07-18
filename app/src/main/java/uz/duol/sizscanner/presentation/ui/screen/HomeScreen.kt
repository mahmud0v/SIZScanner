package uz.duol.sizscanner.presentation.ui.screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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
import uz.duol.sizscanner.utils.backPressDispatcher
import uz.duol.sizscanner.utils.snackBar


@AndroidEntryPoint
class HomeScreen : Fragment(R.layout.home_screen){
    private val binding by viewBinding(HomeScreenBinding::bind)
    private val viewModel : NewTaskListViewModel by viewModels<NewTaskLisViewModelImpl>()
    private val navArgs by navArgs<HomeScreenArgs>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.userText.text = "${navArgs.userInfo?.username?:""} ${navArgs.userInfo?.lastName?:""}"

        binding.taskMenu.setOnClickListener {
            findNavController().navigate(HomeScreenDirections.actionHomeScreenToTasksScreen())
        }

        binding.logOut.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.log_out_title))
                .setMessage(getString(R.string.log_out_message))
                .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss()}
                .setPositiveButton(getString(R.string.yes)) {_,_ -> viewModel.logout()}
                .create()

            dialog.show()
        }

        viewModel.newTaskList(0,10)



        viewModel.totalSizeLiveData.observe(viewLifecycleOwner, totalSizeObserver)
        viewModel.errorMessageLiveData.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.logoutLiveData.observe(viewLifecycleOwner, logoutObserver)

    }

    private val totalSizeObserver = Observer<Int>{
        binding.taskSize.text = it.toString()
    }

    private val errorMessageObserver = Observer<String>{
        if (it == getString(R.string.unauthorised)){
            findNavController().navigate(R.id.PINCodeScreen)
        }
        snackBar(it)
    }

    private val logoutObserver = Observer<Unit>{
        Log.d("LLLL", "logout ")
        requireActivity().supportFragmentManager.popBackStack(
            "Screen",
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
        requireActivity().finish()
    }
}