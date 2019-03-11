package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PositionComponent : Component, Pool.Poolable {

    var x: Float = 0f
    var y: Float = 0f

    var prevX: Float = 0f
    var prevY: Float = 0f

    fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun setPrev(prevX: Float, prevY: Float) {
        this.prevX = prevX
        this.prevY = prevY
    }

    override fun reset() {
        x = 0f
        y = 0f
        prevX = 0f
        prevY = 0f
    }
}