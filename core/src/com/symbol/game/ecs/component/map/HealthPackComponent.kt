package com.symbol.game.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class HealthPackComponent : Component, Pool.Poolable {

    var regen = 0
    var regenTime = 0f

    override fun reset() {
        regen = 0
        regenTime = 0f
    }

}