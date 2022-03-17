package com.myjb.dev.sensor.step.view

import android.Manifest.permission.ACTIVITY_RECOGNITION
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.myjb.dev.sensor.R
import com.myjb.dev.sensor.databinding.StepActivityBinding

class StepActivity : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var stepCounterSensor: Sensor? = null
    private var stepDetectorSensor: Sensor? = null

    private lateinit var fragment: StepFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: StepActivityBinding = DataBindingUtil.setContentView(this, R.layout.step_activity)
        binding.lifecycleOwner = this

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)
            || !packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)
        ) {
            finish()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && checkSelfPermission(ACTIVITY_RECOGNITION) == PERMISSION_DENIED) {
            requestPermissions(arrayOf(ACTIVITY_RECOGNITION), 12345)
            return
        }

        init(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?) {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepDetectorSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

        if (savedInstanceState == null) {
            fragment = StepFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow()
        } else {
            fragment = supportFragmentManager.fragments[0] as StepFragment
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager?.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this, stepCounterSensor)
        sensorManager?.unregisterListener(this, stepDetectorSensor)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_STEP_COUNTER -> {
                fragment.updateCounter(event.values[0].toInt())
            }
            Sensor.TYPE_STEP_DETECTOR -> {
                fragment.updateDetector(event.values[0].toInt())
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 12345 && permissions[0] == ACTIVITY_RECOGNITION) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                init(null)
            } else {
                finish()
            }
        } else {
            finish()
        }
    }
}