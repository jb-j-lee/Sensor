package com.myjb.dev.sensor.light.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.myjb.dev.sensor.databinding.LightFragmentBinding
import com.myjb.dev.sensor.light.viewmodel.LightViewModel

class LightFragment : Fragment() {

    companion object {
        fun newInstance() = LightFragment()
    }

    private val viewModel by lazy { ViewModelProvider(this)[LightViewModel::class.java] }
    private lateinit var binding: LightFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LightFragmentBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                (activity as LightActivity).update(progress)

                viewModel.brightness.value = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        return binding.root
    }

    fun setLightIntensity(value: Float) {
        viewModel.lux.value = value
    }
}