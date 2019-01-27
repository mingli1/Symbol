package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

data class SpeedComponent(var speed: Float = 0f) : Component, Pool.Poolable {
    override fun reset() {
        speed = 0f
    }
}