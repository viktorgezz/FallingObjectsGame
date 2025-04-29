package ru.viktorgezz.fallingobjectsgame.activity

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import ru.viktorgezz.fallingobjectsgame.R
import ru.viktorgezz.fallingobjectsgame.service.GameView

class MainActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var gameController: GameView
    private lateinit var backgroundMusic: MediaPlayer

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val xTilt = event.values[0]
            gameController.updateCharacterPosition(-xTilt)
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate started")

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        gameController = findViewById(R.id.gameController)
        Log.d("MainActivity", "GameView initialized: $gameController")

        backgroundMusic = MediaPlayer.create(this, R.raw.began)
        backgroundMusic.isLooping = true
        backgroundMusic.start()
        Log.d("MainActivity", "Background music started")
        manageSensor()
        Log.d("MainActivity", "onCreate finished")
    }

    private fun manageSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer == null) {
            Log.e("MainActivity", "Accelerometer not available on this device")
            return
        }
        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorListener)
        if (backgroundMusic.isPlaying) {
            backgroundMusic.pause()
            Log.d("MainActivity", "Background music paused")
        }
    }

    override fun onResume() {
        super.onResume()
        manageSensor()
        if (!backgroundMusic.isPlaying) {
            backgroundMusic.start()
            Log.d("MainActivity", "Background music resumed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        backgroundMusic.release()
        Log.d("MainActivity", "Background music released")
    }
}