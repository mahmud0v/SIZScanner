package uz.duol.sizscanner.presentation.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import uz.duol.sizscanner.R
import uz.duol.sizscanner.databinding.HomeScreenBinding

@AndroidEntryPoint
class HomeScreen : Fragment(R.layout.home_screen){
    private val binding by viewBinding(HomeScreenBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.taskMenu.setOnClickListener {
            findNavController().navigate(HomeScreenDirections.actionHomeScreenToScannerScreen())
        }

    }


}