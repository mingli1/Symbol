package com.symbol.game.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class JumpComponent : Component, Pool.Poolable {

    var impulse = 0f
    var timer = 0f

    override fun reset() {
        impulse = 0f
        timer = 0f
    }

}