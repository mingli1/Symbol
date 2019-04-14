package com.symbol.game.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class MovingPlatformComponent : Component, Pool.Poolable {

    var distance = 0f
    var positive = true

    override fun reset() {
        distance = 0f
        positive = true
    }

}