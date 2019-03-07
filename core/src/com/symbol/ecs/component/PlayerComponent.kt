package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PlayerComponent : Component, Pool.Poolable {

    var canJump: Boolean = false
    var canDoubleJump: Boolean = false
    var canShoot: Boolean = true
    var hasJumpBoost: Boolean = false

    override fun reset() {
        canJump = false
        canDoubleJump = false
        canShoot = true
        hasJumpBoost = false
    }

}