package ru.viktorgezz.fallingobjectsgame.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import ru.viktorgezz.fallingobjectsgame.R

class GameOverActivity : AppCompatActivity() {
    private lateinit var gameOverSound: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_over)

        gameOverSound = MediaPlayer.create(this, R.raw.gameover)
        gameOverSound.start()

        // Получаем количество монет из Intent
        val coinCount = intent.getIntExtra("coins", 0)

        // Отображаем количество монет
        val scoreTextView = findViewById<TextView>(R.id.score_text)
        scoreTextView.text = "Вы собрали: $coinCount монет"

        // Кнопка "Начать заново"
        val restartButton = findViewById<Button>(R.id.restart_button)
        restartButton.setOnClickListener {
            // Запускаем MainActivity для перезапуска игры
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish() // Завершаем GameOverActivity
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameOverSound.release()
    }
}