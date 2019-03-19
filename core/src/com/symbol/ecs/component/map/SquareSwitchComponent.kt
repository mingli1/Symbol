package com.symbol.ecs.component.map

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class SquareSwitchComponent : Component, Pool.Poolable {

    var targetId = 0
    var toggle = true

    override fun reset() {
        targetId = 0
        toggle = true
    }

}