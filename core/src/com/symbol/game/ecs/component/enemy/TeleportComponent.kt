package com.symbol.game.ecs.component.enemy

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class TeleportComponent : Component, Pool.Poolable {

    var range = 0f
    var pos = 0
    var freq = 0f

    override fun reset() {
        range = 0f
        pos = 0
        freq = 0f
    }
}