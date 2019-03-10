package com.symbol.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Pool

class ClampComponent : Component, Pool.Poolable {

    var right: Boolean = false
    var rect: Rectangle = Rectangle()
    var acceleration: Float = 0f
    var backVelocity: Float = 0f
    var clamping: Boolean = true

    override fun reset() {
        right = false
        rect.set(0f, 0f, 0f, 0f)
        acceleration = 0f
        backVelocity = 0f
        clamping = true
    }

}