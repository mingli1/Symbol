package com.symbol.game.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PlayerComponent : Component, Pool.Poolable {

    var canJump = false
    var canDoubleJump = false
    var canShoot = true
    var hasJumpBoost = false

    override fun reset() {
        canJump = false
        canDoubleJump = false
        canShoot = true
        hasJumpBoost = false
    }

}