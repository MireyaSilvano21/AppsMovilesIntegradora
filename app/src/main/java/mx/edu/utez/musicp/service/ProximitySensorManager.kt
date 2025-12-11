package mx.edu.utez.musicp.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProximitySensorManager(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    private val _isNear = MutableStateFlow(false)
    val isNear: StateFlow<Boolean> = _isNear

    private var lastProximityState = false
    private var isRegistered = false

    fun startListening() {
        if (proximitySensor != null && !isRegistered) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)
            isRegistered = true
        }
    }

    fun stopListening() {
        if (isRegistered) {
            sensorManager.unregisterListener(this)
            isRegistered = false
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_PROXIMITY) {
                val isCurrentlyNear = it.values[0] < it.sensor.maximumRange

                if (isCurrentlyNear != lastProximityState) {
                    _isNear.value = isCurrentlyNear
                    lastProximityState = isCurrentlyNear
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No necesitamos manejar cambios de precisión
    }

    fun hasProximitySensor(): Boolean {
        return proximitySensor != null
    }
}