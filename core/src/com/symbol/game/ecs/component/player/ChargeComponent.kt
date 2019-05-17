package com.symbol.game.ecs.component.player

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ChargeComponent : Component, Pool.Poolable {

    var charge = 0
    var chargeChange = false
    var chargeDelta = 0

    fun getChargeIndex(threshold: Int) = (charge.toFloat() / threshold).toInt()

    override fun reset() {
        charge = 0
        chargeChange = false
        chargeDelta = 0
    }
}