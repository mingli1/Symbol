package com.symbol.game.ecs.component.enemy

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class CorporealComponent : Component, Pool.Poolable {

    var corporeal = true
    var incorporealTime = 0f

    override fun reset() {
        corporeal = true
        incorporealTime = 0f
    }

}