package com.symbol.game.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class MovingPlatformComponent : Component, Pool.Poolable {

    var originX = 0f
    var originY = 0f
    var distance = 0f
    var positive = true

    override fun reset() {
        originX = 0f
        originY = 0f
        distance = 0f
        positive = true
    }

}