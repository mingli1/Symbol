package com.symbol.game.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class HealthComponent : Component, Pool.Poolable {

    var hp = 0
    var maxHp = 0
    var hpDelta = 0
    var hpChange = false

    fun hit(damage: Int) {
        hp -= damage
        hpDelta = if (damage > maxHp) maxHp else damage
        hpChange = true
    }

    override fun reset() {
        hp = 0
        maxHp = 0
        hpDelta = 0
        hpChange = false
    }

}