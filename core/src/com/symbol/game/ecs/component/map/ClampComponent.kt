package com.symbol.game.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Pool

class ClampComponent : Component, Pool.Poolable {

    var right = false
    var rect = Rectangle()
    var acceleration = 0f
    var backVelocity = 0f
    var clamping = true

    override fun reset() {
        right = false
        rect.set(0f, 0f, 0f, 0f)
        acceleration = 0f
        backVelocity = 0f
        clamping = true
    }

}