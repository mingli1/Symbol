package com.symbol.game.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.symbol.game.ecs.entity.PLAYER_CHARGE_THRESHOLD

class PlayerComponent : Component, Pool.Poolable {

    var dead = false

    var charge = 0
    var damageBoost = 0

    var canJump = false
    var canDoubleJump = false
    var canShoot = true
    var hasJumpBoost = false

    var startHealing = false
    var healing = 0
    var healTime = 0f

    fun getChargeIndex() = (charge.toFloat() / PLAYER_CHARGE_THRESHOLD).toInt()

    override fun reset() {
        dead = false
        charge = 0
        damageBoost = 0
        canJump = false
        canDoubleJump = false
        canShoot = true
        hasJumpBoost = false
        startHealing = false
        healing = 0
        healTime = 0f
    }

}