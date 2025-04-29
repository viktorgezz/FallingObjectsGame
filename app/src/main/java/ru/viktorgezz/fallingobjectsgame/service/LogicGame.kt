package ru.viktorgezz.fallingobjectsgame.service

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import ru.viktorgezz.fallingobjectsgame.model.FallingObject
import kotlin.math.max
import kotlin.random.Random

class LogicGame(private val gameView: GameView, context: Context) {

    private val character: Character = Character(context, 200f, 200f)
    private val objects: MutableList<FallingObject> = mutableListOf()

    private var isRunning = false
    private var gameThread: Thread? = null

    private var missedCount = 0
    private var coinCount = 0
    private var speedBoost = 1f
    private var speedBoostTime = 0L
    private var slowDownTime = 0L
    private var happyEndTime = 0L
    private var spawnRate = 1.5f
    private var nextSpawnTime = 0L

    private val soundManager = SoundManager(context)
    private val objectPaint = Paint().apply { color = Color.RED }
    private val powerUpPaint = Paint().apply { color = Color.GREEN }

    fun startGameLoop() {
        if (gameThread == null || !gameThread!!.isAlive) {
            isRunning = true
            nextSpawnTime = System.currentTimeMillis() + (spawnRate * 1000).toLong()
            gameThread = Thread {
                while (isRunning) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime >= nextSpawnTime) {
                        spawnObject()
                        nextSpawnTime = currentTime + (spawnRate * 1000).toLong()
                        spawnRate = max(0.4f, spawnRate - 0.04f)
                    }
                    update()
                    gameView.drawGame()
                    try {
                        Thread.sleep(16)
                    } catch (e: InterruptedException) {
                        Log.e("LogicGame", "Game thread interrupted", e)
                    }
                }
            }
            gameThread!!.start()
        }
    }

    fun stopGame() {
        isRunning = false
        gameThread?.join()
        gameThread = null
        soundManager.release()
    }

    fun setDimensions(width: Int, height: Int) {
        character.x = (width / 2) - (character.width / 2)
        character.y = height - character.height - 20f
    }

    fun updateCharacterPosition(tilt: Float) {
        character.x += tilt * 10 * speedBoost
        clampCharacterPosition()
    }

    private fun update() {
        updateObjects()
        updateSpeedBoost()
        updateHappyState()
    }

    private fun updateObjects() {
        val iterator = objects.iterator()
        while (iterator.hasNext()) {
            val obj = iterator.next()
            moveObject(obj)
            if (checkCollision(obj)) {
                handleCollision(iterator, obj)
            } else if (obj.y > gameView.height) {
                handleMissedObject(iterator)
            }
        }
    }

    private fun moveObject(obj: FallingObject) {
        obj.y += obj.speed
    }

    private fun checkCollision(obj: FallingObject): Boolean {
        return obj.y + obj.size > character.y &&
                obj.x + obj.size > character.x &&
                obj.x < character.x + character.width
    }

    private fun handleCollision(iterator: MutableIterator<FallingObject>, obj: FallingObject) {
        iterator.remove()
        if (obj.isPowerUp) {
            activateSpeedBoost()
        } else {
            collectCoin()
        }
    }

    private fun collectCoin() {
        coinCount++
        character.isHappy = true
        happyEndTime = System.currentTimeMillis() + 500
        soundManager.playCoinSound()
        if (Random.nextInt(100) < 5) {
            activateSlowDown()
        }
    }

    private fun activateSpeedBoost() {
        speedBoost = 2f
        speedBoostTime = System.currentTimeMillis() + 5000
    }

    private fun activateSlowDown() {
        speedBoost = 0.5f
        slowDownTime = System.currentTimeMillis() + 5000
    }

    private fun handleMissedObject(iterator: MutableIterator<FallingObject>) {
        iterator.remove()
        missedCount++
        if (missedCount >= 3) {
            endGame()
        }
    }

    private fun endGame() {
        isRunning = false
        gameView.onGameOver(coinCount)
    }

    private fun updateSpeedBoost() {
        val currentTime = System.currentTimeMillis()
        if (slowDownTime > currentTime) return
        if (speedBoostTime < currentTime) speedBoost = 1f
    }

    private fun updateHappyState() {
        if (character.isHappy && happyEndTime < System.currentTimeMillis()) {
            character.isHappy = false
        }
    }

    private fun clampCharacterPosition() {
        if (character.x < 0) character.x = 0f
        if (character.x + character.width > gameView.width) character.x = gameView.width - character.width
    }

    private fun spawnObject() {
        val x = Random.nextFloat() * (gameView.width - 50f)
        val speed = Random.nextFloat() * (10f - 3f) + 3f
        val isPowerUp = Random.nextInt(10) == 0
        val paint = if (isPowerUp) powerUpPaint else objectPaint
        val newObject = FallingObject(x, 0f, speed, 50f, paint, isPowerUp)
        objects.add(newObject)
    }

    fun getCharacter(): Character = character
    fun getObjects(): List<FallingObject> = objects
    fun getCoinCount(): Int = coinCount
    fun getMissedCount(): Int = missedCount
}