package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Pool
import com.symbol.ecs.system.GRAVITY
import com.symbol.ecs.system.TERMINAL_VELOCITY

class GravityComponent : Component, Pool.Poolable {

    var onGround = false
    var onMovingPlatform = false
    var platform = Rectangle()
    var collidable = true

    var gravity = GRAVITY
    var terminalVelocity = TERMINAL_VELOCITY

    var reverse = false

    override fun reset() {
        onGround = false
        onMovingPlatform = false
        collidable = true
        platform.set(0f, 0f, 0f, 0f)
        gravity = GRAVITY
        terminalVelocity = TERMINAL_VELOCITY
        reverse = false
    }
}