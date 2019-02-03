package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Pool

class GravityComponent : Component, Pool.Poolable {

    var onGround: Boolean = false
    var platform: Rectangle = Rectangle()

    override fun reset() {
        onGround = false
        platform.set(0f, 0f, 0f, 0f)
    }
}