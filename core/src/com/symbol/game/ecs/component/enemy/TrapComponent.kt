package com.symbol.game.ecs.component.enemy

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class TrapComponent : Component, Pool.Poolable {

    var countdown = false
    var hits = 0
    var timer = 0f

    override fun reset() {
        countdown = false
        hits = 0
        timer = 0f
    }

}