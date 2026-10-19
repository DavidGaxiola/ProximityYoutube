package com.example.proximityyoutube

import android.app.Activity
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import kotlin.math.min

class MainActivity : Activity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private lateinit var statusText: TextView
    private lateinit var toggleButton: Button
    private var listening = false
    private var wasNear = false
    private var lastLaunchTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val title = TextView(this).apply {
            text = "Proximity YouTube"
            textSize = 26f
            gravity = Gravity.CENTER
        }
        statusText = TextView(this).apply {
            textSize = 17f
            gravity = Gravity.CENTER
            setPadding(0, 28, 0, 28)
        }
        toggleButton = Button(this).apply {
            textSize = 16f
            setOnClickListener { if (listening) stopListening() else startListening() }
        }

        layout.addView(title)
        layout.addView(statusText)
        layout.addView(toggleButton)
        setContentView(layout)

        if (proximitySensor == null) {
            statusText.text = "Este dispositivo no informa un sensor de proximidad."
            toggleButton.isEnabled = false
        } else {
            statusText.text = "Pulsa Iniciar y acerca la mano al sensor, normalmente en la parte superior del teléfono."
            updateButton()
        }
    }

    private fun startListening() {
        val sensor = proximitySensor ?: return
        listening = sensorManager.registerListener(
            this, sensor, SensorManager.SENSOR_DELAY_NORMAL
        )
        if (listening) {
            wasNear = false
            statusText.text = "Detección activa. Acerca la mano al sensor."
        } else {
            statusText.text = "No se pudo iniciar la lectura del sensor."
        }
        updateButton()
    }

    private fun stopListening() {
        sensorManager.unregisterListener(this)
        listening = false
        wasNear = false
        statusText.text = "Detección detenida."
        updateButton()
    }

    private fun updateButton() {
        toggleButton.text = if (listening) "Detener detección" else "Iniciar detección"
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (!listening || event.sensor.type != Sensor.TYPE_PROXIMITY) return

        val distance = event.values.firstOrNull() ?: return
        val maxRange = event.sensor.maximumRange
        val near = distance < min(maxRange, 5f)

        // Open only on the transition from far to near, with a cooldown.
        if (near && !wasNear) {
            val now = System.currentTimeMillis()
            if (now - lastLaunchTime > 2500L) {
                lastLaunchTime = now
                openYouTube()
            }
        }
        wasNear = near
    }

    private fun openYouTube() {
        val launchIntent = packageManager.getLaunchIntentForPackage("com.google.android.youtube")
        try {
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(launchIntent)
            } else {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com")))
            }
        } catch (_: Exception) {
            Toast.makeText(this, "No se pudo abrir YouTube.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onPause() {
        super.onPause()
        // Android typically pauses sensor delivery when the app leaves the foreground.
        sensorManager.unregisterListener(this)
        listening = false
        wasNear = false
        if (proximitySensor != null) {
            statusText.text = "Detección pausada. Vuelve a la app para activarla."
            updateButton()
        }
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
}
