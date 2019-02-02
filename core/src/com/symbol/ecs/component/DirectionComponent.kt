package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class DirectionComponent : Component, Pool.Poolable {

    var facingRight: Boolean = true

    override fun reset() {
        facingRight = true
    }

}