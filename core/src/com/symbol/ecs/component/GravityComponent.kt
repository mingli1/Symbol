package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Pool
import com.symbol.ecs.system.GRAVITY
import com.symbol.ecs.system.TERMINAL_VELOCITY

class GravityComponent : Component, Pool.Poolable {

    var onGround: Boolean = false
    var onMovingPlatform: Boolean = false
    var platform: Rectangle = Rectangle()
    var collidable: Boolean = true

    var gravity: Float = GRAVITY
    var terminalVelocity: Float = TERMINAL_VELOCITY

    override fun reset() {
        onGround = false
        onMovingPlatform = false
        collidable = true
        platform.set(0f, 0f, 0f, 0f)
        gravity = GRAVITY
        terminalVelocity = TERMINAL_VELOCITY
    }
}