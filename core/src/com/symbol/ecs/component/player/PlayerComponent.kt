package com.symbol.ecs.component.player

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PlayerComponent : Component, Pool.Poolable {

    var canDoubleJump: Boolean = false

    override fun reset() {
        canDoubleJump = false
    }

}