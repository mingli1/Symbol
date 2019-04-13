package com.symbol.game.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool

class LastEntityComponent : Component, Pool.Poolable {

    var entity: Entity? = null

    override fun reset() {
        entity = null
    }
}