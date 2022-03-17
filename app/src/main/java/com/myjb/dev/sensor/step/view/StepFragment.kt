package com.myjb.dev.sensor.step.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.myjb.dev.sensor.databinding.StepFragmentBinding
import com.myjb.dev.sensor.step.viewmodel.StepViewModel

class StepFragment : Fragment() {

    companion object {
        fun newInstance() = StepFragment()
    }

    private val viewModel by lazy { ViewModelProvider(this)[StepViewModel::class.java] }
    private lateinit var binding: StepFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = StepFragmentBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    fun updateCounter(value: Int) {
        viewModel.counter.value = value
    }

    fun updateDetector(value: Int) {
        viewModel.detector.value = value
    }
}