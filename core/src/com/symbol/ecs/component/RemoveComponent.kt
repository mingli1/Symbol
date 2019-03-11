package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class RemoveComponent : Component, Pool.Poolable {

    var shouldRemove: Boolean = false

    override fun reset() {
        shouldRemove = false
    }

}