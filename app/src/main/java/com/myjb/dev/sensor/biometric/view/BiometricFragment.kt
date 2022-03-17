package com.myjb.dev.sensor.biometric.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import com.myjb.dev.sensor.R
import com.myjb.dev.sensor.databinding.BiometricFragmentBinding
import java.util.concurrent.Executor

class BiometricFragment : Fragment() {

    companion object {
        fun newInstance() = BiometricFragment()
    }

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = BiometricFragmentBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this

        val biometricManager = BiometricManager.from(requireContext())
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                show()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Snackbar.make(binding.root, R.string.biometric_not_support, LENGTH_INDEFINITE).setAction(android.R.string.ok) { removeFragment() }
                    .show()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Snackbar.make(binding.root, R.string.biometric_unavailable, LENGTH_INDEFINITE).setAction(android.R.string.ok) { removeFragment() }
                    .show()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                    )
                }
                val intent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                    if (result.resultCode == RESULT_OK) {
                        show()
                    } else {
                        removeFragment()
                    }
                }
                intent.launch(enrollIntent)
            }
            else -> {
                Snackbar.make(binding.root, android.R.string.unknownName, LENGTH_INDEFINITE).setAction(android.R.string.ok) { removeFragment() }
                    .show()
            }
        }

        return binding.root
    }

    private fun show() {
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == 10) {
                        Toast.makeText(context, "[onAuthenticationError] cancel", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "[onAuthenticationError] error : $errString", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(context, "[onAuthenticationSucceeded]", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(context, "[onAuthenticationFailed]", Toast.LENGTH_SHORT).show()
                }
            })

        val builder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.menu_biometric))
            .setSubtitle(getString(R.string.biometric_description))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            builder.setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        } else {
            builder.setNegativeButtonText(getString(android.R.string.cancel))
        }

        promptInfo = builder.build()
        biometricPrompt.authenticate(promptInfo)
    }

    private fun removeFragment() {
        parentFragmentManager.beginTransaction().remove(this).commitNow()
    }
}