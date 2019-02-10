package com.symbol.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PlayerComponent : Component, Pool.Poolable {

    var canDoubleJump: Boolean = false
    var canShoot: Boolean = true

    override fun reset() {
        canDoubleJump = false
        canShoot = true
    }

}