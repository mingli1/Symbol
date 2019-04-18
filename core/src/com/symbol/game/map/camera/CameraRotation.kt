package com.symbol.game.map.camera

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Interpolation
import kotlin.math.min

object CameraRotation {

    private lateinit var cam: OrthographicCamera
    private val interpolation = Interpolation.linear

    private var startRotation = 0f
    private var rotationAmount = 0f
    private var totalRotation = 0f

    private var time = 0f
    private var totalTime = 0f

    private var ended = false

    fun init(cam: OrthographicCamera) {
        this.cam = cam
    }

    fun update(dt: Float) {
        if (!ended) {
            time += dt
            val alpha = min(1f, time / totalTime)

            rotate(interpolation.apply(startRotation, rotationAmount, alpha) - totalRotation)

            if (alpha == 1f) end()
        }
    }

    fun start(degrees: Float, totalTime: Float) {
        startRotation = 0f
        rotationAmount = degrees
        this.totalTime = totalTime
        ended = false
    }

    fun end() {
        ended = true
        time = 0f
        totalRotation = 0f
    }

    fun isEnded() = ended

    private fun rotate(degrees: Float) {
        cam.rotate(degrees)
        totalRotation += degrees
    }

}