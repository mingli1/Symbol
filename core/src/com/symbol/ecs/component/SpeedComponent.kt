package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class SpeedComponent : Component, Pool.Poolable {

    var speed: Float = 0f

    override fun reset() {
        speed = 0f
    }
}