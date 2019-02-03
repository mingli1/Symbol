package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PreviousPositionComponent : Component, Pool.Poolable {

    var x: Float = 0f
    var y: Float = 0f

    fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    override fun reset() {
        x = 0f
        y = 0f
    }
}