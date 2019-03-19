package com.symbol.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class HealthPackComponent : Component, Pool.Poolable {

    var regen = 0

    override fun reset() {
        regen = 0
    }

}