package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class OrbitComponent : Component, Pool.Poolable {

    var clockwise: Boolean = true

    var originX: Float = 0f
    var originY: Float = 0f
    var angle: Float = 0f
    var speed: Float = 0f
    var radius: Float = 0f

    fun setOrigin(originX: Float, originY: Float) {
        this.originX = originX
        this.originY = originY
    }

    override fun reset() {
        clockwise = true
        originX = 0f
        originY = 0f
        angle = 0f
        speed = 0f
        radius = 0f
    }

}