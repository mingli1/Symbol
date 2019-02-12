package com.symbol.map.camera

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3

object CameraShake {

    var time: Float = 0f
        private set

    private var currentTime: Float = 0f
    private var power: Float = 0f
    private var currentPower: Float = 0f

    val position: Vector3 = Vector3()

    fun shake(power: Float, duration: Float) {
        this.power = power
        time = duration
    }

    fun update(dt: Float) {
        if (currentTime <= time) {
            currentPower = power * ((time - currentTime) / time)

            position.x = (MathUtils.random() - 0.5f) * 2 * currentPower
            position.y = (MathUtils.random() - 0.5f) * 2 * currentPower

            currentTime += dt
        }
        else {
            time = 0f
        }
    }

}