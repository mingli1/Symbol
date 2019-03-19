package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class VelocityComponent : Component, Pool.Poolable {

    var dx = 0f
    var dy = 0f

    var speed = 0f

    var platformDx = 0f

    fun set(dx: Float, dy: Float) {
        this.dx = dx
        this.dy = dy
    }

    override fun reset() {
        dx = 0f
        dy = 0f
        speed = 0f
        platformDx = 0f
    }

    fun move(right: Boolean) = if (right) dx = speed else dx = -speed

}