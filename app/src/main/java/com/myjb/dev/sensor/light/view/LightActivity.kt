package com.myjb.dev.sensor.light.view

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.myjb.dev.sensor.R
import com.myjb.dev.sensor.databinding.LightActivityBinding

class LightActivity : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null
    private lateinit var fragment: LightFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: LightActivityBinding = DataBindingUtil.setContentView(this, R.layout.light_activity)
        binding.lifecycleOwner = this

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

        if (savedInstanceState == null) {
            fragment = LightFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow()
        } else {
            fragment = supportFragmentManager.fragments[0] as LightFragment
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LIGHT) {
            fragment.setLightIntensity(event.values[0])
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    fun update(progress: Int) {
        var convertedProgress = progress
        if (convertedProgress < 10) {
            convertedProgress = 10
        }

        val layoutParams: WindowManager.LayoutParams = window.attributes
        layoutParams.screenBrightness = convertedProgress.toFloat() / 100
        window.attributes = layoutParams
    }
}