package ru.viktorgezz.fallingobjectsgame.service

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import ru.viktorgezz.fallingobjectsgame.activity.GameOverActivity
import ru.viktorgezz.fallingobjectsgame.activity.MainActivity
import ru.viktorgezz.fallingobjectsgame.model.FallingObject

class GameView(
    context: Context,
    attrs: AttributeSet? = null
) : SurfaceView(context, attrs),
    SurfaceHolder.Callback {

    private lateinit var logicGame: LogicGame

    private val textPaint = Paint().apply {
        textSize = 50f
        color = Color.BLACK
        setShadowLayer(2f, 2f, 2f, Color.GRAY)
    }

    private val livesPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }

    private val lifeCircleRadius = 20f
    private val lifeCircleSpacing = 10f

    init {
        holder.addCallback(this)
        logicGame = LogicGame(this, context)
    }

    fun onGameOver(coinCount: Int) {
        Handler(Looper.getMainLooper()).post {
            val intent = Intent(context, GameOverActivity::class.java)
            intent.putExtra("coins", coinCount)
            context.startActivity(intent)
            (context as? MainActivity)?.finish()
        }
    }

    fun updateCharacterPosition(tilt: Float) {
        logicGame.updateCharacterPosition(tilt)
    }

    fun drawGame() {
        val canvas = holder.lockCanvas() ?: return
        try {
            clearCanvas(canvas)
            drawCharacter(canvas, logicGame.getCharacter())
            drawObjects(canvas, logicGame.getObjects())
            drawUI(canvas, logicGame.getCoinCount(), logicGame.getMissedCount())
        } finally {
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun clearCanvas(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
    }

    private fun drawCharacter(canvas: Canvas, character: Character) {
        val bitmap = character.getCurrentBitmap()
        canvas.drawBitmap(
            bitmap,
            null,
            RectF(
                character.x,
                character.y,
                character.x + character.width,
                character.y + character.height
            ),
            null
        )
    }

    private fun drawObjects(canvas: Canvas, objects: List<FallingObject>) {
        for (obj in objects) {
            canvas.drawRect(obj.x, obj.y, obj.x + obj.size, obj.y + obj.size, obj.paint)
        }
    }

    private fun drawUI(canvas: Canvas, coinCount: Int, missedCount: Int) {
        canvas.drawText("Монеты: $coinCount", 20f, 180f, textPaint)
        val livesRemaining = 3 - missedCount
        for (i in 0 until livesRemaining) {
            val circleX = width - 20f - (i * (lifeCircleRadius * 2 + lifeCircleSpacing))
            val circleY = 160f
            canvas.drawCircle(circleX, circleY, lifeCircleRadius, livesPaint)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        logicGame.setDimensions(width, height)
        logicGame.startGameLoop()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        logicGame.stopGame()
    }
}
