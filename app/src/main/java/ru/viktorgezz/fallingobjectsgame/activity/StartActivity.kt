package ru.viktorgezz.fallingobjectsgame.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ru.viktorgezz.fallingobjectsgame.R

class StartActivity : AppCompatActivity() {

    private lateinit var text1: TextView
    private lateinit var text2: TextView
    private lateinit var text3: TextView
    private lateinit var startButton: Button

    private var currentTextIndex = 0

    private lateinit var backgroundMusic: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // Находим элементы интерфейса
        text1 = findViewById(R.id.text1)
        text2 = findViewById(R.id.text2)
        text3 = findViewById(R.id.text3)
        startButton = findViewById(R.id.start_button)

        backgroundMusic = MediaPlayer.create(this, R.raw.began)
        backgroundMusic.isLooping = true // Зацикливаем музыку
        backgroundMusic.start()

        // Изначально показываем только первый текст, остальные скрыты
        text1.visibility = View.VISIBLE
        text2.visibility = View.GONE
        text3.visibility = View.GONE
        startButton.visibility = View.GONE

        // Обработчик нажатия на экран
        findViewById<View>(android.R.id.content).setOnClickListener {
            currentTextIndex++
            when (currentTextIndex) {
                1 -> {
                    text2.visibility = View.VISIBLE
                }
                2 -> {
                    text3.visibility = View.VISIBLE
                }
                3 -> {
                    startButton.visibility = View.VISIBLE
                }
            }
        }

        // Обработчик нажатия на кнопку "Начать игру"
        startButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Завершаем StartActivity
        }
    }

    override fun onPause() {
        super.onPause()
        // Приостанавливаем музыку, если активность уходит на паузу
        if (backgroundMusic.isPlaying) {
            backgroundMusic.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        // Возобновляем музыку, если активность возвращается
        backgroundMusic.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Освобождаем ресурсы MediaPlayer
        backgroundMusic.stop()
        backgroundMusic.release()
    }
}