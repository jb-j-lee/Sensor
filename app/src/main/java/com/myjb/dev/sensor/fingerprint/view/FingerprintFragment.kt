package com.myjb.dev.sensor.fingerprint.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.hardware.fingerprint.FingerprintManagerCompat.*
import androidx.core.os.CancellationSignal
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.myjb.dev.sensor.R
import com.myjb.dev.sensor.databinding.FingerprintDialogBinding
import com.myjb.dev.sensor.fingerprint.viewmodel.FingerprintViewModel
import com.myjb.dev.sensor.fingerprint.viewmodel.Status

interface AuthCallback {
    fun onAuthenticated()
    fun onAuthMessage(message: String?)
    fun onAuthFinish()
    fun onResetMessage()
}

class FingerprintFragment : DialogFragment(), AuthCallback {

    class FingerprintHelper(context: Context, private val callback: AuthCallback) : AuthenticationCallback() {

        private val fingerprintManagerCompat = from(context)
        private val cancellationSignal: CancellationSignal = CancellationSignal()

        fun start() {
            if (!isHardwareDetected() || !hasEnrolledFingerprints()) {
                return
            }

            val cryptoObject: CryptoObject? = null
            fingerprintManagerCompat.authenticate(cryptoObject, 0, cancellationSignal, this, null)
        }

        fun stop() {
            if (cancellationSignal.isCanceled) {
                return
            }

            cancellationSignal.cancel()
        }

        override fun onAuthenticationError(errMsgId: Int, errString: CharSequence?) {
            if (cancellationSignal.isCanceled) {
                return
            }

            callback.onAuthMessage(errString.toString())
            stop()
            callback.onAuthFinish()
        }

        override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence?) {
            callback.onAuthMessage(helpString.toString())
            callback.onResetMessage()
        }

        override fun onAuthenticationFailed() {
            callback.onAuthMessage(null)
            callback.onResetMessage()
        }

        override fun onAuthenticationSucceeded(result: AuthenticationResult?) {
            callback.onAuthenticated()
            callback.onAuthFinish()
        }

        fun isHardwareDetected(): Boolean {
            return fingerprintManagerCompat.isHardwareDetected
        }

        fun hasEnrolledFingerprints(): Boolean {
            return fingerprintManagerCompat.hasEnrolledFingerprints()
        }
    }

    companion object {
        private const val DELAY_TIME: Long = 1000
    }

    private lateinit var binding: FingerprintDialogBinding
    private val viewModel by lazy { ViewModelProvider(this)[FingerprintViewModel::class.java] }

    private var fingerprintHelper: FingerprintHelper? = null
    private var handler = Handler(Looper.getMainLooper())

    private val runnable: Runnable = Runnable {
        viewModel.status.value = Status.NORMAL
        viewModel.message.value = getString(R.string.fingerprint_hint)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            binding = FingerprintDialogBinding.inflate(LayoutInflater.from(context))
            binding.viewmodel = viewModel
            binding.lifecycleOwner = this

            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.menu_fingerprint)

            fingerprintHelper = FingerprintHelper(requireContext(), this)

            when {
                fingerprintHelper?.isHardwareDetected() == false -> {
                    builder.setMessage(R.string.fingerprint_not_support)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            dismiss()
                        }
                    fingerprintHelper?.stop()
                }
                fingerprintHelper?.hasEnrolledFingerprints() == false -> {
                    builder.setMessage(R.string.fingerprint_not_registered)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            dismiss()
                        }
                    fingerprintHelper?.stop()
                }
                else -> {
                    builder.setView(binding.root)
                        .setNegativeButton(android.R.string.cancel) { _, _ ->
                            dismiss()
                        }
                }
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAuthenticated() {
        handler.removeCallbacks(runnable)

        viewModel.status.value = Status.SUCCESS
        viewModel.message.value = getString(R.string.fingerprint_success)
    }

    override fun onAuthMessage(message: String?) {
        viewModel.status.value = Status.ERROR
        viewModel.message.value = message ?: getString(R.string.fingerprint_not_recognized)
    }

    override fun onResetMessage() {
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, DELAY_TIME.times(1.5).toLong())
    }

    override fun onAuthFinish() {
        handler.postDelayed({ dismiss() }, DELAY_TIME)
    }

    override fun onResume() {
        super.onResume()
        fingerprintHelper?.start()
    }

    override fun onPause() {
        super.onPause()
        fingerprintHelper?.stop()
    }
}