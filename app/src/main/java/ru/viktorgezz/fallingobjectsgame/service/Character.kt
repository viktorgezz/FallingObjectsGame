package ru.viktorgezz.fallingobjectsgame.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import ru.viktorgezz.fallingobjectsgame.R

class Character(context: Context, val width: Float, val height: Float) {

    var x: Float = 0f
    var y: Float = 0f
    var isHappy: Boolean = false

    private val standBitmap: Bitmap
    private val happyBitmap: Bitmap

    init {
        standBitmap = loadScaledBitmap(context, R.drawable.person_stand)
        happyBitmap = loadScaledBitmap(context, R.drawable.person_happy)
    }

    private fun loadScaledBitmap(context: Context, resourceId: Int): Bitmap {
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
        if (bitmap == null) {
            Log.e("Character", "Failed to load bitmap for resource ID: $resourceId")
            throw IllegalStateException("Bitmap loading failed")
        }
        return Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), true)
    }

    fun getCurrentBitmap(): Bitmap {
        return if (isHappy) happyBitmap else standBitmap
    }
}