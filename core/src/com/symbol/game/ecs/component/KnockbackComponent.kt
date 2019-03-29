package com.symbol.game.ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class KnockbackComponent : Component, Pool.Poolable {

    var knockingBack = false
    var timer = 0f

    override fun reset() {
        knockingBack = false
        timer = 0f
    }

}