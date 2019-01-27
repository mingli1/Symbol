package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

data class PreviousPositionComponent(var x: Float, var y: Float) : Component, Pool.Poolable {
    override fun reset() {
        x = 0f
        y = 0f
    }
}