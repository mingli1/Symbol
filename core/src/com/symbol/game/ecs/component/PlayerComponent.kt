package com.symbol.game.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PlayerComponent : Component, Pool.Poolable {

    var dead = false

    var damageBoost = 0

    var canJump = false
    var canDoubleJump = false
    var canShoot = true
    var hasJumpBoost = false

    var startHealing = false
    var healing = 0
    var healTime = 0f

    override fun reset() {
        dead = false
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