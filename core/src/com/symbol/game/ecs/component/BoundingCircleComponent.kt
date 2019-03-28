package com.symbol.game.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.utils.Pool

class BoundingCircleComponent : Component, Pool.Poolable {

    var circle = Circle()

    override fun reset() {
        circle.set(0f, 0f, 0f)
    }

}