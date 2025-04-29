package ru.viktorgezz.fallingobjectsgame.model

import android.graphics.Paint
import kotlin.random.Random

data class FallingObject(
    var x: Float,
    var y: Float,
    val speed: Float,
    val size: Float,
    val paint: Paint,
    val isPowerUp: Boolean = Random.nextInt(10) == 0 // 10% шанс
)