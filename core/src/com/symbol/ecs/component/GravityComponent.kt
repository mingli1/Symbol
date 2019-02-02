package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class GravityComponent : Component, Pool.Poolable {

    var onGround: Boolean = false

    override fun reset() {
        onGround = false
    }
}