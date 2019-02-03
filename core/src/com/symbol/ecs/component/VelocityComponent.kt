package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class VelocityComponent : Component, Pool.Poolable {

    var dx: Float = 0f
    var dy: Float = 0f

    fun set(dx: Float, dy: Float) {
        this.dx = dx
        this.dy = dy
    }

    override fun reset() {
        dx = 0f
        dy = 0f
    }

    fun move(right: Boolean, speed: Float) = if (right) dx = speed else dx = -speed

}