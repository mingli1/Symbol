package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class DirectionComponent : Component, Pool.Poolable {

    var facingRight: Boolean = true
    var facingUp: Boolean = true
    var yFlip: Boolean = false

    override fun reset() {
        facingRight = true
        facingUp = true
        yFlip = false
    }

}