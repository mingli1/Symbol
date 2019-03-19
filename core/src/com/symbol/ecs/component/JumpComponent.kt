package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class JumpComponent : Component, Pool.Poolable {

    var impulse = 0f

    override fun reset() {
        impulse = 0f
    }

}