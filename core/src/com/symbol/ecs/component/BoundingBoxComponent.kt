package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Pool

class BoundingBoxComponent : Component, Pool.Poolable {

    var rect = Rectangle(0f, 0f, 0f, 0f)

    override fun reset() {
        rect.set(0f, 0f, 0f, 0f)
    }
}