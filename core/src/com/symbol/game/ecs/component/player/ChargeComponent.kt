package com.symbol.game.ecs.component.player

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.symbol.game.ecs.entity.PLAYER_CHARGE_THRESHOLD

class ChargeComponent : Component, Pool.Poolable {

    var charge = 0
    var chargeChange = false
    var chargeDelta = 0

    fun getChargeIndex() = (charge.toFloat() / PLAYER_CHARGE_THRESHOLD).toInt()

    override fun reset() {
        charge = 0
        chargeChange = false
        chargeDelta = 0
    }
}