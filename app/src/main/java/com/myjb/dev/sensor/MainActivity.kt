package com.myjb.dev.sensor

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.myjb.dev.sensor.biometric.view.BiometricFragment
import com.myjb.dev.sensor.databinding.MainActivityBinding
import com.myjb.dev.sensor.fingerprint.view.FingerprintFragment
import com.myjb.dev.sensor.light.view.LightActivity
import com.myjb.dev.sensor.step.view.StepActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: MainActivityBinding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        binding.lifecycleOwner = this
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_light -> {
                startActivity(Intent(this, LightActivity::class.java))
                true
            }
            R.id.action_step -> {
                startActivity(Intent(this, StepActivity::class.java))
                true
            }
            R.id.action_fingerprint -> {
                FingerprintFragment().show(supportFragmentManager, "fingerprint")
                true
            }
            R.id.action_biometric -> {
                val fragment = BiometricFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commitNow()
                true
            }
            else -> {
                finish()
                false
            }
        }
    }
}