package com.symbol.game.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class DamageBoostComponent : Component, Pool.Poolable {

    var damageBoost = 0
    var duration = 0f

    override fun reset() {
        damageBoost = 0
        duration = 0f
    }

}