package com.symbol.game.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class RemoveComponent : Component, Pool.Poolable {

    var shouldRemove = false

    override fun reset() {
        shouldRemove = false
    }

}