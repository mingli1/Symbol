package com.symbol.game.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class InvertSwitchComponent : Component, Pool.Poolable {

    var toggle = false

    override fun reset() {
        toggle = false
    }

}