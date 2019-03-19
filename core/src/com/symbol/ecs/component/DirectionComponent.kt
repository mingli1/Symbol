package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class DirectionComponent : Component, Pool.Poolable {

    var facingRight = true
    var facingUp = true
    var yFlip = false

    override fun reset() {
        facingRight = true
        facingUp = true
        yFlip = false
    }

}

enum class Direction {

    Up,
    Down,
    Left,
    Right

}