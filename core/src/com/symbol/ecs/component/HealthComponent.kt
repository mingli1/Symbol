package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class HealthComponent : Component, Pool.Poolable {

    var hp: Int = 0
    var maxHp: Int = 0
    var hpDelta: Int = 0
    var hpChange: Boolean = false

    override fun reset() {
        hp = 0
        maxHp = 0
        hpDelta = 0
        hpChange = false
    }

}