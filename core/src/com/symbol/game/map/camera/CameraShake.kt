package com.symbol.game.map.camera

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3

object CameraShake {

    var time = 0f
        private set
    var toggle = false
        private set

    private var currentTime = 0f
    private var power = 0f
    private var currentPower = 0f

    val position = Vector3()

    fun shake(power: Float) {
        this.power = power
        toggle = true
    }

    fun shakeFor(power: Float, duration: Float) {
        this.power = power
        time = duration
    }

    fun stop() {
        toggle = false
    }

    fun update(dt: Float) {
        if (toggle) {
            currentPower = power
            applyShake()
        }
        else {
            if (currentTime < time) {
                currentPower = power * ((time - currentTime) / time)
                applyShake()
                currentTime += dt
            }
            else {
                time = 0f
                currentTime = 0f
            }
        }
    }

    private fun applyShake() {
        position.x = (MathUtils.random() - 0.5f) * 2 * currentPower
        position.y = (MathUtils.random() - 0.5f) * 2 * currentPower
    }

}