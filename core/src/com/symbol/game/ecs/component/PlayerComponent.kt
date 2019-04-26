package com.symbol.game.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PlayerComponent : Component, Pool.Poolable {

    var dead = false

    var damage = 0
    var damageBoost = 0

    var canJump = false
    var canDoubleJump = false
    var canShoot = true
    var hasJumpBoost = false

    var chargeTime = 0f
    var chargeIndex = 1

    var startHealing = false
    var healing = 0
    var healTime = 0f

    override fun reset() {
        dead = false
        damage = 0
        damageBoost = 0
        canJump = false
        canDoubleJump = false
        canShoot = true
        hasJumpBoost = false
        chargeTime = 0f
        chargeIndex = 1
        startHealing = false
        healing = 0
        healTime = 0f
    }

}