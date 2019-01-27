package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Pool

data class BoundingBoxComponent(var rect: Rectangle) : Component, Pool.Poolable {
    override fun reset() {
        rect.set(0f, 0f, 0f, 0f)
    }
}