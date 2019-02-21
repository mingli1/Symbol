package com.symbol.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class MovingPlatformComponent : Component, Pool.Poolable {

    var originX: Float = 0f
    var originY: Float = 0f
    var distance: Float = 0f
    var positive: Boolean = true

    override fun reset() {
        originX = 0f
        originY = 0f
        distance = 0f
        positive = true
    }

}